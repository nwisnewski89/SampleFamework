package zeta.pages;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import zeta.utilities.Report;
import zeta.utilities.ZetaTestDriver.RunDetails;
//widget extends DocumentDefinitions because the UI is identical
public class Widgets extends DocumentDefinitions{
	//Title field for both Assets and Widgets
	//@FindBy(name = "Properties.title")
	//protected WebElement propTitle;
	
	protected static final String PropTitle =  "Properties.title";
	
	
	public Widgets(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	
	/**
	 * Creates a new widget
	 * @param widgetName - title of the new widget
	 * @return
	 * @throws Exception
	 */
	public Widgets createWidget(String widgetName) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNew)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", CreateNew);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(PropTitle)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", PropTitle).sendKeys(widgetName);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", PropTitle), widgetName);
		clickElementBy("css", submitWidgetOrAsset("widget"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(500);
		zetaSearch(widgetName, EditAPW);
		report.runStep(isElementPresentBy("xpath",editWidgetOrAsset(widgetName)), "New widget '"+widgetName+"' was created.");
		clickElementBy("css", EditAPW);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNewFieldBtn)));
		return this;
	}
	/**
	 * css selector of the widget or asset submit button
	 * @param page - Fixed: widget, asset
	 * @return
	 */
	protected String submitWidgetOrAsset(String page){
		return "a.btn.btn-default.zeta-btn-"+page+"-create";
	}
	/**
	 * css selector of edit icon for a widget or an asset
	 * @param title - title of widget or asset
	 * @return
	 */
	protected String editWidgetOrAsset(String title){
		return "//td[text()='"+title+"']/following-sibling::td/div/a[@role='button']";
	}
	
}
