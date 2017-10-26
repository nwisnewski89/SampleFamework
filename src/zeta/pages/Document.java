
package zeta.pages;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import zeta.utilities.DataManager;
import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.TestSettings;
import zeta.utilities.ZetaTestDriver;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class Document extends ZetaCMS{
	
	@FindBy(id = "createDocSelectDocDef")
	private WebElement docDefDrpDwn;
	
	@FindBy(css = "textarea.custom-scroll.zeta-fld-textarea")
	private WebElement textArea;
	
	@FindBy(css="div.note-editable")
	private WebElement wysiwygTextInput;
	
	private static final String CreateNewDocument = "//div[@class='modal-footer']/button[contains(text(), 'Create')]";
	
	private static final String DeleteDocument = "//div[contains(@id,'zeta_document_navbar_collapse')]/button[text()='Delete Document']";
	
	private static final String DropDownSelection = "//div[@class='selectize-dropdown-content']/div[@class='active']";
	
	private static final String DocTreeLinks = "a.context-link.home-core-buttons";
	
	private static final String DropDownInput = ".selectize-input.items.not-full > input"; 
	
	private static final String TextFieldInput = ".input-lg.form-control.zeta-fld-text";
	
	public Document(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser,report, threadRun);
	}
	/**
	 * Creates a new document
	 * @param title - title of the new document
	 * @param docDef - Doc def the document references
	 * @param baseDocument - Document that the new document is a child of -- most tests this is the homepage set in the porject variables
	 * @return
	 * @throws InterruptedException
	 */
	public Document createNewDocument(String title, String docDef, String baseDocument) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(createDoc(baseDocument))));
		} catch (Exception e) {
			waitFail(e);	
		}
		//create from parent doc
		clickElementBy("xpath", createDoc(baseDocument));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(TitleField)));
		} catch (Exception e) {
			waitFail(e);	
		}
		//adds title
		tempElementBy("name", TitleField).sendKeys(title);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", TitleField), title);
		Thread.sleep(1000);
		//selects doc def to reference
		selectDropDown(docDefDrpDwn, docDef);
		clickElementBy("xpath", CreateNewDocument);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(DeleteDocument)));
		} catch (Exception e) {
			waitFail(e);	
		}
		report.runStep(true, "Created new document '"+title+"' using the doc def '"+docDef+"'.");
		return this;
	}
	/**
	 * Clicks on a document link displayed in the doc grid for the newly created document, verifies the UI, then 
	 * deletes the document.
	 * @param title
	 * @return
	 */
	public Document documentSanityTest(String title){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(zetaPageTabs("browse"))));
		} catch (Exception e) {
			waitFail(e);	
		}
		//Navigates back to doc grid
		clickElementBy("xpath", zetaPageTabs("browse"));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(docLinkInTree(title.toLowerCase()))));
		} catch (Exception e) {
			waitFail(e);	
		}
		//doc link is present in doc grid click on it
		report.runStep(isElementPresentBy("xpath", docLinkInTree(title.toLowerCase())), "Newly created document '"+title+"' is displayed in the document tree.");
		clickElementBy("xpath", docLinkInTree(title.toLowerCase()));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(createDoc(title))));
		} catch (Exception e) {
			waitFail(e);
		}
		//can create child of doc and edit doc
		report.runStep(isElementPresentBy("xpath", createDoc(title))&&isElementPresentBy("xpath", editDoc(title))
				, "The create child of and edit buttons are displayed for document '"+title+"' after clicking the link in the document tree.");
		List<WebElement> environments = browser.findElements(By.xpath("//input[@name='environment']"));
		//can view the doc in multiple environments
		if(!environments.isEmpty()){
			for(WebElement environment:environments){
				report.runStep(true, "Button for the '"+environment.getAttribute("data-slug")+"'  environment is displayed for the document '"+title+"'.");
			}
		}else{
			report.runStep(false, "Environment buttons are missing for the document '"+title+"'.");
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(zetaPageTabs("app-tab"))));
		} catch (Exception e) {
			waitFail(e);	
		}
		//navigates back to edit doc page to delete doc
		clickElementBy("xpath", zetaPageTabs("app-tab"));
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DeleteDocument)));
		} catch (Exception e) {
			waitFail(e);	
		}
		return this;
	}
	/**
	 * Opens an existing doc for editing
	 * @param docTitle - title of doc for editing
	 * @param isTest - if true searches for doc to open, if false any doc is opened
	 * @return
	 * @throws Exception
	 */
	public Document editExistingDoc(String docTitle, boolean isTest) throws Exception{
		if(isTest){
			//searches for doc
			zetaSearch(docTitle, "doc");
			Thread.sleep(500);
			System.out.println(isElementPresentBy("xpath", editDoc(docTitle)));
			report.runStep(tempElementBy("css", editDocOrDocDef("doc")).getAttribute("data-title").equalsIgnoreCase(docTitle)
					, "The document "+docTitle+" was located via document search.");
			//opens doc
			clickElementBy("css",editDocOrDocDef("doc"));
		}else{
			//opens any doc
			Thread.sleep(1000);
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(editDocOrDocDef("doc"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("css", editDocOrDocDef("doc"));
			Thread.sleep(1000);
		}
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(EnvironmentLinks+"/a")));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(true, "The document '"+docTitle+"' has been opened for editing.");
		return this;
	}
	
	/**
	 * Verifies that the title is a required field for a document, then creates new
	 * @param docTitle - title of document
	 * @param baseDocDef - doc def template sepecific to the project
	 * @return
	 * @throws InterruptedException
	 */
	public Document titleRequired(String docTitle, String baseDocDef) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(DocTreeLinks)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String otherDoc = tempElementBy("css", DocTreeLinks).getText();
		clickElementBy("css", DocTreeLinks);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(createDoc(otherDoc))));
		} catch (Exception e) {
			waitFail(e);	
		}
		clickElementBy("xpath", createDoc(otherDoc));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(TitleField)));
		} catch (Exception e) {
			waitFail(e);	
		}
		clickElementBy("xpath", CreateNewDocument);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(AlertDialouge)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String errorMessage = tempElementBy("css", AlertDialouge).getText();
		report.runStep(errorMessage.contains("ValidationError"), "Error message '"+errorMessage+"' when attempting to create a document with no title.");
		clickElementBy("xpath", "//div[@class='modal-footer']/button[contains(text(), 'Cancel')]");
		createNewDocument(docTitle,baseDocDef,otherDoc);
		return this;
	}
	/**
	 * Creates a new edition of an existing doc
	 * @param docEditionTitle - title of new edition
	 * @param docTitle - title of original document
	 * @param docDef - doc def new doc edition references
	 * @return
	 * @throws InterruptedException
	 */
	public Document createNewAutoDocEdition(String docEditionTitle, String docTitle, String docDef) throws InterruptedException{
		String createNewEdition = "//a[contains(@data-modal-link, '/documents/create-edition-modal')]";
		System.out.println(docDef);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(createNewEdition)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
		clickElementBy("xpath", createNewEdition);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(NameField)));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("name", NameField).sendKeys(docEditionTitle);
		Thread.sleep(500);
		verifyTextSent(tempElementBy("name", NameField), docEditionTitle);
		selectDropDown(docDefDrpDwn, docDef);
		clickElementBy("xpath", CreateNewDocument);
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOf(wysiwygTextInput));
		} catch (Exception e) {
			waitFail(e);	
		}
		report.runStep(true, "Created new edition '"+docEditionTitle+"' of the document '"+docTitle+"' using the doc def '"+docDef+"'.");
		return this;
	}
	/**
	 * Ends the document edition
	 * @param docTitle - title of original doc
	 * @param editionTitle - title of edition ending
	 * @return
	 * @throws Exception
	 */
	public Document endDocumentEdition(String docTitle, String editionTitle) throws Exception{
		boolean editionEnded = endEdition(editionTitle);
		report.runStep(editionEnded
				, "The documnet edition '"+editionTitle+"' was set to start in 2020, UI of document '"+docTitle+"' should revert back to original values.");
		return this;
	}
	/**
	 * Adds a video to a video selector in document
	 * @param videoTitle - title of video to add
	 * @param thumbNail - source url of thumbnail image
	 * @return
	 * @throws Exception
	 */
	public Document addVideoToDoc(String videoTitle, String thumbNail) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(DropDownInput)));
		} catch (Exception e) {
			waitFail(e);
		}
		//send video title
		tempElementBy("css", DropDownInput).sendKeys(videoTitle);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DropDownSelection)));
		} catch (Exception e) {
			waitFail(e);	
		}
		//select video from drop down list
		clickElementBy("xpath",DropDownSelection);
		Thread.sleep(1000);
		String videoThumbnailXpath = "//img[@class='video_image']";
		System.out.println(isElementPresentBy("xpath", videoThumbnailXpath));
		System.out.println(tempElementBy("xpath", videoThumbnailXpath).getAttribute("src"));
		//check video is selected, and thumbnail url matches expected
		report.runStep(isElementPresentBy("xpath", videoThumbnailXpath)&&tempElementBy("xpath", videoThumbnailXpath).getAttribute("src").equalsIgnoreCase(thumbNail)
				, "The video '"+videoTitle+"' was added the video field of the document, and the thumbnail added to the video in the media section is displayed for the selected video.");
		return this;
	}
	/**
	 * Adds an image to a document
	 * @param imageTitle - title of the image to be added
	 * @param seperateTab - if true the image selector is in a separate tab in the doc
	 * @return
	 * @throws InterruptedException
	 */
	public Document addImageToDoc(String imageTitle, boolean seperateTab) throws InterruptedException{
		WebElement imageInput = null;
		if(seperateTab){
			imageInput = tempElementBy("css", DropDownInput);
		}else{
			imageInput = tempElementBy("xpath", "//select[@name='image']/following-sibling::div/div/input");
		}
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(imageInput));
		} catch (Exception e) {
			waitFail(e);	
		}
		//send image title
		imageInput.sendKeys(imageTitle);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DropDownSelection)));
		} catch (Exception e) {
			waitFail(e);	
		}
		//selects image
		clickElementBy("xpath",DropDownSelection);
		Thread.sleep(1000);
		System.out.println(tempElementBy("css", "img").getAttribute("src"));
		//verifies the image was selected
		report.runStep(!tempElementBy("css", "img").getAttribute("src").isEmpty(), "The image '"+imageTitle+"' was added to the image field of document.");
		return this;
	}
	/**
	 * Adds a person to an author selector
	 * @param personName - name of the author
	 * @return
	 * @throws InterruptedException
	 */
	public Document addAuthorToDoc(String personName) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(DropDownInput)));
		} catch (Exception e) {
			waitFail(e);
		}
		//sends author name
		tempElementBy("css",DropDownInput).sendKeys(personName);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DropDownSelection)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(500);
		//selects author
		clickElementBy("xpath",DropDownSelection);
		Thread.sleep(1000);
		String authorName = tempElementBy("xpath", "//div[@class='people']/span[@class='name']").getText();
		System.out.println(authorName);
		report.runStep(authorName.toLowerCase().contains(personName.toLowerCase())
				, "The author '"+authorName+"' was added to the document.");
		return this;
	}
	/**
	 * Adds text to a text area component
	 * @param text - text to add, if "" then dummy text is added
	 * @return
	 * @throws InterruptedException
	 */
	public Document addToTextArea(String text) throws InterruptedException{
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(textArea));
		} catch (Exception e) {
			waitFail(e);	
		}
		textArea.clear();
		String reptText = text;
		if(text.equals("")){
			text = DummyText;
			reptText = "Lorem ipsum dummy";
		}
		textArea.sendKeys(text);
		report.runStep(true,reptText+" text was added to the text area of document.");
		return this;
	}
	/**
	 * Adds text in bold to a wysiwyg
	 * @param name - label above the wysiwyg, which is the slug of the wysiwyg field
	 * @param text - text to add, if "" then dummy text is added
	 * @return
	 * @throws InterruptedException
	 */
	public Document addToWysiwyg(String name, String text) throws InterruptedException{
		Thread.sleep(1000);
		String wysiwygXpath = "//label[text()='"+name+"']/following-sibling::div/div[@class='note-editable']";
		String boldText = "//label[text()='"+name+"']/following-sibling::div/div/div/button[@data-event='bold']";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(wysiwygXpath)));
		} catch (Exception e) {
			waitFail(e);	
		}
		WebElement wysiwygTextInput = tempElementBy("xpath", wysiwygXpath);
		wysiwygTextInput.click();
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(boldText)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", boldText);
		int i = 0;
		//Verifies text will be added in bold
		while(!tempElementBy("xpath", boldText).getAttribute("class").contains("active")&&i<3){
			System.out.println(tempElementBy("xpath", boldText).getAttribute("class"));
			i++;
		}
		if(i==3){
			report.runStep(false, "Did not bold wysiwyg font! Run error.");
		}
		String reptText = text;
		if(text.equals("")){
			text = DummyText;
			reptText = "Lorem ipsum dummy";
		}
		wysiwygTextInput.click();
		wysiwygTextInput.sendKeys(text);
		Thread.sleep(500);
		System.out.println(wysiwygTextInput.getText());
		System.out.println(text.trim());
		i = 0;
		//Verifies the full text was added
		while(!wysiwygTextInput.getText().trim().equalsIgnoreCase(text.trim())&&i<4){
			wysiwygTextInput.clear();
			wysiwygTextInput.sendKeys(text);
			Thread.sleep(250);
			System.out.println(wysiwygTextInput.getText());
			i++;
		}
		report.runStep(wysiwygTextInput.getText().trim().equalsIgnoreCase(text.trim())&&i!=4, reptText+" text was added in bold font to the wysiwyg note editor '"+name+"' of document.");
		return this;
	}
	/**
	 * Adds text to a text field
	 * @param fieldName - label above the text field, which is the slug of the text field
	 * @param text - text to add
	 * @return
	 * @throws InterruptedException
	 */
	public Document addToTextField(String fieldName, String text) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(TextFieldInput)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
		textField(fieldName).clear();
		textField(fieldName).sendKeys(text);
		Thread.sleep(500);
		report.runStep(true, "'"+text+"' was added to the text field '"+fieldName+"' of document.");
		return this;
	}
	/**
	 * Adds to a all fields for the repeater test
	 * @param numRows - number of rows of the repeater
	 * @param rowComp - array of components contained in repeater
	 * @return
	 * @throws Exception
	 */
	public Document addRepeater(int numRows, String[] rowComp) throws Exception{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.addRow")));
			System.out.println("Can click?");
		} catch (Exception e) {
			waitFail(e);
		}
		//clicks the add row button the number of times specified
		for(int i = 0; i<numRows; i++){
			Thread.sleep(500);
			System.out.println("Click " +i);
			clickElementBy("css", "input.addRow");
		}
		report.runStep(browser.findElements(By.xpath("//section[@class='dataList']")).size()==numRows, "Added "+numRows+" rows of the repeater.");
		for(int i = 0; i<rowComp.length; i++){
			//iterates over array, goes into switch statement to call method to add values to components
			callComponentMethod(numRows, rowComp[i]);
		}
		return this;
	}
	/**
	 * Adds to todays date to a date field in document
	 * @param dateLabel - label of datefield
	 * @return
	 * @throws InterruptedException
	 */
	public Document todayInDateField(String dateLabel) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dateFieldInput(dateLabel))));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(500);
		clickElementBy("xpath", dateFieldInput(dateLabel));
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(selectToday(dateLabel))));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath",selectToday(dateLabel));
		Date today = new Date();
		SimpleDateFormat frmt = new SimpleDateFormat("MMMMM dd, yyyy");
		String value = frmt.format(today).toUpperCase();
		System.out.println(value);
		System.out.println(tempElementBy("xpath",dateFieldInput(dateLabel)).getAttribute("value"));
		report.runStep(tempElementBy("xpath",dateFieldInput(dateLabel)).getAttribute("value").equalsIgnoreCase(value), "The date "+value+" was selected from the date selector with label '"+dateLabel+"'.");
		return this;
	}
	/**
	 * method to switch tabs on the doc page
	 * @param tabTitle - name displayed on tab
	 * @return
	 * @throws InterruptedException
	 */
	public Document switchTab(String tabTitle) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(docTabs(tabTitle))));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", docTabs(tabTitle));
		Thread.sleep(1000);
		return this;
	}
	/**
	 * uploads an image from a file system directly to a document
	 * @param imageFile - image file to upload
	 * @param imageTitle - title of image uploaded
	 * @return
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public Document uploadImageFromFileSysToDoc(String imageFile, String imageTitle) throws Exception, InterruptedException{
		String imgUploadButton = "a.btn.btn-default.col-lg-1.col-sm-2.doc-level-img-upload-btn";
		RunFactory.threadSafeMedia(new Media(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(imgUploadButton)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("css",  imgUploadButton);
		Thread.sleep(1000);
		RunFactory.thisMedia().uploadImageFile(imageFile, imageTitle, "documnets");
		report.runStep(!tempElementBy("css", "img").getAttribute("src").isEmpty(), "The image '"+imageTitle+"' uploaded in previous step was added to the image field of the document.");
		return this;
	}
	/**
	 * adds alternate text to an image in a document
	 * @param imageTitle
	 * @param altText
	 * @return
	 * @throws InterruptedException
	 */
	public Document addAltTextToImage(String imageTitle, String altText) throws InterruptedException{
		String altTextInput = "input.zeta-img-fld-alt-txt";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(altTextInput)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(isElementPresentBy("css", altTextInput), "Alternate text input field apears once image is selected.");
		Thread.sleep(500);
		browser.findElement(By.cssSelector(altTextInput)).sendKeys(altText);
		report.infoStep("The alternate text '"+altText+"' was added to the image '"+imageTitle+"' in the body of the document." );
		return this;
	}

	/**
	 * deletes a doc
	 * @param title - title of deleted doc
	 */
	public void deleteDocument(String title){
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DeleteDocument)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", DeleteDocument);
		try {
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception e) {
			waitFail(e);
			
		}
		browser.switchTo().alert().accept();
		report.runStep(true, "Document '"+title+"' was deleted successfully");
	}
	/**
	 * switch statement to add values to components in repeater
	 * @param row - how many rows in the repeater
	 * @param component - compent to add values to
	 * @throws Exception
	 */
	private void callComponentMethod(int row, String component) throws Exception{
		switch(component.toLowerCase()){
		case "title":
			addTitleRepeater(row);
			break;
		case "author":
			addAuthorToRepeater(row);
			break;
		case "radio":
			addRadioOrCheckboxToRepeater(row, "radio");
			break;
		case "check":
			addRadioOrCheckboxToRepeater(row, "checkbox");
			break;
		}
	}
	/**
	 * Adds a title to multiple rows in repeater
	 * @param row - number of rows in repeater
	 * @throws InterruptedException
	 */
	private void addTitleRepeater(int row) throws InterruptedException{
		for(int i = 1; i<=row; i++){
			Thread.sleep(500);
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(titlePathByRow(i))));
			}catch (Exception e){
				waitFail(e);
			}
			tempElementBy("xpath", titlePathByRow(i)).sendKeys("Title Row "+i);
			Thread.sleep(500);
			report.runStep(true, "Text was added to the title of "+i+" row in the repeater.");
		}
	}
	/**
	 * Adds an author created by a the automation to the rows of a repeater 
	 * @param row - number of rows in repeater
	 * @throws InterruptedException
	 */
	private void addAuthorToRepeater(int row) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(DropDownInput)));
		} catch (Exception e) {
			waitFail(e);
		}
		List<WebElement> authorInputs = null;
		int index = 0;
		for(int i = 1; i<=row; i++){
			//gets the list of the author selectors
			authorInputs = browser.findElements(By.cssSelector(DropDownInput));
			System.out.println(authorInputs.size());
			//once author is entered the size of this list changes,
			//adds to the bottom first, then adds the second from last and so on
			index =authorInputs.size()-1;
			System.out.println(index);
			Thread.sleep(500);
			try {
				wait.until(ExpectedConditions.elementToBeClickable(authorInputs.get(index)));
			} catch (Exception e) {
				waitFail(e);
			}
			//scrolls to author input
			int y = authorInputs.get(index).getLocation().getY()-300;
			((JavascriptExecutor)browser).executeScript(" window.scrollBy(0, "+y+");");
			Thread.sleep(500);
			authorInputs.get(index).click();
			Thread.sleep(500);
			//select one of the authors created by automation
			authorInputs.get(index).sendKeys("auto");
			Thread.sleep(250);
			//select author from dropdown
			try {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DropDownSelection)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath",DropDownSelection);
			Thread.sleep(500);
			report.runStep(true, "An author was selected for the "+i+" row of the repeater.");
			((JavascriptExecutor)browser).executeScript("window.scrollTo(0, 0);", "");
		}
	}	
	/**
	 * Checks a checkbox or radio button
	 * @param row - number of rows in repeater
	 * @param which - Fixed: radio, check
	 * @throws InterruptedException
	 */
	private void addRadioOrCheckboxToRepeater(int row, String which) throws InterruptedException{
		for(int i = 1; i<=row;i++){
			System.out.println(i);
			try {
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(reptRadAndCheck(i, which))));
			} catch (Exception e) {
				waitFail(e);	
			}
			Thread.sleep(1000);
			//checks the checkbox/radio, which == ("check"||"radio")
			clickElementBy("xpath",reptRadAndCheck(i, which));
			report.runStep(true, "The "+i+" "+which+" was enabled on the "+i+" row of the repeater.");
		}	
	}
	/**
	 * xpath of title field based on row of repeater
	 * @param i - row of repeater
	 * @return
	 */
	private String titlePathByRow(int i){
		return "//section[@class='dataList']["+i+"]/fieldset/label/input";
	}
	
	/**
	 * xpath of checkbox or radio button 
	 * @param i - row of repeater
	 * @param which - Fixed: radio, checl
	 * @return
	 */
	private String reptRadAndCheck(int i, String which){
		return "//section[contains(@class, 'dataList')]["+i+"]/fieldset/section[@class='"+which+"']/div["+i+"]/label";
	}
	/**
	 * xpath of the create button of the homepage document
	 * @param baseDoc - homepage document of project
	 * @return
	 */
	private String createDoc(String baseDoc){
		return "//div[@data-title='"+baseDoc+"']/div/a[@data-btn-action='create']";
	}
	/**
	 * xpath of the edit button of the homepage document
	 * @param baseDoc - homepage document of project
	 * @return
	 */
	private String editDoc(String baseDoc){
		return "//div[@data-title='"+baseDoc+"']/div/a[@data-btn-action='edit']";
	}
	/**
	 * xpath of hyperlink in the doc grid
	 * @param docTitle
	 * @return
	 */
	private String docLinkInTree(String docTitle){
		return "//tr/td/a[contains(@href,'/"+docTitle+"')]";
	}
	/**
	 * xpath of the tabs displayed in the document -- set in the field of document
	 * @param tabTitle - title of the tab
	 * @return
	 */
	private String docTabs(String tabTitle){
		String ariaControls = tabTitle.toLowerCase().replace(" ", "-");
		return "//div[@role='tabpanel']/ul/li/a[@aria-controls='"+ariaControls+"']";
	}
	/**
	 * xpath of date selector based on the label 
	 * @param dateLabel - label over date selector
	 * @return
	 */
	private String dateFieldInput(String dateLabel){
		return "//label[text()='"+dateLabel+"']/following-sibling::div/input[contains(@class, 'hasDatepicker')]";
	}
	/**
	 * xpath of todays date based on label of date selector
	 * @param dateLabel - label over date selector
	 * @return
	 */
	private String selectToday(String dateLabel){
		return "//label[text()='"+dateLabel+"']/following-sibling::div/div/table/tbody/tr/td[contains(@class,' ui-datepicker-today')]";
	}
	/** 
	 * Returns  web element for text field based on the label of the text field
	 * @param name
	 * @return
	 */
	private WebElement textField(String name){
		WebElement textField = null;
		for(WebElement field: browser.findElements(By.cssSelector(TextFieldInput))){
			System.out.println(field.getAttribute("name"));
			if(field.getAttribute("name").equals(name.toLowerCase().replace(" ", "-"))){
				System.out.println("Got text field");
				textField = field;
				break;
			}
		}
		if(textField == null){
			report.runStep(false, "Could not locate text field "+name+" in the document.");
		}
		return textField;
	}
	/**
	 * Click on the link to the environment which the document was published to 
	 * to view the UI
	 * @param docTitle 
	 * @param docDef
	 * @return - UIValidation object
	 * @throws InterruptedException
	 */
	public UIValidation openDocument(String docTitle, String docDef) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(EnvironmentLinks+"/a")));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(2000);
		clickElementBy("xpath", EnvironmentLinks+"/a");
		Thread.sleep(1000);
		long start = System.currentTimeMillis();
		long delta = 0;
		ArrayList<String> tabs = null;
		//waits for new tab
		do{
			Thread.sleep(250);
			tabs = new ArrayList<String>(browser.getWindowHandles());
			delta = (System.currentTimeMillis()-start)/1000;
			System.out.println("Windows - "+tabs.size()+": Wait time: "+delta);
		}while(tabs.size()<2&&delta<30);
		browser.switchTo().window(tabs.get(1));
		//will cause error on mac chrome because of the way window is maximized in initializing the remote webdriver
		if(!(RunFactory.getRunDetails().runOS().equalsIgnoreCase("mac")&&RunFactory.getRunDetails().runBrowser().equalsIgnoreCase("chrome"))){
			browser.manage().window().maximize();
		}
		Thread.sleep(1000);
		System.out.println(browser.getCurrentUrl());
		//verifies the title is in the url 
		report.runStep(browser.getCurrentUrl().contains(docTitle.toLowerCase()), "The UI of document '"+docTitle+"' using doc def '"+docDef+"' has been opened.");
		Thread.sleep(3000);
		//initializes the threadlocal UI in run factory
		return RunFactory.threadSafeUi(new UIValidation(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
}

