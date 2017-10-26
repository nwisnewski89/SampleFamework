package zeta.pages;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import zeta.utilities.Report;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class Environment extends ZetaCMS {
	
	private static final String CreateEnvironment = "//a[@role='button' and @data-modal-link='/entities/environments/create-modal']"; 
	
	private static final String DeleteEnvironment = ".btn.btn-default.zeta-btn-env-delete";
	
	private static final String NewEnvironment = "//div/div/div/a[@class='btn btn-default zeta-btn-env-create']";
	
	
	
	public Environment(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	/**
	 * Creates a new environment, then deletes that environment.
	 * @return
	 * @throws InterruptedException
	 */
	public Environment createAndDeleteEnvironment() throws InterruptedException{
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateEnvironment)));
		String api = tempElementBy("name", "api").getAttribute("value");
		System.out.println(api);
		clickElementBy("xpath", CreateEnvironment);
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(NewEnvironment)));
		Thread.sleep(1000);
		String newEnvFields[][] = {{"name","Auto Test Environment"},  {"slug","auto-test-environment"},  {"alias", "ATE"}, {"short_alias", "T"},{"url", "test.com"}, {"api", api}};
		int size = newEnvFields.length;
		System.out.println(size);
		for(int i=0; i<size; i++){
			environmentField(newEnvFields[i][0]).sendKeys(newEnvFields[i][1]);
			Thread.sleep(500);
		}
		clickElementBy("xpath", NewEnvironment);
		Thread.sleep(1000);
		report.runStep(true, "Created new environment: name-'Auto Test Environment', slug-'auto-test-environment', alias-'T', short_alias - 'ATE', url - 'test.com', api - '"+api+"'.");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateEnvironment)));
		clickElementBy("css", RefreshZeta);
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateEnvironment)));
		ArrayList<Map.Entry<String, Boolean>> environmentCreated =  new ArrayList<Map.Entry<String, Boolean>>();
		Thread.sleep(500);
		for(int i=0; i<size-1;i++){
			environmentCreated.add(new AbstractMap.SimpleEntry<String, Boolean>(newEnvFields[i][0], newEnvironmentCheck(newEnvFields[i][0], newEnvFields[i][1])));
		}
		String fail = "";
		for(int i=0; i< environmentCreated.size(); i++){
			if(!environmentCreated.get(i).getValue()){
				fail = environmentCreated.get(i).getKey();
				break;
			}
		}
		if(fail.equals("")){
			report.runStep(true, " Environment Auto Test environment was created successfully and saved with all values entered in the previous step.");
		}else{
			report.runStep(false, "The environment value '"+fail+"' was not saved correctly.");
		}
		List<WebElement> delete = browser.findElements(By.cssSelector(DeleteEnvironment));
		Thread.sleep(500);
		delete.get(delete.size()-1).click();
		wait.until(ExpectedConditions.alertIsPresent());
		browser.switchTo().alert().accept();
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateEnvironment)));
		report.runStep(!newEnvironmentCheck(newEnvFields[0][0], newEnvFields[0][1]), "Environment Auto Test Environment was deleted successfully.");
		return this;
	}
	/**
	 * Checks if the value entered for the new environment is displayed on the 
	 * Environments page, after new environment was created.
	 * @param name
	 * @param value
	 * @return
	 */
	private boolean newEnvironmentCheck(String name, String value){
		return isElementPresentBy("xpath","//input[@name='"+name+"' and @value='"+value+"']");
	}
	/**
	 * Returns the text input object of the new environment pop up modal.
	 * @param field - The name attribute of the <input> tag of the text input object
	 * @return
	 */
	private WebElement environmentField(String field){
		return tempElementBy("xpath", "//div[@id='remoteModal']/div[@class='modal-dialog']/div/div/div[@class='modal-body']/div/div/div/input[@name='"+field+"']");
	}

}
