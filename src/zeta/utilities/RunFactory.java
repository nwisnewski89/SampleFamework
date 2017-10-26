package zeta.utilities;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.browserstack.local.Local;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import zeta.pages.Environment;
import zeta.pages.Assets;
import zeta.pages.Document;
import zeta.pages.DocumentDefinitions;
import zeta.pages.LogInPage;
import zeta.pages.Media;
import zeta.pages.People;
import zeta.pages.RoleManagement;
import zeta.pages.UIValidation;
import zeta.pages.Widgets;
import zeta.pages.ZetaCMS;
import zeta.utilities.ZetaTestDriver.RunDetails;
/**
 * 
 * @author nwisne002c
 *This class sets thread local instances of all objects needed for a test run.
 */
public class RunFactory {
	/**
	 * Run manager
	 */
	private static ThreadLocal<RunManager> manager = new ThreadLocal<RunManager>();
	/**
	 * POM constructors
	 */
	private static ThreadLocal<RemoteWebDriver> rwd = new ThreadLocal<RemoteWebDriver>();
	private static ThreadLocal<WebDriver> wd = new ThreadLocal<WebDriver>();
	private static ThreadLocal<Report> report = new ThreadLocal<Report>();
	private static ThreadLocal<RunDetails> runThread = new ThreadLocal<RunDetails>();
	/**
	 * POM objects
	 */
	private static ThreadLocal<ZetaCMS> zeta = new ThreadLocal<ZetaCMS>();
	private static ThreadLocal<LogInPage> login = new ThreadLocal<LogInPage>();
	private static ThreadLocal<Assets> assets = new ThreadLocal<Assets>();
	private static ThreadLocal<Document> doc = new ThreadLocal<Document>();
	private static ThreadLocal<DocumentDefinitions> docDef = new ThreadLocal<DocumentDefinitions>();
	private static ThreadLocal<Environment> environmnet = new ThreadLocal<Environment>();
	private static ThreadLocal<Media> media = new ThreadLocal<Media>();
	private static ThreadLocal<People> people = new ThreadLocal<People>();
	private static ThreadLocal<UIValidation> ui = new ThreadLocal<UIValidation>();
	private static ThreadLocal<Widgets> widget = new ThreadLocal<Widgets>();
	private static ThreadLocal<RoleManagement> roles = new ThreadLocal<RoleManagement>();
	/**
	 * Utility objects
	 */
	private static ThreadLocal<ExtentReports> runReport = new ThreadLocal<ExtentReports>();
	private static ThreadLocal<ExtentTest> runStep = new ThreadLocal<ExtentTest>();
	private static ThreadLocal<WebDriverWait> wait = new ThreadLocal<WebDriverWait>();
	private static ThreadLocal<Local> local = new ThreadLocal<Local>();
	
	public static RunManager getRunManager(){
		return manager.get();
	}
	
	public static void setRunManager(RunManager driverClass){
		manager.set(driverClass);
	}
	
	public static void setRemoteBrowser(RemoteWebDriver browser){
		rwd.set(browser);
	}
	
	public static RemoteWebDriver getRemoteBrowser(){
		return rwd.get();
	}
	
	public static void flushRemoteBrowser(){
		rwd.remove();
	}
	
	public static void setBrowser(WebDriver browser){
		wd.set(browser);
	}
	
	public static WebDriver getBrowser(){
		return wd.get();
	}
	
	public static void setExtentTest(ExtentTest runTest){
		runStep.set(runTest);
	}
	
	public static ExtentTest getExtentTest(){
		return runStep.get();
	}
	
	public static void setExtentReport(ExtentReports startReport){
		runReport.set(startReport);
	}
	
	public static ExtentReports getExtentReport(){
		return runReport.get();
	}
	
	public static void setReport(Report runReport){
		report.set(runReport);
	}
	
	public static Report getReport(){
		return report.get();
	}
	
	public static void setRunDetails(RunDetails thread){
		runThread.set(thread);
	}
	
	public static RunDetails getRunDetails(){
		return runThread.get();
	}
	
	public static void setZeta(ZetaCMS testZeta){
		zeta.set(testZeta);
	}
	
	public static ZetaCMS getZeta(){
		return zeta.get();
	}

	public static LogInPage threadSafeLogin(LogInPage logOn){
		if(login.get()==null){
			login.set(logOn);
		}
		return login.get();
	}
	public static LogInPage thisLogin(){
		return login.get();
	}
	
	public static Assets threadSafeAssets(Assets asset){
		if(assets.get()==null){
			assets.set(asset);
		}
		return assets.get();
	}
	
	public static Assets thisAsset(){
		return assets.get();
	}
	
	public static Document threadSafeDocument(Document myDoc){
		if(doc.get()==null){
			doc.set(myDoc);
		}
		return doc.get();
	}
	
	public static Document thisDocument(){
		return doc.get();
	}
	
	public static DocumentDefinitions threadSafeDocumentDefinitions(DocumentDefinitions myDocDef){
		if(docDef.get()==null){
			docDef.set(myDocDef);
		}
		return docDef.get();
	}
	
	public static DocumentDefinitions thisDocDef(){
		return docDef.get();
	}
	
	public static Environment threadSafeEnvironment(Environment environment){
		if(environmnet.get()==null){
			environmnet.set(environment);
		}
		return environmnet.get();
	}
	
	public static Environment thisEnvironment(){
		return environmnet.get();
	}
	
	public static Media threadSafeMedia(Media med){
		if(media.get()==null){
			media.set(med);
		}
		return media.get();
	}
	
	public static Media thisMedia(){
		return media.get();
	}
	
	public static People threadSafePeople(People peop){
		if(people.get()==null){
			people.set(peop);
		}
		return people.get();
	}
	
	public static People thisPeople(){
		return people.get();
	}
	
	public static UIValidation threadSafeUi(UIValidation myUi){
		ui.set(myUi);
		return ui.get();
	}
	
	public static UIValidation thisUI(){
		return ui.get();
	}
	
	public static Widgets threadSafeWidget(Widgets widg){
		if(widget.get()==null){
			widget.set(widg);
		}
		return widget.get();
	}
	
	public static Widgets thisWidgets (){
		return widget.get();
	}
	
	public static RoleManagement threadSafeRoles(RoleManagement rM){
		if(roles.get()==null){
			roles.set(rM);
		}
		return roles.get();
	}
	
	public static RoleManagement thisRoles(){
		return roles.get();
	}
	
	public static WebDriverWait setWait(){
		wait.set(new WebDriverWait(wd.get(), 60));
		return wait.get();
	}
	
	public static void setLocal(Local l){
		local.set(l);
	}
	
	public static Local getLocal(){
		return local.get();
	}
	/**
	 * Flushes all objects except extent report
	 */
	public static void flushForRestart(){
		if(wd.get()!=null){
			wd.get().quit();
		}
		wd.remove();
		if(runThread.get().nodeIp().equalsIgnoreCase("browserstack")){
			try {
				if(local.get()!=null){
					local.get().stop();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			local.remove();
		}
		zeta.remove();
		report.remove();
		runThread.remove();
		widget.remove();
		doc.remove();
		docDef.remove();
		assets.remove();
		login.remove();
		media.remove();
		environmnet.remove();
		people.remove();
		ui.remove();
		roles.remove();
	}
	/**
	 * Ends extent report and removes all thread objects
	 */
	public static void flushTest(){
		manager.remove();
		if(wd.get()!=null){
			wd.get().quit();
		}
		wd.remove();
		if(runThread.get().nodeIp().equalsIgnoreCase("browserstack")){
			try {
				if(local.get()!=null){
					local.get().stop();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			local.remove();
		}
		report.remove();
		runThread.remove();
		zeta.remove();
		runReport.get().endTest(runStep.get());
		runStep.remove();
		runReport.get().flush();
		runReport.remove();
		widget.remove();
		doc.remove();
		docDef.remove();
		assets.remove();
		login.remove();
		media.remove();
		environmnet.remove();
		people.remove();
		ui.remove();
		roles.remove();
	}
	
}

