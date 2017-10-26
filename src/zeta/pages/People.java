package zeta.pages;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import zeta.utilities.Report;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class People extends ZetaCMS{
	
	/*@FindBy(css = "input.form-control.input-sm")
	private WebElement searchPeople;
	
	@FindBy(name = "zeta_people_table_length")
	private WebElement showDropDown;*/
	
/*	@FindBy(name = "Name.first")
	private WebElement firstName;
	
	@FindBy(name = "Name.last")
	private WebElement lastName;
	
	@FindBy(name = "Info.position")
	private WebElement position;
	
	@FindBy(name = "Info.desc")
	private WebElement description;*/
	
	/*@FindBy(css= ".btn.btn-default.zeta-btn-person-create")
	private WebElement submitPersonButton;
	
	@FindBy(css = ".btn.btn-default.zeta-btn-person-delete")
	private WebElement deletePersonButton;
		*/
	/*private static final String EditPerson = "//tbody/tr/td[4]/a";*/
	

	private static final String DeletePersonBtn = ".btn.btn-default.zeta-btn-person-delete";
	
	private static final String SubmitPersonBtn = ".btn.btn-default.zeta-btn-person-create";
	
	private static final String FirstNameCol = "//tbody/tr/td[1]";
	
	private static final String LastNameCol = "//tbody/tr/td[2]";
	
	private static final String PositionCol = "//tbody/tr/td[3]";
	
	private static final String CreateButton = "//nav/form/a[text()='Create']";
	
	private static final String ImageSelect = "//div[contains(@class, 'selectize-dropdown')]/div/div[@class='active']";
	
	private static final String SelectedImage = "Img.primary";
	
	private static final String RowCount = "//table[@id ='zeta_people_table']/tbody/tr";
	
	public People(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	/**
	 * Adds a person to the person section, with position, description, and image
	 * @param firstname
	 * @param lastname
	 * @param imageName
	 * @return
	 * @throws InterruptedException
	 */
	public People addPeople(String firstname, String lastname, String imageName) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", CreateButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Name.first")));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", "Name.first").sendKeys(firstname);
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name", "Name.first"), firstname);
		tempElementBy("name", "Name.last").sendKeys(lastname);
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name", "Name.last"), lastname);
		tempElementBy("name", "Info.position").sendKeys("QA Engineer");
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name", "Info.position"), "QA Engineer");
		tempElementBy("name", "Info.desc").sendKeys("I am an artificial person blah blah blah blah.");
		Thread.sleep(250);
		verifyTextSent(tempElementBy("name", "Info.desc"), "I am an artificial person blah blah blah blah.");
		tempElementBy("css",".selectize-input.items.not-full>input").sendKeys(imageName);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(ImageSelect)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath",ImageSelect);
		Thread.sleep(1000);
		System.out.println(tempElementBy("name", SelectedImage).getAttribute("value"));
		report.runStep(tempElementBy("name", "Name.first").getAttribute("value").equalsIgnoreCase(firstname)&&tempElementBy("name", "Name.last").getAttribute("value").equalsIgnoreCase(lastname)&&tempElementBy("name", "Info.position").getAttribute("value").equalsIgnoreCase("QA Engineer")
				&&!(tempElementBy("name", "Info.desc").getAttribute("value").isEmpty()||tempElementBy("name", SelectedImage).getAttribute("value").isEmpty())
				, "New Person '"+firstname+" "+lastname+"' has been created with position, description, and image.");
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(SubmitPersonBtn)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("css", SubmitPersonBtn);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateButton)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		return this;
	}
	/**
	 * searches for person added in previous step
	 * @param firstname
	 * @param lastname
	 * @return
	 * @throws Exception
	 */
	public People searchForNewPerson(String firstname, String lastname) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		zetaSearch(firstname,"people");
		List<WebElement> first = browser.findElements(By.xpath(FirstNameCol));
		System.out.println(first.size());
		List<WebElement> last = browser.findElements(By.xpath(LastNameCol));
		System.out.println(first.get(0).getText().equalsIgnoreCase(firstname));
		System.out.println(last.get(0).getText().equalsIgnoreCase(lastname));
		report.runStep(first.get(0).getText().equalsIgnoreCase(firstname)&&last.get(0).getText().equalsIgnoreCase(lastname)
				, "Entered search for new user '"+firstname+" "+lastname+"', people search returned the correct result.");
		tempElementBy("css", ZetaSearch).clear();
		tempElementBy("css", ZetaSearch).sendKeys(Keys.ENTER);
		Thread.sleep(2000);
		return this;
	}
	/**
	 * Verifies each column in the people grid can be sorted in ascending and descending order
	 * @return
	 * @throws InterruptedException
	 */
	public People columnSortCheck() throws InterruptedException{
		List<WebElement> first = browser.findElements(By.xpath(FirstNameCol));
		report.runStep(sortCheck(first, "ascending"), "The first name column is sorted in ascending order by default.");
		Thread.sleep(750);
		clickElementBy("xpath", sortCol("First Name"));
		Thread.sleep(2000);
		first = browser.findElements(By.xpath(FirstNameCol));
		report.runStep(sortCheck(first, "descending"), "After clicking the first name column header, the first name column is sorted descending order.");
		Thread.sleep(750);
		clickElementBy("xpath", sortCol("Last Name"));
		Thread.sleep(2000);
		List<WebElement> last = browser.findElements(By.xpath(LastNameCol));
		report.runStep(sortCheck(last, "ascending"), "After clicking the last name column header, the last name column is sorted ascending order.");
		Thread.sleep(750);
		clickElementBy("xpath", sortCol("Last Name"));
		Thread.sleep(2000);
		last = browser.findElements(By.xpath(LastNameCol));
		report.runStep(sortCheck(last, "descending"), "After clicking the last name column header again, the last name column is sorted descending order.");
		Thread.sleep(750);
		clickElementBy("xpath", sortCol("Position"));
		Thread.sleep(2000);
		List<WebElement> position = browser.findElements(By.xpath(PositionCol));
		report.runStep(sortCheck(position, "ascending"), "After clicking the position column header, the position column is sorted ascending order.");
		Thread.sleep(750);
		clickElementBy("xpath", sortCol("Position"));
		Thread.sleep(2000);
		position = browser.findElements(By.xpath(PositionCol));
		report.runStep(sortCheck(position, "descending"), "After clicking the position column header again, the position column is sorted descending order.");
		return this;
	}
	/**
	 * Checks that if there are more than 10 authors/people the pagination buttons are present, and the table size can be increased
	 * @return
	 * @throws InterruptedException
	 */
	public People paginationCheck() throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);	
		}
		if(browser.findElements(By.xpath(RowCount)).size()==10){
			String firstPage = browser.findElement(By.xpath(FirstNameCol)).getText();
			clickElementBy("xpath", pagination("next"));
			Thread.sleep(2000);
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(FirstNameCol)));
			} catch (Exception e) {
				waitFail(e);	
			}
			report.runStep(!firstPage.equals(browser.findElement(By.xpath(FirstNameCol)).getText())
					, "Next pagination button loaded the next page.");
			clickElementBy("xpath", pagination("previous"));
			report.runStep(firstPage.equals(browser.findElement(By.xpath(FirstNameCol)).getText())
					, "Pevious pagination button loaded the previous page.");
			selectDropDown(tableLength("people"),"25");
			Thread.sleep(1000);
			int size = browser.findElements(By.xpath(RowCount)).size();
			System.out.println(size);
			report.runStep(size>10&&size<=25,"25 was selected from the show entries, page size is '"+size+"'.");
		}
		return this;
	}
	/**
	 * Deletes a person from people section
	 * @param firstname
	 * @param lastname
	 * @throws Exception
	 */
	public void deletePerson(String firstname, String lastname) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);	
		}
		zetaSearch(lastname, "people");
		clickElementBy("css", EditAPW);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Name.first")));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("name","Name.first"), "Edit link opened the edit window of '"+firstname+" "+lastname+"'.");
		Thread.sleep(1000);
		clickElementBy("css", DeletePersonBtn);
		try {
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception e) {
			waitFail(e);
		}
		Alert alert = browser.switchTo().alert();
		String deleteConfirm = alert.getText();
		report.runStep(deleteConfirm.equals("Are you sure you want to delete this Person?"), "Delete person confirmation alert was recieved.");
		alert.accept();
		Thread.sleep(1000);
		tempElementBy("css", ZetaSearch).sendKeys(lastname);
		tempElementBy("css", ZetaSearch).sendKeys(Keys.ENTER);
		Thread.sleep(1000);
		report.runStep(browser.findElement(By.xpath(FirstNameCol)).getText().contains("No matching records found")
				, "Testing person '"+firstname+" "+lastname+"' was deleted successfully.");
	}
	/**
	 * Gets the first name of the first author dispalyed in people section	
	 * @return - author name
	 */
	public String getAuthorName(){
		report.infoStep("Navigating to people page to retrieve and author name");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateButton)));
		} catch (Exception e) {
			waitFail(e);
		}
		return tempElementBy("xpath", FirstNameCol).getText();
	}
	/**
	 * Validates the columns are sorted in ascending/descending order
	 * @param names
	 * @param sort
	 * @return
	 */
	private boolean sortCheck(List<WebElement> names, String sort){
		boolean isSorted = true;
		int i = 0;
		int rowCount = browser.findElements(By.xpath(RowCount)).size();
		System.out.println(rowCount);
		System.out.println(names.size());
		System.out.println(sort.toLowerCase());
		for(WebElement name:names){
			if(i<rowCount-1){
				switch(sort.toLowerCase()){
					case "ascending":
						System.out.println(name.getText());
						System.out.println(names.get(i+1).getText());
						System.out.println(name.getText().toLowerCase().compareTo(names.get(i+1).getText().toLowerCase()));
						if(name.getText().toLowerCase().compareTo(names.get(i+1).getText().toLowerCase())>0){
							isSorted = false;
							break;
						}
						break;
					case "descending":
						System.out.println(name.getText());
						System.out.println(names.get(i+1).getText());
						System.out.println(name.getText().toLowerCase().compareTo(names.get(i+1).getText().toLowerCase()));
						if(name.getText().toLowerCase().compareTo(names.get(i+1).getText().toLowerCase())<0){
							isSorted = false;
							break;
						}
					break;
				}
			}
			i++;
		}
		System.out.println(isSorted);
		return isSorted;
	}
	/**
	 * xpath of the column header -- click to change the sorting order 
	 * @param column - Fixed: First Name, Last Name, Position
	 * @return
	 */
	private String sortCol(String column){
		return "//thead/tr/th[contains(text(), '"+column+"')]";
	}
	/**
	 * pagination boxes
	 * @param direction
	 * @return
	 */
	private String pagination(String direction){
		return "//li[@id='zeta_people_table_"+direction.toLowerCase()+"']/a";
	}
	
}
