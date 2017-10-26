package zeta.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class LogInPage extends Page {
	
	public LogInPage(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	
	public ZetaCMS logIn(String user, String password) throws InterruptedException{
		WebDriverWait wait = new WebDriverWait(browser, 30);
		String signIn = "//button[text()='Sign in']";
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(signIn)));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("id", "username").sendKeys(user);
		tempElementBy("id", "password").sendKeys(password);
		clickElementBy("xpath", signIn);
		Thread.sleep(2000);
		//Initializes the zeta cms object with browser,report,and run details from RunFactory, sets zeta cms in RunFactory
		RunFactory.setZeta(new ZetaCMS(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
		}
		return RunFactory.getZeta();
	}
	
}
