package zeta.utilities;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.browserstack.local.Local;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import zeta.pages.ZetaCMS;
import zeta.utilities.ZetaTestDriver.RunDetails;

/**
 * @author nwisne002c
  * This class sets and constructs all objects needed for a test run
 */
public class RunManager {
	
	private String host = new TestSettings().getHub();
	private ThreadLocal<String> operatingSystem = new ThreadLocal<String>();
	private ThreadLocal<String> osVersion = new ThreadLocal<String>();
	private ThreadLocal<String> runBrowser = new ThreadLocal<String>();
	private ThreadLocal<String> browserVersion = new ThreadLocal<String>();
	private ThreadLocal<String> resolution = new ThreadLocal<String>();

	protected RemoteWebDriver remoteDriver(String OS, String runBrowser, String testName) throws MalformedURLException, InterruptedException{
		RemoteWebDriver driver = null;
		String capabilities = OS+"_"+runBrowser;
		DesiredCapabilities caps = null;
		Thread.sleep(7000);
		switch(capabilities.toLowerCase()){
			case "windows_chrome":
				caps = DesiredCapabilities.chrome();
				caps.setPlatform(Platform.WINDOWS);
				break;
			case "windows_ie":
				caps = DesiredCapabilities.internetExplorer();
				caps.setVersion("11");
				caps.setPlatform(Platform.WINDOWS);
				caps.setCapability("ACCEPT_SSL_CERTS", true);
				caps.setCapability("InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION", true);
				//Sessions seem to collide in windows ie, trying a wait to resolve the issue
				break;
			case "windows_firefox":
				caps = DesiredCapabilities.firefox();	
				caps.setCapability("marionette", true);
				caps.setPlatform(Platform.WINDOWS);
				//Sessions seem to collide in windows firefox, trying a wait to resolve the issue
				break;
			case "mac_safari":
				caps = DesiredCapabilities.safari();
				caps.setPlatform(Platform.MAC);
				break;
			case "mac_chrome":
				caps = DesiredCapabilities.chrome();
				caps.setPlatform(Platform.MAC);
				ChromeOptions options = new ChromeOptions();
		        options.addArguments("start-fullscreen");
		        caps.setCapability("chrome.binary", "drivers/chromedriver.exe");
		        caps.setCapability(ChromeOptions.CAPABILITY, options);
				break;
			case "mac_firefox":
				caps = DesiredCapabilities.firefox();
				caps.setCapability("marionette", true);
				caps.setPlatform(Platform.MAC);
		}
		driver = new RemoteWebDriver(new URL("http://"+host+":4444/wd/hub"), caps);
		if(!capabilities.equalsIgnoreCase("mac_chrome")){
			driver.manage().window().maximize();
		}
		return driver;
	}
	
	
	protected WebDriver getDriver(String OS, String browserName, String testName){
		WebDriver driver = null;
		switch(browserName.toLowerCase()){
			case "ie":
				System.setProperty("webdriver.ie.driver","drivers/IEDriverServer.exe");
				DesiredCapabilities capabilitiesIE = DesiredCapabilities.internetExplorer();
				capabilitiesIE.setCapability("ACCEPT_SSL_CERTS", true);
				capabilitiesIE.setCapability("InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION", true);
				driver=new InternetExplorerDriver(capabilitiesIE);
				break;
			case "firefox":
				System.setProperty("webdriver.gecko.driver","drivers/geckodriver.exe");
				DesiredCapabilities capabilitiesFF = DesiredCapabilities.firefox();
				FirefoxOptions options = new FirefoxOptions();		
				options.addPreference("log", "{level: trace}");		
				capabilitiesFF.setCapability("marionette", true);
				capabilitiesFF.setCapability("moz:firefoxOptions", options);
				driver = new FirefoxDriver(capabilitiesFF);
				break;
			case "chrome":
				System.setProperty("webdriver.chrome.driver","drivers/chromedriver.exe");
				driver = new ChromeDriver();
				break;
			}
		driver.manage().window().maximize();
		return driver;
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	protected WebDriver startBrowserStack(String config_file, String environment) throws Exception{
		JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir")+File.separator+"ConfigFiles"+File.separator+config_file));
        JSONObject envs = (JSONObject) config.get("environments");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey().toString());
            switch(pair.getKey().toString().toLowerCase()){
             case "os":	
            	 operatingSystem.set(pair.getValue().toString());
            	 break;
             case "os_version":	
            	 osVersion.set(pair.getValue().toString());
            	 break;
             case "browser":	
            	 runBrowser.set(pair.getValue().toString());
            	 break;
             case "browser_version":	
            	 browserVersion.set(pair.getValue().toString());
            	 break;
             case "resolution":	
            	 resolution.set(pair.getValue().toString());
            	 break;
            	 
            }
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }
		Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(capabilities.getCapability(pair.getKey().toString()) == null){
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }
        }
        TestSettings bStackCred = new TestSettings();
        String username = bStackCred.getBStackName();
        System.out.println(username);
        String accessKey = bStackCred.getBStackKey();
        System.out.println(accessKey);
        if(capabilities.getCapability("browserstack.local") != null && capabilities.getCapability("browserstack.local") == "true"){
            //l = new Local();
            RunFactory.setLocal(new Local());
            Map<String, String> options = new HashMap<String, String>();
            options.put("key", accessKey);
            RunFactory.getLocal().start(options);
        }
        return new RemoteWebDriver(new URL("http://"+username+":"+accessKey+"@"+config.get("server")+"/wd/hub"), capabilities);
	}
	
/*	protected void endLocal() throws Exception{
		if(!(l==null)){
			l.stop();
		}
	}*/
	
	protected ExtentReports initializeReporter(String testName){ 
		String reportName = testName+".html";
		ExtentReports runReport = new ExtentReports(System.getProperty("user.dir")+File.separator+"results"+File.separator+reportName, true);
		runReport.loadConfig(new File(System.getProperty("user.dir")+File.separator+"zetaReportConfig.xml"));
		return runReport;
	}
	
	protected ExtentTest startReport(String testName, ExtentReports runReport){
		return runReport.startTest(testName);
	}
	
	protected RunDetails setRunDetails(String testName, String runBrowser, String OS, RemoteWebDriver driver){
		String runNodeIP = new ActiveNodeDeterminer(host, 4444).getNodeInfoForSession(driver.getSessionId());
		return new RunDetails(testName, runNodeIP, OS, runBrowser);
	}
	
	protected String getOsDetails(){
		return operatingSystem.get()+" version "+osVersion.get();
	}
	
	protected String getBrowserDetails(){
		System.out.println(runBrowser.get()+" version "+browserVersion.get()+" resolution "+resolution.get());
		return runBrowser.get()+" version "+browserVersion.get()+" resolution "+resolution.get();
	}
}
