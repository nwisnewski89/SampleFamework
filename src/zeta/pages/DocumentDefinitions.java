package zeta.pages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class DocumentDefinitions extends ZetaCMS{

	@FindBy(css = ".navbar-brand")
	protected WebElement docDefName;
	
	@FindBy(xpath = "//div[@class='search input-group']/input")
	protected WebElement componentSearch;
	
	@FindBy(xpath = "//select[@name='img-size']")
	protected WebElement imageSize;
	
	@FindBy(xpath = "//tr[@class='zeta-docdef-history-draft-row']/td[2]")
	protected WebElement docDefEdition;
	
	@FindBy(id = "createDocDefSelectWrapper")
	protected WebElement selectWrapper;
	
	@FindBy(xpath = "//label[text()='Assets (Header)']/following-sibling::div/div/div/input")
	protected WebElement assetHeader;
	
	protected static final String CreateButton = "//div[@class='modal-footer']/button[contains(text(), 'Create')]";
	
	protected static final String CreateNewFieldBtn = "//button[contains(text(), 'Create New Field')]";
	
	protected static final String CreateFieldButton = "//div[@class='modal-footer']/button[contains(text(), 'Create')]";
	
	protected static final String ComponentAdd = ".btn.btn-primary.btn-xs.btn-cmp-select";
	
	protected static final String SaveComponentButton = "//div[@class='modal-footer']/button[contains(text(), 'Save')]";
		
	protected static final String PublishChkBox = "input.zeta-docdef-history";
	
	protected static final String OptionsTab = "//ul[@class='nav nav-tabs in']/li/a[contains(@href, '#docdef_options')]";
	
	protected static final String DataListAddField = "zetaFormDataListAddFields";
	
	protected static final String CreateNew = "//form[@class='navbar-form']/a[text()='Create']";
	
	protected static final String ContentField = "content";
	
	protected static final String ImageField = "image";
	
	protected static final String SaveUI = "//div[contains(@id,'ui_navbar')]/div/button[text()='Save']";
	
	//protected static final String WrapperDrpDwn = "//select[@id='r']";
	
	public DocumentDefinitions(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	
	
	/**
	 * Creates a new doc definition
	 * @param title - title of doc def
	 * @param wrapper - set in project variables
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions createNewDocDefinition(String title, String wrapper) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNew)));
			System.out.println("good");
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(1000);
		clickElementBy("xpath", CreateNew);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", TitleField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", TitleField).sendKeys(title);
		verifyTextSent(tempElementBy("name", TitleField), title);
		Thread.sleep(1000);
		selectDropDown(selectWrapper, wrapper);
		clickElementBy("xpath", CreateButton);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
			
		}
		System.out.println(docDefName.getText());
		System.out.println(title.toLowerCase());
		report.runStep(docDefName.getText().equalsIgnoreCase(title.toLowerCase()), "New doc definition '"+title+"' was created.");
		return this;
	}
	/**
	 * Opens an existing doc def for editing
	 * @param title- title of doc def to open
	 * @return
	 * @throws Exception 
	 */
	public DocumentDefinitions editExistingDocDef(String title) throws Exception{
		searchForContent(title);
		Thread.sleep(500);
		clickElementBy("xpath", editDocDef(title));
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(docDefName.getText().equalsIgnoreCase(title.toLowerCase()), "The doc definition '"+title+"' was opened for editing.");
		return this;
	}
	/**
	 * Validates that the title is a required field for doc defs.
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions titleRequired() throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNew)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(1000);
		clickElementBy("xpath", CreateNew);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateButton)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", CreateButton);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(AlertDialouge)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		String errorMessage = tempElementBy("css", AlertDialouge).getText();
		report.runStep(errorMessage.contains("ValidationError"), "Error message '"+errorMessage+"' when attempting to create a doc definition with no title.");
		clickElementBy("xpath", "//div[@class='modal-footer']/button[contains(text(), 'Cancel')]");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNew)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		return this;
	}
	/**
	 * @author nwisnewski
	 * @param title
	 * @param fieldDetails - List of value pairs in 2d array where each entry represents [field/slug name, field]
	 * field/slug name  - should be all lowercase and no spaces
	 * field - Fixed: Checkbox, Data List, Date Picker, Document Selector, Image Selector, Person Selector, Radio Button, Text Field, Textarea, WYSIWYG
	 * @return
	 * @throws Exception 
	 */
	public DocumentDefinitions addFields(String title, String[][] fieldDetails, String page) throws Exception{
		String reportString = "";
		int size = fieldDetails.length;
		//String[] fieldTitles = new String[size];
		for(int i =0 ; i< size; i++){
			clickElementBy("xpath", CreateNewFieldBtn);
			Thread.sleep(2000);
			try {
				wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
			} catch (Exception e) {
				waitFail(e);
				
			}
			Thread.sleep(500);
			tempElementBy("name", NameField).sendKeys(fieldDetails[i][0]);
			Thread.sleep(1000);
			selectDropDown(selectFieldType(page), fieldDetails[i][1]);
			if(fieldDetails[i][1].equalsIgnoreCase("radio button")||fieldDetails[i][1].equalsIgnoreCase("checkbox")){
				switch(fieldDetails[i][1].toLowerCase()){
					case "radio button":
						add3RadioButtonsOrCheckBox("Radio", "doc def");
						break;
					case "checkbox":
						add3RadioButtonsOrCheckBox("Checkbox", "doc def");
						break;
				}
			}
			Thread.sleep(500);
			clickElementBy("xpath", CreateFieldButton);
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CreateNewFieldBtn)));
			} catch (Exception e) {
				waitFail(e);
				
			}
			reportString+= " field '"+fieldDetails[i][1]+"' with title '"+fieldDetails[i][0]+"'";
		}
		System.out.println(reportString);
		clickElementBy("xpath", saveFields(page));
		report.runStep(checkAddedFields(fieldDetails), "The following:"+reportString+", was added to the "+page+" '"+title+"'.");
		return this;
	}
	/**
	 * @author nwisnewski
	 * Adds a data list, then enters desired fields into data list
	 * @param docDefTitle - title of doc def
	 * @param repeaterFieldName - data list name/slug
	 * @param DatalistFields - List of value pairs in 2d array where each entry represents [field/slug name, field]
	 * @throws Exception
	 */
	public DocumentDefinitions addDataList(String docDefTitle, String[][] datalistFields) throws Exception{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CreateNewFieldBtn)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", CreateNewFieldBtn);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(250);
		tempElementBy("name", NameField).sendKeys("repeater");
		Thread.sleep(500);
		Thread.sleep(250);
		selectDropDown(selectFieldType("docdef"), "Data List");
		Thread.sleep(250);
		int numFields = datalistFields.length;
		clickAddFields(numFields);
		String reportString = "";
		for(int i = 0; i<numFields; i++){
			Thread.sleep(250);
			dataListFieldSlug(i+1).sendKeys(datalistFields[i][0].toLowerCase());
			Thread.sleep(250);
			dataListFieldName(i+1).sendKeys(datalistFields[i][0]);
			Thread.sleep(500);
			selectDropDown(dataListFieldType(i+1), datalistFields[i][1]);
			Thread.sleep(250);
			if(datalistFields[i][1].equalsIgnoreCase("radio button")||datalistFields[i][1].equalsIgnoreCase("check box")){
				switch(datalistFields[i][1].toLowerCase()){
					case "radio button":
						add3RadioButtonsOrCheckBox("Radio", "data list");
						break;
					case "check box":
						add3RadioButtonsOrCheckBox("Checkbox", "data list");
						break;
				}
			}
			reportString+= " field '"+datalistFields[i][1]+"' with title '"+datalistFields[i][0]+"'";
		}
		Thread.sleep(500);
		clickElementBy("xpath", CreateFieldButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(saveFields("docdef"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(1000);
		clickElementBy("xpath", saveFields("docdef"));
		System.out.println("Saved 2");
		String[][] rept = {{"repeater", ""}};
		report.runStep(checkAddedFields(rept), "Data list with the following fields: "+reportString+", was added to the doc def '"+docDefTitle+"'.");
		clickElementBy("xpath", saveFields("docdef"));
		System.out.println("Saved 3");
		return this;
	}
	/**
	 * Creates a new doc definition edition of the doc def created in test createNewDocDefAndDocument
	 * @param title - new edition title
	 * @param wrapper - wrapper of project
	 * @return String title of the doc def new edition was created from
	 * @throws InterruptedException
	 */
	public DocumentDefinitions createNewEditionDocDef(String docDefTitle, String docEditionTitle, String wrapper) throws InterruptedException{
		String createNewEdition = ".widget-toolbar>a.btn";
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(createNewEdition)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("css", createNewEdition);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(docEditionTitle);
		verifyTextSent(tempElementBy("name", NameField), docEditionTitle);
		Thread.sleep(1000);
		System.out.println(wrapper);
		selectDropDown(selectWrapper, wrapper);
		clickElementBy("xpath", CreateButton);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(docDefName.isDisplayed(), "Created new edition '"+docEditionTitle+"' of doc def '"+docDefTitle+"'.");
		return this;
	}
	/**
	 * Sets the doc def edition to a future date to revert back to original
	 * @param docDefTitle - title of original doc def
	 * @param editionTitle - title of doc def edition
	 * @return
	 * @throws Exception
	 */
	public DocumentDefinitions endDocDefEdition(String docDefTitle, String editionTitle) throws Exception{
		searchForContent(docDefTitle);
		clickElementBy("xpath", editDocDef(docDefTitle));
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(saveFields("docdef"))));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(docDefName.getText().equalsIgnoreCase(docDefTitle), "Opened the doc def '"+docDefTitle+"' for editing.");
		boolean editionEnded = endEdition(editionTitle);
		report.runStep(editionEnded
				, "The doc def edition '"+editionTitle+"' was set to start in 2020, UI of document should revert back to original doc def '"+docDefTitle+"'.");
		return this;
	}
	/**
	 * Imports doc def from the testData folder
	 * @param docDefTitle - title of imported doc def
	 * @param wrapper
	 * @return
	 * @throws Exception
	 */
	public DocumentDefinitions importDocDef(String docDefTitle, String wrapper) throws Exception{
		uploadImportFile(docDefTitle, wrapper);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
			
		}
		System.out.println(docDefName.getText());
		System.out.println(docDefTitle.toLowerCase());
		report.runStep(docDefName.getText().equalsIgnoreCase(docDefTitle.toLowerCase()), "New doc definition '"+docDefTitle+"' was created from an imported .docdef file.");
		String[][] image = {{"image", ""}, {"textarea", ""}};
		report.runStep(checkAddedFields(image), "The image and text area fields on the doc def that was imported is displayed in the newly created doc def '"+docDefTitle+"'.");
		navToUI("docdef");
		report.runStep(isElementPresentBy("xpath", clickAddedUISection("image"))&&isElementPresentBy("xpath", clickAddedUISection("textarea"))
				, "The image and text area component added to the UI of the imported doc def is displayed in the UI of the newly created doc def '"+docDefTitle+"'.");
		clickElementBy("xpath", delete("DocDef"));
		try {
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception e) {
			waitFail(e);
		}
		browser.switchTo().alert().accept();
		report.runStep(true, "Successfully imported doc def, doc def '"+docDefTitle+"' created from import was deleted.");
		return this;
	}
	/**
	 * Verify that the newly created doc def appears in the grid/table
	 * @param docDefTitle
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions verifyInDocDefGrid(String docDefTitle) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(zetaPageTabs("browse"))));
		} catch (Exception e) {
			waitFail(e);	
		}
		//switch back to grid view
		clickElementBy("xpath", zetaPageTabs("browse"));
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOf(tableLength("docdefs")));
		} catch (Exception e) {
			waitFail(e);
			
		}
		selectDropDown(tableLength("docdefs"), "100");
		String newDocDef =  "//td[text()='"+docDefTitle+"']";
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newDocDef)));
		} catch (Exception e) {
			waitFail(e);	
		}
		report.runStep(isElementPresentBy("xpath", newDocDef), "Doc definition '"+docDefTitle+"' created in the previous step is displayed in the doc definition tree.");
		clickElementBy("xpath", zetaPageTabs("app-tab"));
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOf(docDefName));
		} catch (Exception e) {
			waitFail(e);
			
		}
		return this;
	}
	/**
	 * Checks all the expected buttons and dropdown values are present on the edit doc def page
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions docDefPagesSanity() throws InterruptedException{
		report.runStep(isElementPresentBy("xpath", "//button[contains(text(), 'Export DocDef')]")&&isElementPresentBy("xpath", delete("DocDef"))
				, "Export button, delete doc def button, and edition button are displayed in the doc def page.");
		report.runStep(isElementPresentBy("xpath", saveFields("docdef"))&&isElementPresentBy("xpath", CreateNewFieldBtn)&&isElementPresentBy("xpath", "//button[contains(text(), 'Add New Tab')]") 
				, "Save button, create new fields button, and add new tab button are displayed in the Fields section of the doc def page.");
		clickElementBy("xpath", CreateNewFieldBtn);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		String[] expectedFields = {"Checkbox", "Data List", "Date Picker", "Document Selector", "Image Selector", "Person Selector", "Dropdown (Select)", "Text Field", "Textarea", "WYSIWYG"};
		List<WebElement> fields = new Select(selectFieldType("docDef")).getOptions();
		String presentFields = "";
		for(int i= 0; i< expectedFields.length; i++){
			boolean inDrpDown = false;
			System.out.println(fields.size());
			for(WebElement field:fields){
				System.out.println(field.getText());
				if(field.getText().equalsIgnoreCase(expectedFields[i])){
					inDrpDown = true;
					if(i<(expectedFields.length-1)){
						presentFields+=expectedFields[i]+", ";
					}else{
						presentFields+=expectedFields[i];
					}
					break;
				}
			}
			if(!inDrpDown){
				report.runStep(false, "Expected field '"+expectedFields[i]+"' is not present in the dropdown.");
				break;
			}
		}
		report.runStep(true, "All expected fields "+presentFields+" are available in the create new field drop down.");
		clickElementBy("xpath", "//div[@class='modal-footer']/button[contains(text(), 'Cancel')]");
		navToUI("docdef");
		report.runStep(isElementPresentBy("xpath", addToUI("region"))&&isElementPresentBy("xpath", addToUI("repeater"))&&isElementPresentBy("xpath", addToUI("row"))&&isElementPresentBy("xpath", addToUI("column"))  
				&&isElementPresentBy("xpath", addToUI("cmp"))&&isElementPresentBy("xpath", addToUI("widget"))&&isElementPresentBy("xpath", SaveUI)
				, "Add region, add repeater, add row, add column, add component, add widget, and save buttons are all present in the ui section of doc def page.");
		return this;
	}
	/**
	 * Verifies when a user navigates away from the doc def page to another section of the cms, then back
	 * to the doc def page the last action is remembered.
	 * @param docDefTitle
	 * @param lastAddition -  title of the last value added to the UI section of doc def
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions rememberLastAction(String docDefTitle, String lastAddition) throws InterruptedException{
		navigateToPeople(false);
		report.infoStep("Navigated away from doc def '"+docDefTitle+"' page to the people section.");
		navigateToDocDef(false);
		/*wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(clickAddedUISection(lastAddition))));*/
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(lastAddition)), "After navigating to a different section of the CMS, and back to Doc def, the section '"+lastAddition+"' added to Ui of '"+docDefTitle+"' in previous step is displayed.");
		return this;
	}
	/**
	 * Verifies that from the UI section of a doc def a user can edit or delete 
	 * compnents added to the UI.
	 * @param sectionName
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions editDeleteUISection(String sectionName) throws InterruptedException{
		String regionContainer = "//li[@data-ui-type='region']";
		new Actions(browser).moveToElement(tempElementBy("xpath",regionContainer)).doubleClick().build().perform();
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);		
		}
		tempElementBy("name", NameField).clear();
		tempElementBy("name", NameField).sendKeys("Edit Test");
		verifyTextSent(tempElementBy("name", NameField), "Edit Test");
		clickElementBy("xpath", SaveComponentButton);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(regionContainer)));
		} catch (Exception e) {
			waitFail(e);
				
		}
		System.out.println(tempElementBy("xpath", regionContainer).getText());
		report.runStep(tempElementBy("xpath", regionContainer).getText().contains("Edit Test"), "The ui section '"+sectionName+"' was changed to 'Edit Test' using the edit button on the section.");
		System.out.println(RunFactory.getRunDetails().runBrowser());
		if(!RunFactory.getRunDetails().runBrowser().equalsIgnoreCase("ie")){
			clickElementBy("xpath", regionContainer+"/div/button[@data-action='remove']");
			Thread.sleep(1000);
			report.runStep(!isElementPresentBy("xpath", regionContainer), "The ui section 'Edit Test' was deleted using the delete button on the section.");
		}
		return this;
	}
	/**
	 * Deletes a doc def
	 * @param docDefTitle
	 */
	public void deleteDocDef(String docDefTitle){
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(delete("DocDef"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", delete("DocDef"));
		try {
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception e) {
			waitFail(e);
			
		}
		browser.switchTo().alert().accept();
		report.runStep(true, "Doc definition '"+docDefTitle+"' was deleted successfully.");
	}
	/**
	 * Clicks on UI tab to navigate to the UI section of the doc def/widget page.
	 * @param page
	 * @return
	 */
	public DocumentDefinitions navToUI(String page){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(UITab(page))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath",  UITab(page));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("cmp"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		return this;
	}
	/**
	 * Adds a content component to the doc def/widget UI, works for most field types
	 * @param textSlug - the slug value of the field being mapped to the UI component
	 * @param title - doc def/widget title
	 * @param page - Fixed: docdef, widget, repeater
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addContentComponent(String textSlug, String title, String page) throws InterruptedException{
		addComp("Content");
		String name = "Content - "+textSlug;
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(name);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), name);
		tempElementBy("name", ContentField).clear();
		tempElementBy("name", ContentField).sendKeys(slug(textSlug,page));
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", ContentField), slug(textSlug,page));
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(name))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(name)), textSlug+" component was added to UI of "+page+" '"+title+"'.");
		return this;
	}
	/**
	 * Adds an image component to the to the doc def/widget UI, works for only image field type
	 * @param imageSlug - slug of the Image Selector field
	 * @param title -   doc def/widget title
	 * @param page -  Fixed: docdef, widget, repeater
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addImage(String imageSlug, String title, String page) throws InterruptedException{
		addComp("Image (img) Tag");
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		String imgSrc = "img-src";
		tempElementBy("name", NameField).sendKeys("Image");
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), "Image");
		Thread.sleep(1000);
	//	new Select(imageSize).selectByValue("500x500");
		selectDropDown(imageSize, "500x500");
		Thread.sleep(500);
		tempElementBy("name", imgSrc).clear();
		tempElementBy("name", imgSrc).sendKeys(slug(imageSlug, page));
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", imgSrc), slug(imageSlug, page));
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection("Image"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection("Image")), "Image was added to the UI of "+page+" '"+title+"'.");
		return this;
	}
	/**
	 * Adds a story headline component
	 * @param titleSlug - slug value of the Text Field added
	 * @param authorSlug - slug value of the Person Selector added 
	 * @param page - Fixed: docdef, widget, repeator
	 * @param title -  doc def title
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addStoryHeadline(String titleSlug,String authorSlug, String page, String title) throws InterruptedException{
		addComp("Story Headline");
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys("Story Headline");
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), "Story Headline");
		Thread.sleep(500);
		WebElement sendTitleSlug = tempElementBy("name", "title-field");
		sendTitleSlug.clear();
		sendTitleSlug.sendKeys(slug(titleSlug, page));
		Thread.sleep(500);
		verifyTextSent(sendTitleSlug, slug(titleSlug, page));
		Thread.sleep(500);
		WebElement sendAuthorSlug = tempElementBy("name", "author");
		String author = "Doc."+authorSlug;
		if(page.equalsIgnoreCase("repeater")){
			author = authorSlug;
		}
		sendAuthorSlug.clear();
		sendAuthorSlug.sendKeys(author);
		Thread.sleep(500);
		verifyTextSent(sendAuthorSlug, author);
		WebElement authorDisplayName = tempElementBy("name", "author-name");
		authorDisplayName.clear();
		authorDisplayName.sendKeys("{Name.first} {Name.last}");
		Thread.sleep(500);
		verifyTextSent(authorDisplayName, "{Name.first} {Name.last}");
		Thread.sleep(500);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection("Story Headline"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection("Story Headline")), "Story headline was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Creates a repeater in the doc def/widget UI section
	 * @param reptName - name to appear on the repeater in the doc def/widget UI section
	 * @param title - title of doc def
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addRepeater(String reptName, String title) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("repeater"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", addToUI("repeater"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(reptName);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), reptName);
		WebElement srcVal = tempElementBy("name", "source-value");
		Thread.sleep(500);
		srcVal.sendKeys("Doc.repeater");
		Thread.sleep(500);
		verifyTextSent(srcVal, "Doc.repeater");
		Thread.sleep(500);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(reptName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", clickAddedUISection(reptName));
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(reptName)), "Repeater was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Adds a colum section to UI of doc def/widget
	 * @param columnName - name to appear on column added to the doc def/widget UI
	 * @param title - title of doc def
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addColumn(String columnName, String title) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("column"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", addToUI("column"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(columnName);
		Thread.sleep(1000);
		selectDropDown(selectColumnSize("xs"), "12");
		Thread.sleep(1000);
		selectDropDown(selectColumnSize("sm"), "12");
		Thread.sleep(1000);
		selectDropDown(selectColumnSize("md"), "6");
		Thread.sleep(1000);
		selectDropDown(selectColumnSize("lg"), "6");
		Thread.sleep(1000);
		verifyTextSent(tempElementBy("name", NameField), columnName);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(columnName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", clickAddedUISection(columnName));
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(columnName)), "Column was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Adds a row section to UI of doc def/widget
	 * @param rowName - name to appear on row added to the doc def/widget UI
	 * @param title - title of doc def/widget
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addRow(String rowName, String title) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("row"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", addToUI("row"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(rowName);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), rowName);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(rowName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(rowName)), "Row was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Saves a publishes the doc def/widget
	 * @param title
	 * @param page - Fixed: docdef, widget
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions saveAndPublish(String title, String page) throws InterruptedException{
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SaveUI)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", SaveUI);
		Thread.sleep(500);
		publishContent(page,title);
		return this;
	}
	/**
	 * Adds a region to UI of doc def/widget 
	 * @param rowName - name to appear on region added to the doc def/widget UI
	 * @param title - title of doc def/widget
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addRegion(String regionName, String title) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("region"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", addToUI("region"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(regionName);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), regionName);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(regionName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(regionName)), "Region was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Click on a section added to doc def/widget UI to add a UI section within that section
	 * @param sectionName - name of the scection to click
	 * @param sectionToAdd -  Fixed: region, repeater, row, column, cmp, widget
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions clickOnSectionAddedToUI(String sectionName, String sectionToAdd) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(clickAddedUISection(sectionName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(500);
		clickElementBy("xpath", clickAddedUISection(sectionName));
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(addToUI(sectionToAdd))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		return this;
	}
	/**
	 * Adds a widget to the doc def UI
	 * @param widgetTitle - title of widget to add to the doc def
	 * @param widgetName - name displayed on the widget section added to the UI of doc def
	 * @param title - title of the doc def
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addWidget(String widgetTitle, String widgetName, String title) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(addToUI("widget"))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		clickElementBy("xpath", addToUI("widget"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(componentSearch));
		} catch (Exception e) {
			waitFail(e);
			
		}
		componentSearch.sendKeys(widgetTitle);
		componentSearch.sendKeys(Keys.ENTER);
		Thread.sleep(2000);
		clickElementBy("css",ComponentAdd);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(widgetName);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), widgetName);
		WebElement wysiwyg = tempElementBy("name", "wysiwyg");
		WebElement textarea = tempElementBy("name", "textarea");
		wysiwyg.sendKeys(slug("wysiwyg", "docdef"));
		Thread.sleep(500);
		verifyTextSent(wysiwyg, slug("wysiwyg", "docdef"));
		textarea.sendKeys(slug("textarea", "docdef"));
		Thread.sleep(500);
		verifyTextSent(textarea, slug("textarea", "docdef"));
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(widgetName))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(widgetName)), "The widget '"+widgetTitle+"' was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Adds a video component to the UI section of a doc def
	 * @param videoTitle -  name displayed on the video section added to the UI of doc def
	 * @param title - title of the doc def
	 * @param slug - slug of the video selector
	 * @return
	 * @throws Exception
	 */
	public DocumentDefinitions addVideoComponent(String videoTitle, String title, String slug) throws Exception{
		addComp("nextGen Video Media");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(NameField)));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("name", NameField).sendKeys(videoTitle);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), videoTitle);
		tempElementBy("name", "video").clear();
		tempElementBy("name", "video").sendKeys("Doc."+slug);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", "video"), "Doc."+slug);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(videoTitle))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(videoTitle)), "Then video media component '"+videoTitle+"' was added to UI section of '"+title+"'.");
		return this;
	}
	/**
	 * Adds a header to the doc def UI
	 * @param headerTitle - name displayed on the header section added to the UI of doc def
	 * @param header - header text that will appear in document
	 * @param title - title of doc def
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions addHeader(String headerTitle, String header, String title) throws InterruptedException{
		addRegion("Header Region", title).clickOnSectionAddedToUI("Header Region", "row");
		addRow("Header Row", title).clickOnSectionAddedToUI("Header Row", "cmp");
		addComp("Header Tag (h1...h6)");
		try {
			wait.until(ExpectedConditions.visibilityOf(tempElementBy("name", NameField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("name", NameField).sendKeys(headerTitle);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), headerTitle);
		tempElementBy("name", "el-id").sendKeys("headerTest");
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", "el-id"),"headerTest");
		tempElementBy("name", ContentField).sendKeys(header);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", ContentField),header);
		clickElementBy("xpath", SaveComponentButton);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(clickAddedUISection(headerTitle))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		report.runStep(isElementPresentBy("xpath", clickAddedUISection(headerTitle)), "The header '"+header+"' was added to UI section of docDef '"+title+"'.");
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SaveUI)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", SaveUI);
		return this;
	}
	/**
	 * Adds a css or js asset to a doc def
	 * @param assetTitle - title of the asset to be added
	 * @param docDefTitle - title of the doc def 
	 * @return
	 * @throws Exception
	 */
	public DocumentDefinitions addAssetToHeader(String assetTitle, String docDefTitle) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(optionsTab("docdef"))));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", optionsTab("docdef"));
		try {
			wait.until(ExpectedConditions.visibilityOf(assetHeader));
		} catch (Exception e) {
			waitFail(e);
		}
		assetHeader.sendKeys(assetTitle);
		Thread.sleep(2000);
		String assetSelect = "//div[@class='selectize-dropdown-content']/div[contains(@class, 'option')]";
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(assetSelect)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath",assetSelect);
		Thread.sleep(250);
		report.runStep(isElementPresentBy("css", "div.selectize-input.items.not-full.ui-sortable.has-options.has-items")
				, "The asset '"+assetTitle+"' was successfully added to the header of doc def '"+docDefTitle+"'.");
		navToUI("docdef");
		return this;
	}
	
	/**
	 * @author nwisnewski
	 * Returns the dropdown object of either the doc def or the widget page.
	 * @param page - Fixed: docdef, widget
	 * @return
	 */
	protected WebElement selectFieldType(String page){
		String id = "";
		if(page.equalsIgnoreCase("docdef")){
			id = "DocDefField";
		}else{
			id = "Widget";
		}
		return tempElementBy("id", "create"+id+"CmpType");
	}
	/**
	 * @author nwisnewski
	 * Extends this to the widget page
	 * @param page - Fixed: docdef, widget
	 * @return
	 */
	protected String saveFields(String page){
		return "//div[contains(@id, '"+page+"_fields_navbar')]/button";
	}

	/**
	 * Xpath of the tab to navigate to the UI section of a doc def/widget
	 * @param page- Fixed: docdef, widget
	 * @return
	 */
	protected String UITab(String page){
		return "//ul[@class='nav nav-tabs in']/li/a[contains(@href, '#"+page+"_ui')]"; 
	}
	/**
	 * Xpath of the tab to navigate to the options section of a doc def/widget
	 * @param page- Fixed: docdef, widget
	 * @return
	 */
	protected String optionsTab(String page){
		return "//ul[@class='nav nav-tabs in']/li/a[contains(@href, '#"+page+"_options')]"; 
	}
	/**
	 * @author nwisnewski
	 * Returns xpath of the edit button
	 * @param docDef
	 * @return
	 */
	private String editDocDef(String docDef){
		return "//td[text()='"+docDef+"']/following-sibling::td/div/div/a[@data-btn-action='edit']";
	}
/*	*//**
	 * @author nwisnewski
	 * Returns xpath of the import button
	 * @param docDef
	 * @return
	 *//*
	private String importDocDefBtn(String docDef){
		return "//li[@data-title='"+docDef+"']/span/div/div/a[@data-btn-action='import']";
	}*/
	/**
	 * @author nwisnewski
	 * Returns xpath of UI section
	 * @param section - Fixed: region, repeater, row, column, cmp, widget
	 * @return
	 */
	protected String addToUI(String section){
		return "//button[@data-cmp='"+section+"']";
	}
	/**
	 * xpath of a section added to the UI of doc def/widget
	 * @param sectionName
	 * @return
	 */
	protected String clickAddedUISection(String sectionName){
		return "//li[contains(@data-ui-cfg,'"+sectionName+"')]";
	}
	/**
	 * drop down object for selecting column width in different break points
	 * @param pageSize - Fixed: xs,sm, md. lg
	 * @return
	 */
	protected WebElement selectColumnSize(String pageSize){
		return tempElementBy("name", pageSize);
	}
	
	protected String edition(String page){
		return "//tr[@class='zeta-"+page+"-history-draft-row']/td[2]";
	}
	/**
	 * @author nwisnewski
	 * Xpath of doc def in the tree based on title
	 * @param title - doc def title
	 * @return
	 */
	protected String docDefInTree(String title){
		return "//li[@data-title='"+title+"']";
	}
	/**
	 * @author nwisnewski
	 * Xpath of  delete docdef/widget button
	 * @param page - Fixed: DocDef, Widget
	 * @return
	 */
	protected String delete(String page){
		return "//button[@zeta-action='delete-"+page.toLowerCase()+"']";
	}
	/**
	 * xpath to click an asset in the dropdown
	 * @param assetTitle - title of the asset to select
	 * @return
	 */
	protected String assetSelect(String assetTitle){
		return "//div[@class='selectize-dropdown-content']/div[contains(text(), '"+assetTitle+"')]";
	}
	/**
	 * returns the string value of a slug to map to UI depending on the page
	 * @param slug
	 * @param page - Fixed: docdef, widget, repeater
	 * @return
	 */
	protected String slug(String slug, String page){
		String returnSlug = "";
		switch(page.toLowerCase()){
			case "docdef":
				returnSlug = "{Doc."+slug+"}";
				break;
			case "widget":
				returnSlug = "{widgetCfg."+slug+"}";
				break;
			case "":
				returnSlug = "{"+slug+"}";
				break;
		}
		return returnSlug;
	}
	/**
	 * text field to type in slug value of field into a data list
	 * @param row - the row of the data list 
	 * @return
	 */
	private WebElement dataListFieldSlug(int row){
		return tempElementBy("xpath","//div[@id='zetaFormDataListFields']/div["+row+"]/div/input[@data-fld='fieldslug']");
	}
	/**
	 * text field to type in name of field into a data list
	 * @param row - the row of the data list 
	 * @return
	 */
	private WebElement dataListFieldName(int row){
		return tempElementBy("xpath", "//div[@id='zetaFormDataListFields']/div["+row+"]/div/input[@data-fld='fieldname']");
	}
	/**
	 * dropdown list of field types in data list
	 * @param row - the row of the data list 
	 * @return
	 */
	private WebElement dataListFieldType(int row){
		return tempElementBy("xpath", "//div[@id='zetaFormDataListFields']/div["+row+"]/div/select[@id='fieldOpt']");
	}
	/**
	 * Clicks add fields button untill the desired number of fields displays
	 * @param numFields
	 * @throws InterruptedException
	 */
	private void clickAddFields(int numFields) throws InterruptedException{
		for(int i = 0; i<numFields; i++){
			tempElementBy("id", "zetaFormDataListAddFields").click();
			Thread.sleep(250);
		}
		report.runStep(browser.findElements(By.xpath("//div[@id='zetaFormDataListFields']/div[@class='row']")).size()==numFields
				, numFields+" fields were added to the data list successfully.");
	}
	/**
	 * Adds either 3 radio buttons or check boxes to a data list of to a field
	 * @param which - Fixed: Checkbox, Radio
	 * @param addedTo - data list or field
	 * @throws InterruptedException
	 */
	private void add3RadioButtonsOrCheckBox(String which, String addedTo) throws InterruptedException{
		String id = "";
		String xpathParent = "";
		if(which.equals("Radio")){
			switch(addedTo.toLowerCase()){
				case "doc def":
					id = "zetaFormRadioAddRadio";
					xpathParent = "zetaForm";
					break;
				case "data list":
					id = "addradiobox";
					xpathParent = "data";
					break;
				}
			
		}else{
			switch(addedTo.toLowerCase()){
			case "doc def":
				id = "zetaFormCheckboxAddCheckbox";
				xpathParent = "zetaForm";
				break;
			case "data list":
				id = "addcheckboxes";
				xpathParent = "data";
				break;
			}
		}
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
		for(int i = 0; i<3; i++){
			tempElementBy("id", id).click();
			Thread.sleep(250);
		}
		Thread.sleep(1000);
		report.runStep(browser.findElements(By.xpath("//div[@id='"+xpathParent+which+"List']/div")).size()==3, "Three "+which+" buttons were added to "+addedTo+".");
		for(int i = 1; i<=3;i++){
			Thread.sleep(250);
			tempElementBy("xpath", "//div[@id='"+xpathParent+which+"List']/div["+i+"]/div/input[@data-fld='slug']").sendKeys(which.toLowerCase()+i);
			Thread.sleep(250);
			tempElementBy("xpath", "//div[@id='"+xpathParent+which+"List']/div["+i+"]/div/input[@data-fld='desc']").sendKeys(which+i);
			Thread.sleep(250);
		}
		report.runStep(true, "Slug and description given to all 3 "+which+" buttons on "+addedTo+".");
	}
	/**
	 * Checks that all fields were added to the doc def
	 * @param fieldTitles
	 * @return
	 * @throws InterruptedException
	 */
	private boolean checkAddedFields(String[][] fieldTitles) throws InterruptedException{
		boolean allAdded = true;
		for(int i = 0; i< fieldTitles.length; i++){
			Thread.sleep(250);
			if(!isElementPresentBy("xpath", "//div[@class='col-md-10']/strong[text()='"+fieldTitles[i][0]+"']")){
				allAdded = false;
				break;
			}
		}
		return allAdded;
	}
	
	private void addComp(String compType) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(addToUI("cmp"))));
		} catch (Exception e1) {
			waitFail(e1);
			
		}
		clickElementBy("xpath", addToUI("cmp"));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(componentSearch));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
		componentSearch.sendKeys(compType);
		componentSearch.sendKeys(Keys.ENTER);
		zetaCMSWait("css",ComponentAdd);
		clickElementBy("css",ComponentAdd);
		Thread.sleep(1000);
	}
	
	/**
	 * @author nwisnewski
	 * Uploads a doc def file
	 * @param docDefTitle
	 * @param baseDocDef
	 * @throws InterruptedException 
	 */
	private void uploadImportFile(String docDefTitle,String wrapper) throws InterruptedException{
		String importId = "docdef_import_json_file";
		String importDocDef = "//form[@class='navbar-form']/a[text()='Import']";
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(importDocDef)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", importDocDef);
		Thread.sleep(250);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(importId)));
		} catch (Exception e) {
			waitFail(e);
		}
		String filePath = System.getProperty("user.dir")+File.separator+"testData"+File.separatorChar+"importtest.docdef";
		if(threadRun.nodeIp().equalsIgnoreCase("local")){
			tempElementBy("id", importId).sendKeys(filePath);
		}else{
			RemoteWebElement remoteImport = (RemoteWebElement) tempElementBy("id", importId);
			remoteImport.setFileDetector(new LocalFileDetector());
			remoteImport.sendKeys(filePath);
		}
		Thread.sleep(500);
		tempElementBy("name", TitleField).sendKeys(docDefTitle);
		verifyTextSent(tempElementBy("name", TitleField), docDefTitle);
		Thread.sleep(1000);
		selectDropDown(selectWrapper, wrapper);
		clickElementBy("xpath", CreateButton);
	}
	/**
	 * Search for doc def
	 * @param title - title of doc def
	 * @throws Exception
	 */
	protected void searchForContent(String title) throws Exception{
		zetaSearch(title, "docdef");
		report.runStep(isElementPresentBy("xpath", editDocDef(title))
				, "The doc def '"+title+"' was located via doc defsearch.");
	}
	
	
}
