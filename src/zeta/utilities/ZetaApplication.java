package zeta.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import zeta.pages.LogInPage;
import zeta.pages.UIValidation;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class ZetaApplication {
	protected WebDriver browser;
	protected Report report;
	protected RunDetails threadRun;
	
	public ZetaApplication(WebDriver browser, Report report, RunDetails threadRun){
		this.browser = browser;
		this.report = report;
		this.threadRun = threadRun;
	}
	
	public LogInPage openZeta(String url){
		browser.get(url);
		report.infoStep("Url is "+url);
		//return new LogInPage(browser, report, threadRun);
		return RunFactory.threadSafeLogin(new LogInPage(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	
	public UIValidation openZetaDocumnet(String url){
		browser.get(url);
		report.infoStep("Url is "+url);
		//return new LogInPage(browser, report, threadRun);
		return RunFactory.threadSafeUi(new UIValidation(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
}
