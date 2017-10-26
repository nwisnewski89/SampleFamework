package zeta.pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import Client.Robotil;
import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
import zeta.utilities.ZetaTestDriver.RunDetails;

public abstract class Page {
	protected WebDriver browser;
	protected Report report;
	protected RunDetails threadRun;
	
	public static final String DashBoardLinks = "//dl[@class='dl-horizontal']/dt/a";
	
	public Page(WebDriver browser, Report report, RunDetails threadRun){
		this.browser = browser;
		this.report = report;
		this.threadRun = threadRun;
		PageFactory.initElements(browser, this);
	}
	
	/**
	 * @author nwisnewski 5/23/2017
	 * Clicks an element
	 * @param property - Fixed: id, xpath, css, classname, linktext, name, tagname
	 * @param identifier
	 */
	public void clickElementBy(String property, String identifier){
		WebElement click = null;
		switch(property.toLowerCase()){
		case "id":
			try {
				click = browser.findElement(By.id(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
		case "xpath":
			try {
				click = browser.findElement(By.xpath(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
		case "css":
			try {
				click = browser.findElement(By.cssSelector(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
		case "classname":
			try {
				click = browser.findElement(By.className(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
		case "linktext":
			try {
				click = browser.findElement(By.linkText(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
		case "name":
			try {
				click = browser.findElement(By.name(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());;
			}
			break;
		case "tagname":
			try {
				click = browser.findElement(By.tagName(identifier));
			} catch (Exception e1) {
				report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
				Assert.fail(e1.getMessage());
			}
			break;
	}
		try{
			Thread.sleep(2000);
			click.click();
		}catch(Exception e){
			report.infoStep("Unable to click element idenfied by "+property+" = '"+identifier+"':"+ e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * @author nwisnewski 5/23/2017
	 * Returns a temporary element
	 * @param property - Fixed: id, xpath, css, classname, linktext, name, tagname
	 * @param identifier
	 */
	public WebElement tempElementBy(String property, String identifier){
		WebElement temp = null;
		switch(property.toLowerCase()){
			case "id":
				try {
					temp = browser.findElement(By.id(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "xpath":
				try {
					temp = browser.findElement(By.xpath(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "css":
				try {
					temp = browser.findElement(By.cssSelector(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "classname":
				try {
					temp = browser.findElement(By.className(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "linktext":
				try {
					temp = browser.findElement(By.linkText(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "name":
				try {
					temp = browser.findElement(By.name(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present :"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
			case "tagname":
				try {
					temp = browser.findElement(By.tagName(identifier));
				} catch (Exception e1) {
					report.infoStep("Element "+property+" = '"+identifier+"' is not present in DOM:"+ e1.getMessage());
					Assert.fail(e1.getMessage());
				}
				break;
		}
		try{
			Thread.sleep(500);
			temp.isDisplayed();
		}catch(Exception e){
			report.infoStep("Element "+property+" = '"+identifier+"' is present but not displayed:"+ e.getMessage());
			Assert.fail(e.getMessage());
		}
		return temp;
	}
	
	/**
	 * @author nwisnewski 6/6/2017
	 * Adds text to a non web element using robotil class
	 * @param text
	 * @throws AWTException 
	 */
	
	public void sendTextNonWebElement(String text) throws AWTException{
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection copyText = new StringSelection(text);
		clip.setContents(copyText, copyText);
		if(threadRun.nodeIp().equalsIgnoreCase("local")){
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.delay(50);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			robot.delay(50);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.delay(50);
			robot.keyRelease(KeyEvent.VK_ENTER);
		}else{
			Robotil robotil=new Robotil(threadRun.nodeIp(), 5556);
			robotil.pressKey(KeyEvent.VK_CONTROL);
			robotil.pressKey(KeyEvent.VK_V);
			robotil.releaseKey(KeyEvent.VK_CONTROL);
			robotil.releaseKey(KeyEvent.VK_V);
			robotil.pressAndReleaseKey(KeyEvent.VK_ENTER);
		}
	}


	/**
	 * @author nwisnewski 6/21/2017
	 * Evaluates whether an element is present
	 * @param property - Fixed: id, xpath, css, classname, linktext, name, tagname
	 * @param identifier
	 */
	public boolean isElementPresentBy(String property, String identifier){
		List<WebElement> temp = null;
		switch(property.toLowerCase()){
		case "id":
			temp = browser.findElements(By.id(identifier));
			break;
		case "xpath":
			temp = browser.findElements(By.xpath(identifier));
			break;
		case "css":
			temp = browser.findElements(By.cssSelector(identifier));
			break;
		case "classname":
			temp = browser.findElements(By.className(identifier));
			break;
		case "linktext":
			temp = browser.findElements(By.linkText(identifier));
			break;
		case "name":
			temp = browser.findElements(By.name(identifier));
			break;
		case "tagname":
			temp = browser.findElements(By.tagName(identifier));
			break;
		}
		return !temp.isEmpty();
	}
	/**
	 * @author nwisnewski
	 * Validates the expected text has been sent, will retry up to 4 times before failing
	 * @param textField - webelement where text is being sent
	 * @param text  - text sent
	 * @throws InterruptedException
	 */
	public void verifyTextSent(WebElement textField, String text) throws InterruptedException{
		int i = 0;
		while(!textField.getAttribute("value").equals(text)&&i<4){
			System.out.println(textField.getAttribute("value"));
			textField.clear();
			textField.sendKeys(text);
			Thread.sleep(250);
			i++;
		}
		if(i==4){
			report.runStep(false, "Text '"+text+"' was not entered in the field correctly.");
		}
	}
	/**
	 * @author nwisnewski
	 * Selects from drop down, resolves issue with selenium select for safari browser.
	 * @param identifier - xpath of select object
	 * @param valOrTxt -Fixed: value- select by value, or text- select by visible text
	 * @param selection- text of value selection form dropdown 
	 * @throws InterruptedException
	 */
	public void selectDropDown(WebElement drpDown, String selection) throws InterruptedException{
		Select select = new Select(drpDown);
		if(!RunFactory.getRunDetails().runBrowser().equalsIgnoreCase("safari")){
			try {
				select.selectByVisibleText(selection);
			} catch (Exception e) {
				report.infoStep("Select dropdown list fail:"+ e.getMessage());
				Assert.fail(e.getMessage());
			}
		}else{
			int i = 0;
			List<WebElement> allOptions = select.getOptions();
			System.out.println(allOptions.size());
			Thread.sleep(250);
			for(WebElement option: allOptions){
				System.out.println(option.getText());
				if(option.getText().equalsIgnoreCase(selection)){
					break;
				}
				i++;
					/*Thread.sleep(500);
					if(i==0){
						drpDown.sendKeys(Keys.ENTER);
					}else{
						for(int j=1;j<=i;j++){
							System.out.println("Arrow down "+j);
							drpDown.sendKeys(Keys.ARROW_DOWN);
							if(j==i){
								System.out.println("On option "+allOptions.get(i).getText());
								System.out.println("Is it equal? -- "+allOptions.get(i).getText().equalsIgnoreCase(selection));
								drpDown.sendKeys(Keys.ENTER);
							}
						}
					}
					break;
				}*/
			}
			System.out.println(i);
			try {
				select.selectByIndex(i);
			} catch (Exception e) {
				report.infoStep("Select dropdown list fail:"+ e.getMessage());
				Assert.fail(e.getMessage());
			}
			
		}
	}
	
	public void waitFail(Exception e){
		report.infoStep("Wait fail:"+ e.getMessage());
		Assert.fail(e.getMessage());
	}
	
	
}
