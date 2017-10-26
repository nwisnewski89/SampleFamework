package zeta.utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import zeta.pages.ZetaCMS;

/**
 * 
 * @author nwisnewski
 *
 */
@Test
public class ZetaTestDriver {
	public static String suiteReportPath;
	protected static String runUrl;
	protected static String baseDocDef;
	protected static String pageTemplate;
	protected static String baseDoc;
	protected static String testImage;
	public static String project;
	protected static String imageSrc;
	protected TestSettings runInfo = new TestSettings();
	
	@BeforeSuite
	public void purgeData() throws IOException{
		//Wipes out data in the results column of data sheet
		DataManager.getData().purgeSuiteRunResults();
	}
	
	@Parameters({"project", "environment", "runBrowser", "OS", "runHost"})
	@BeforeTest(alwaysRun = true)
	public void beforeSuite(ITestContext context, String project,  String environment, String runBrowser, String OS, String runHost) throws IOException {
		//sets retry analyzer for all methods the suite will run 
		if(!runHost.equalsIgnoreCase("local")){
  			for (ITestNGMethod method : context.getAllTestMethods()) {
  		         method.setRetryAnalyzer(new ReRun());
  		     }
		}
		runUrl = getURL(project, environment);
		//sets the various variables particular to a project as global strings
		setProjectVariables(project);
		//creates directoy where detailed html reports will appear
		suiteReportPath = createSuiteResultPath(project+"_"+OS+"_"+runBrowser);
	}
	
	@Parameters({"runBrowser", "OS", "runHost"})
	@BeforeMethod(alwaysRun = true)
	public void setUpTestRun(String runBrowser, String OS, String runHost, Method test) throws Exception{
		//Get a new instance of the manager class
		RunManager driver = RunInstance.getInstance();
		//Set the manager in RunFactory
		RunFactory.setRunManager(driver);
		//Get the web driver 
		if(runHost.equalsIgnoreCase("local")){
			setLocalBrowser(test.getName(), runBrowser, OS);
		}else if(runHost.equalsIgnoreCase("browserstack")){
			setBrowserStack(test.getName(), runBrowser, OS);
		}else{
			setGridBrowser(test.getName(), runBrowser, OS);
		}
		//Initializes the extent report object in run manager, and sets threadlocal in RunFactory
		RunFactory.setExtentReport(RunFactory.getRunManager().initializeReporter(test.getName()));
		//Starts the extent test object in run manager, and sets threadlocal in RunFactory
		RunFactory.setExtentTest(RunFactory.getRunManager().startReport(test.getName(), RunFactory.getExtentReport()));
		//Sets zeta report with extent test and web driver, and sets threadlocal in RunFactory
		RunFactory.setReport(new Report(RunFactory.getExtentTest(),RunFactory.getBrowser()));
		System.out.println(RunFactory.getRunDetails().nodeIp());
		RunFactory.getReport().infoStep("Opening '"+RunFactory.getRunDetails().runBrowser()+"' browser on '"+RunFactory.getRunDetails().nodeIp()+"' with '"+
				RunFactory.getRunDetails().runOS()+"' OS.");
		//Opens browser, sets the threadlocal login object in the RunFactory
		new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()).openZeta(runUrl);
		//Gets login object from RunFactory, logs in
		RunFactory.thisLogin().logIn(runInfo.getAdminUserName(), runInfo.getAdminPassWord());
	}
	
	@Parameters({"runHost", "runBrowser", "OS"})
	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result, String runHost, String runBrowser, String OS) throws Exception{
		//Only run flush test on passes, because flushTest is called in onSkip/onFail methods in listener
		if(result.isSuccess()){
			RunFactory.flushTest();
		}
		if(runHost.equalsIgnoreCase("local")){
			try {
				switch(runBrowser){
					case "ie":
						Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					    Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
					    break;
					case "firefox":
						Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
						Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
						break;
					case "chrome":
						Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
						Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
						break;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
		resultsToReportDirectory(result.getMethod().getMethodName());
	}
	
	/*
	 * Enters in the homepage url again, this helps resolve issues navigating from one section 
	 * to another and having the browser die
	 */
	protected void resetZetaCms() throws Exception{
		new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()).openZeta(runUrl);
		new WebDriverWait(RunFactory.getBrowser(), 90).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(ZetaCMS.DashBoardLinks)));
	}
	/*
	 * quits the web driver to open a new session and log in as
	 * testingacct_02, used only in role management test methods
	 */
	protected void loginAsRestrictedUser() throws Exception{
		String host = RunFactory.getRunDetails().nodeIp();
		String runBrowser = RunFactory.getRunDetails().runBrowser();
		String os = RunFactory.getRunDetails().runOS();
		String testName = RunFactory.getRunDetails().testName();
		RunFactory.flushForRestart();
		if(host.equalsIgnoreCase("local")){
			setLocalBrowser(testName, runBrowser, os);
		}else if(host.equalsIgnoreCase("browserstack")){
			setBrowserStack(testName, runBrowser, os);
		}else{
			setGridBrowser(testName, runBrowser, os);
		}
		RunFactory.setReport(new Report(RunFactory.getExtentTest(),RunFactory.getBrowser()));
		System.out.println(RunFactory.getRunDetails().nodeIp());
		RunFactory.getReport().infoStep("Opening '"+runBrowser+"' browser on '"+RunFactory.getRunDetails().nodeIp()+"' with '"+os+"' OS.");
		new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()).openZeta(runUrl);
		RunFactory.thisLogin().logIn(runInfo.getUserName(), runInfo.getPassWord());
	}
	/*
	 * Closed tab with opened document
	 */
	protected void closeOpenDoc() throws InterruptedException{
		ArrayList<String> windows = new ArrayList<String>(RunFactory.getBrowser().getWindowHandles());
		RunFactory.getBrowser().close();
		RunFactory.getBrowser().switchTo().window(windows.get(0));
		Thread.sleep(500);
	}
	/*
	 * Sets the local web driver for local runs
	 */
	private void setLocalBrowser(String testName, String runBrowser, String os){
		//Sets the web driver  in RunFactory, by calling the run manager getDriver method
		RunFactory.setBrowser(RunFactory.getRunManager().getDriver(os, runBrowser, testName));
		//Sets the rundetails object in RunFactory
		RunFactory.setRunDetails(new RunDetails(testName, "local", os, runBrowser));
	}
	/*
	 * Starts a selenium grid session based runBowser/OS input
	 */
	private void setGridBrowser(String testName, String runBrowser, String os) throws Exception{
		//Sets the remote web driver  in RunFactory, by calling the run manager remoteDriver method
		RunFactory.setRemoteBrowser(RunFactory.getRunManager().remoteDriver(os, runBrowser, testName));
		//Sets rundetails object with the session id of the remote web driver to get the run host
		RunFactory.setRunDetails(RunFactory.getRunManager().setRunDetails(testName, runBrowser, os, RunFactory.getRemoteBrowser()));
		//Sets the remote web driver as the web driver in RunFactory
		RunFactory.setBrowser(RunFactory.getRemoteBrowser());
		//Removes the remote driver to not waste memory
		RunFactory.flushRemoteBrowser();
	}
	/*
	 * Starts a browsers grid session based on selecting config file entry with same
	 * name as the runBrowser parameter in the testNG xml
	 */
	private void setBrowserStack(String testName, String environment, String configFile) throws Exception{
		RunFactory.setBrowser(RunFactory.getRunManager().startBrowserStack(configFile, environment));
		String os = RunFactory.getRunManager().getOsDetails();
		String  runBrowser = RunFactory.getRunManager().getBrowserDetails();
		RunFactory.setRunDetails(new RunDetails(testName, "BrowserStack", os, runBrowser));
	}
	/**
	 * Start of setting project specific variables
	 */
	private String getURL(String project, String environment){
		String url = "";
		switch(project.toLowerCase()){
			case "zeta1(labs)":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://dev-demo.qa.zeta.comcast.net:8010";
						break;
					}
				break;
			case "corporate":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://corpnxt-cms.g.comcast.com";
						break;
					case "prod":
						url = "http://corp-cms.g.comcast.com";
						break;
					}
				break;
			case "labs":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://qa-labs-cms.g.comcast.com";
						break;
					case "prod":
						url = "http://labs-cms.g.comcast.com";
						break;
					}
				break;
			case "dni":
				switch(environment.toLowerCase()){
				case "qa":
					url = "http://dni-test.qa.zeta.comcast.net:8010";
					break;
				case "prod":
					url = "dni-cms-lb01.g.comcast.net:8010";
					break;
				}
			break;
			case "nextgen":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://96.119.246.131:8010";
						break;
					case "prod":
						url = "http://nextgen-cms.comcast.com";
						break;
					}
				break;
			case "zeta":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://demo-env01.zeta.comcast.net:8010";
						break;
					case "prod":
						url = "http://zeta-cms.g.comcast.net";
						break;
					}
				break;
			case "campus application":
				switch(environment.toLowerCase()){
					case "qa":
						url = "http://cmpapp-qa.zeta.comcast.net:8010";
						break;
				}
				break;
		}
		return url;
	}
	
	private void setProjectVariables(String inproject){
		project = inproject;
		baseDocDef = getBaseDocDef(project);
		pageTemplate = getPageTemplate(project);
		baseDoc = getHomeDoc(project);
		imageSrc = imageSrc(project);
		testImage = image(project);
	}
	
	private String imageSrc(String project){
		String imageSrc = "";
		switch(project.toLowerCase()){
			case "demo":
				imageSrc = "Black Comcast";
				break;
			case "labs":
				imageSrc = "labs_";
				break;
			case "corporate":
				imageSrc = "corp_";
				break;
			case "dni":
				imageSrc = "dni_";
				break;
			case "nextgen":
				imageSrc = "spot_";
				break;
			case "campus application":
				imageSrc = "demo_";
				break;
		}
		return imageSrc;
	}
	
	private String image(String project){
		String imageSrc = "";
		switch(project.toLowerCase()){
			case "zeta1(labs)":
				imageSrc = "Editorial-Image_Unlimited-Voice_International";
				break;
			case "labs":
				imageSrc = "Gigabit-Tech";
				break;
			case "corporate":
				imageSrc = "Jeff Shell";
				break;
			case "dni":
				imageSrc = "Test Image";
				break;
			case "nextgen":
				imageSrc = "comcast-logo-official_large";
				break;
			case "zeta":
				imageSrc = "Build-In-Dis-Desktop";
				break;
			case "campus application":
				imageSrc = "Security Hero";
				break;
		}
		return imageSrc;
	}
	
	private String getPageTemplate(String project){
		String template = "";
		switch(project.toLowerCase()){
			case "zeta1(labs)":
				template = "Black Comcast";
				break;
			case "labs":
				template = "Labs Default";
				break;
			case "corporate":
				template = "Corporate";
				break;
			case "dni":
				template = "Diversity";
				break;
			case "nextgen":
				template = "nextGen-Wrapper";
				break;
			case "zeta":
				template = "Zeta Hype";
				break;
			case "campus application":
				template = "campus-wrapper";
				break;
		}
		return template;
	}
	
	private String getBaseDocDef(String project){
			String doc = "";
			switch(project.toLowerCase()){
				case "zeta1(labs)":
					doc = "Demo Doc Def";
					break;
				case "labs":
					doc = "Labs Default";
					break;
				case "corporate":
					doc = "Executive Biographies";
					break;
				case "spot":
					doc = "HomeDocDef";
					break;
				case "nextgen":
					doc = "Home";
					break;
				case "zeta":
					doc = "Zeta Hype Default";
					break;
				case "dni":
					doc = "DI Screen Test";
					break;
			}
			return doc;
	}
	
	private String getHomeDoc(String project){
		String doc = "";
		switch(project.toLowerCase()){
			case "labs":
				doc = "Labs Home1";
				break;
			case "dni":
				doc = "2017";
				break;
			case "corporate":
				doc = "Landing Page";
				break;
			case "campus application":
				doc = "Explore";
				break;
			default:
				doc = "Home";
				break;
		}
		return doc;
	}
	/**
	 * End of setting project specific variables
	 */
	
	/**
	 *Creates directory to house all detailed html reports 
	 */
	private String createSuiteResultPath(String runBrowser){
		File suiteReportDir;
		String suiteReportPath = System.getProperty("user.dir")+File.separator+"Reports"+File.separator+runBrowser+"_Zeta";
		Path suiteReport = Paths.get(suiteReportPath);
		System.out.println(Files.exists(suiteReport));
		if(!Files.exists(suiteReport)){
			suiteReportDir=new File(suiteReportPath);
			suiteReportDir.mkdir();
		}	
		return suiteReportPath;
	}
	/**
	 *Because there was an issue with reports writing to just created directory, this
	 *method copies html report from placeholder directory to new suite run directory,
	 *then deletes the report in the place holder directory
	 */
	private void resultsToReportDirectory(String testName){
		String currentReportDir = System.getProperty("user.dir")+File.separator+"results"+File.separator+testName+".html";
		File original = new File(currentReportDir);
		File reportDest = new File(suiteReportPath+File.separator+testName+".html");
		try {
			FileUtils.copyFile(original, reportDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Files.delete(FileSystems.getDefault().getPath(currentReportDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @author nwisnewski
	 *immutable object with run details for multithreading
	 */
	public static class RunDetails{
		private final String testName;
		private final String nodeId;
		private final String runBrowser;
		private final String runOS;
		public RunDetails(String testName, String nodeIp, String runOS, String runBrowser){
			this.testName = testName;
			this.nodeId = nodeIp;
			this.runBrowser = runBrowser;
			this.runOS = runOS;
		}
		
		public String nodeIp(){
			return this.nodeId;
		}
		
		public String testName(){
			return this.testName;
		}
		
		public String runBrowser(){
			return this.runBrowser;
		}
		
		public String runOS(){
			return this.runOS;
		}
	}
	
	
}
