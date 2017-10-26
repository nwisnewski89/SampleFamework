package zeta.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import zeta.utilities.Report;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class Assets extends Widgets{
	
	private static final String SaveAsset = "//div[contains(@id,'asset_content_navbar')]/button";

	public Assets(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser,report, threadRun);
	}
	/**
	 * Creates either a javascript of css asset.
	 * @param assetTitle
	 * @param assetFileExt- Fixed: css,js
	 * @return
	 * @throws Exception 
	 */
	public Assets createAsset(String assetTitle, String assetFileExt) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNew)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", CreateNew);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Properties.title")));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("name", PropTitle).sendKeys(assetTitle);
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name", PropTitle),assetTitle);
		Thread.sleep(250);
		tempElementBy("name","Properties.filename").sendKeys(assetTitle+"."+assetFileExt.toLowerCase());
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name","Properties.filename"),assetTitle+"."+assetFileExt);
		if(assetFileExt.equalsIgnoreCase("css")){
			selectDropDown(tempElementBy("name", "Properties.type"), "Stylesheet");
		}
		clickElementBy("css", submitWidgetOrAsset("asset"));
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(500);
		zetaSearch(assetTitle, EditAPW);
		Thread.sleep(500);
		report.runStep(isElementPresentBy("xpath",editWidgetOrAsset(assetTitle)), "New javascript asset  '"+assetTitle+"' was created.");
		Thread.sleep(500);
		clickElementBy("css",EditAPW);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
		}
		String assetCode = "textarea.form-control";
		tempElementBy("css",assetCode).clear();;
		tempElementBy("css",assetCode).sendKeys("Testing!");
		Thread.sleep(250);
		clickElementBy("xpath", SaveAsset);
		publishContent("asset", assetTitle);
		return this;
	}
	
}
