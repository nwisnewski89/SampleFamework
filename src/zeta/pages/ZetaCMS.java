package zeta.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.TestSettings;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class ZetaCMS extends Page{
	
	@FindBy(id = "logo")
	protected WebElement projectName;
	
	@FindBy(css = "#show-shortcut>span")
	protected WebElement profileDrpDown;
	
	@FindBy(css = "i.fa.fa-sign-out.fa-4x")
	protected WebElement logOut;
	
	@FindBy(css = "i.fa.fa-user.fa-4x")
	protected WebElement myProfile;
	
	protected static final String CloseTab = ".fa.fa-times.btn-close-tab";
	
	protected static final String AlertDialouge = ".alert.alert-danger";
	
	protected static final String EnvironmentLinks = "//table[@class='table table-bordered table-condensed']/thead/tr/th";
	
	protected static final String RefreshZeta = ".fa.fa-refresh";
	
	protected static final String DummyText = new TestSettings().getDummyText();
	
	protected static final String TitleField = "title";
	
	protected static final String NameField = "name";
	
	protected static final String ZetaSearch = "input.form-control.input-sm";
	//Edit link asset people widget
	protected static final String EditAPW = ".btn.btn-link.btn-xs";
		
	protected WebDriverWait wait = RunFactory.setWait();
	
	public ZetaCMS(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	/**
	 * verifies the expected links appear in the dashboard, expected project name is displayed, expected username is displayed
	 * @param firstName - name of the user logged in (TestingAcct_01
	 * @param project - name of the project
	 * @return
	 * @throws InterruptedException
	 */
	public ZetaCMS validateDashboard(String firstName, String project) throws InterruptedException{
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(projectName.getText().trim().equalsIgnoreCase(project)
				, "Project name is displayed in the top left corner, expected project name displayed '"+project+"' actual displayed '"+projectName.getText()+"'.");
		report.runStep(profileDrpDown.getText().trim().equalsIgnoreCase(firstName)
				, "Name of the user is displayed under the project name, expected user name '"+firstName+"' actual displayed '"+profileDrpDown.getText()+"'.");
		Thread.sleep(500);
		profileDrpDown.click();
		try {
			wait.until(ExpectedConditions.visibilityOf(myProfile));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(browser.findElement(By.id("shortcut")).getAttribute("style").contains("block")&&logOut.isDisplayed()&&myProfile.isDisplayed()
				, "Clicking on the name of the user displayes the Logout and My Profile tiles.");
		String[] dashBoadLinks = {"Documents", "DocDefs", "Environments", "Global", "Media", "People"};
		List<WebElement> links = browser.findElements(By.xpath(DashBoardLinks));
		int i = 0;
		boolean allHere = true;
		String linksDisplayed = "";
		for( WebElement text:links){
			if(!text.getText().trim().equals(dashBoadLinks[i])){
				allHere = false;
				break;
			}
			linksDisplayed += dashBoadLinks[i]+" ";
			i++;
		}
		report.runStep(allHere, "The links to "+linksDisplayed+" are present in the links section.");
		return this;
	}
	/**
	 * Navigates to the DocDef page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the doc def link on dashboard, false -> click doc def link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public DocumentDefinitions navigateToDocDef(boolean fromDashBoard) throws InterruptedException{
		Thread.sleep(2000);
		if(fromDashBoard){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("linktext", "DocDefs");
		}else{
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("Doc Definitions"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath", pageLink("Doc Definitions"));
		}
		report.infoStep("Navigating to the DocDef page");
		//Initializes the doc def object with browser,report,and run details from RunFactory, sets doc def in RunFactory
		return RunFactory.threadSafeDocumentDefinitions(new DocumentDefinitions(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the people page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the people link on dashboard, false -> click people link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public People navigateToPeople(boolean fromDashBoard) throws InterruptedException{
		Thread.sleep(2000);
		if(fromDashBoard){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("linktext", "People");
		}else{
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("People"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath", pageLink("People"));
		}
		report.infoStep("Navigating to the People page");
		//Initializes the people object with browser,report,and run details from RunFactory, sets people in RunFactory
		return RunFactory.threadSafePeople(new People(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the media page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the media link on dashboard, false -> click media link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public Media navigateToMedia(boolean fromDashBoard) throws InterruptedException{
		Thread.sleep(2000);
		if(fromDashBoard){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("linktext", "Media");
		}else{
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("Media"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath", pageLink("Media"));
		}
		report.infoStep("Navigating to the Media page");
		//Initializes the media object with browser,report,and run details from RunFactory, sets media in RunFactory
		return RunFactory.threadSafeMedia(new Media(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the documents page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the documents link on dashboard, false -> click documents link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public Document navigateToDocuments(boolean fromDashBoard) throws InterruptedException{
		Thread.sleep(2000);
		if(fromDashBoard){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("linktext", "Documents");
		}else{
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("Documents"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath", pageLink("Documents"));
		}
		report.infoStep("Navigating to the Documents page");
		//Initializes the documents object with browser,report,and run details from RunFactory, sets documents in RunFactory
		return RunFactory.threadSafeDocument(new Document(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the assets page (link not available in dashboard)
	 * @return
	 * @throws InterruptedException
	 */
	public Assets navigateToAssets() throws InterruptedException{
		Thread.sleep(2000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("Assets"))));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", pageLink("Assets"));
		report.infoStep("Navigating to the Assets page");
		//Initializes the assets object with browser,report,and run details from RunFactory, sets assets in RunFactory
		return RunFactory.threadSafeAssets(new Assets(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the widgets page (link not available in dashboard)
	 * @return
	 * @throws InterruptedException
	 */
	public Widgets navigateToWidgets(){
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
			Assert.fail();
		}
		clickElementBy("xpath", pageLink("Widgets"));
		report.infoStep("Navigating to the Widgets page");
		//Initializes the widgets object with browser,report,and run details from RunFactory, sets widgets in RunFactory
		return RunFactory.threadSafeWidget(new Widgets(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the environments page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the environments link on dashboard, false -> click environments link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public Environment navigateToEnvironment(boolean fromDashBoard) throws InterruptedException{
		Thread.sleep(2000);
		if(fromDashBoard){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("linktext", "Environments");
		}else{
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pageLink("Environments"))));
			} catch (Exception e) {
				waitFail(e);
			}
			clickElementBy("xpath", pageLink("Environments"));
		}
		report.infoStep("Navigating to the Environments page");
		//Initializes the environments object with browser,report,and run details from RunFactory, sets environments in RunFactory
		return RunFactory.threadSafeEnvironment(new Environment(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Navigates to the role management page
	 * @param fromDashBoard - true -> wait for all dashboard links to load clicks the role management link on dashboard, false -> click role management link on side panel
	 * @return
	 * @throws InterruptedException
	 */
	public RoleManagement navigateToRoleManagement(){
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
			Assert.fail();
		}
		clickElementBy("xpath", pageLink("Role Management"));
		report.infoStep("Navigating to the Role Management");
		//Initializes the role management object with browser,report,and run details from RunFactory, sets role management in RunFactory
		return RunFactory.threadSafeRoles(new RoleManagement(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Validates user is restricted to just access documents
	 * @param group - name of the group created in previous test step
	 * @return
	 */
	public Document accessDocuments(String group){
		report.infoStep("Logged into zeta as TestingAcct_02.");
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
		}
		String[] noAccess = {"Doc Definitions","Environments", "Global", "Media", "Assets", "Widgets", "People", "Security"};
		for(int i=0; i<noAccess.length; i++){
			report.runStep(!isElementPresentBy("xpath", pageLink(noAccess[i])), "TestingAcct_02 is a member of '"+group+"', therefore, CAN NOT access '"+noAccess[i]+"'.");
		}
		report.runStep(isElementPresentBy("xpath", pageLink("Documents")), "TestingAcct_02 is a member of '"+group+"', therefore, CAN access Documents.");
		clickElementBy("xpath", pageLink("Documents"));
		//Initializes the documents object with browser,report,and run details from RunFactory, sets documents in RunFactory
		return RunFactory.threadSafeDocument(new Document(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Validates user is restricted to just access doc def
	 * @param group - name of the group created in previous test step
	 * @return
	 */
	public DocumentDefinitions accessDocDecDefs(String group){
		report.infoStep("Logged into zeta as TestingAcct_02.");
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
		}
		String[] noAccess = {"Documents","Environments", "Global", "Media", "Assets", "Widgets", "People", "Security"};
		for(int i=0; i<noAccess.length; i++){
			report.runStep(!isElementPresentBy("xpath", pageLink(noAccess[i])), "TestingAcct_02 is a member of '"+group+"', therefore, CAN NOT access '"+noAccess[i]+"'.");
		}
		report.runStep(isElementPresentBy("xpath", pageLink("Doc Definitions")), "TestingAcct_02 is a member of '"+group+"', therefore, CAN access Doc Definitions.");
		clickElementBy("xpath", pageLink("Doc Definitions"));
		//Initializes the doc def object with browser,report,and run details from RunFactory, sets doc def in RunFactory
		return RunFactory.threadSafeDocumentDefinitions(new DocumentDefinitions(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Validates user is restricted to just access media
	 * @param group - name of the group created in previous test step
	 * @return
	 */
	public Media accessMeda(String group){
		report.infoStep("Logged into zeta as TestingAcct_02.");
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(DashBoardLinks)));
		} catch (Exception e) {
			waitFail(e);
		}
		noAccess("Media", group);
		report.runStep(isElementPresentBy("xpath", pageLink("Media")), "TestingAcct_02 is a member of '"+group+"', therefore, CAN access Media.");
		clickElementBy("xpath", pageLink("Media"));
		//Initializes the media object with browser,report,and run details from RunFactory, sets media in RunFactory
		return RunFactory.threadSafeMedia(new Media(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()));
	}
	/**
	 * Verifies links to other pages in the site are not displayed for restricted user
	 * @param page - the page user should have access to
	 * @param group  - name of the group created in previous test step
	 */
	private void noAccess(String page, String group){
		String[] pageTitle = {"Doc Definitions","Documents","Environments", "Global", "Media", "Assets", "Widgets", "People", "Security"};
		ArrayList<String> pages = new ArrayList<String>();
		for(int i = 0; i < pageTitle.length; i++){
			pages.add(pageTitle[i]);
		}
		pages.remove(page);
		for(int i = 0; i<pages.size();i++){
			System.out.println(pages.get(i));
			report.runStep(!isElementPresentBy("xpath", pageLink(pages.get(i))), "TestingAcct_02 is a member of '"+group+"', therefore, CAN NOT access '"+pages.get(i)+"'.");
		}
	}
	/**
	 * xpath of the link on the side panel
	 * @param page - Fixed: Doc Definitions, Documents, Environments, Global, Media, Assets, Widgets, People, Security
	 * @return
	 */
	private String pageLink(String page){
		return "//li/a[@title='"+page+"']";
	}
	/**
	 * @author nwisnewski
	 * Click on the tabs in any cms page
	 * @param tab: Fixed - browser, app-tab
	 * @return
	 */
	public String zetaPageTabs(String tab){
		return "//ul[@role='tablist']/li/a[contains(@href, '"+tab+"')]";
	}
	
	/**
	 * @author nwisnewski
	 * Polls the publishing of versions
	 * @param content - Fixed: doc, docdef, widget, asset
	 * @throws InterruptedException
	 */
	private boolean isPublished(String page) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td/a[@class='zeta-"+page+"-version-update-name']")));
		} catch (Exception e) {
			report.runStep(false, page+" was not published successfully.");
		}
		String xpath = "//table[@class='table table-bordered table-condensed']/tbody/tr/td/span[contains(text(), 'Version')]";
		return !browser.findElements(By.xpath(xpath)).isEmpty();
	}
	/**
	 * @author nwisnewski
	 * Returns the version of the content published
	 * @return
	 */
	public String version(){
		String xpath = "//table[@class='table table-bordered table-condensed']/tbody/tr/td/span[contains(text(), 'Version')]";
		return browser.findElement(By.xpath(xpath)).getText();
	}
	/**
	 * @author nwisnewski
	 * Returns publish check box of a Doc Def, Widget, and Document
	 * @param page - Fixed: docdef, widget, doc
	 * @return
	 */
	private String publishCheckBox(String page){
		return "//div[contains(@id, 'zeta_"+page+"') and contains(@id, 'history')]/table/tbody/tr[1]/td[3]/input";
	}
	/**
	 *@author nwisnewsk
	 * returns pop update drop down object on widget, document, and doc def pages
	 * @param place - fixed: year, month, day, hour, minute
	 * @return
	 */
	public WebElement dateDropDown(String place){
		return tempElementBy("css", "select."+place+".form-control");
	}
	
	/**
	 * @author nwisnewski
	 * Publishes to each environment available for Doc Def, Widget, and Document
	 * @param page - Fixed: docdef, widget, doc, asset
	 * @return
	 */
	public void publishContent(String page, String title) throws InterruptedException{
		String check = publishCheckBox(page);
		if(page.equalsIgnoreCase("doc")){
			((JavascriptExecutor)browser).executeScript("window.scrollBy(0, 0);");
			clickElementBy("id", "content");
		}
		clickElementBy("css", RefreshZeta);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(check)));
		} catch (Exception e) {
			waitFail(e);
		}
		String xpath = "";
		int environments = 0; 
		if(!page.equals("doc")){
			xpath = EnvironmentLinks+"[contains(@style,'50px;')]";
			environments = browser.findElements(By.xpath(xpath)).size()/2;
		}else{
			xpath = EnvironmentLinks+"/a";
			environments = browser.findElements(By.xpath(xpath)).size();
		}
		clickElementBy("xpath", check);
		Thread.sleep(500);
		String reptPage = "";
		switch(page){
			case "doc":
				reptPage = "Document";
				break;
			case "widget":
				reptPage = "Widget";
				break;
			case "docdef":
				reptPage = "Doc def";
				break;	
			case "asset":
				reptPage = "Asset";
				break;
		}
		report.runStep(isPublished(page), reptPage+"  '"+title+"' was published to the first environment of "+environments+" environment(s) abvailable to project.");
	}
	
	protected boolean endEdition(String editionTitle) throws Exception{
		String setStartDate = "a.combodate.event-start.editable.editable-click";
		String acceptEndDate = "button.btn.btn-primary.btn-sm.editable-submit";
		selectDropDown(tempElementBy("name", "active_edition"), editionTitle);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(setStartDate)));
		} catch (Exception e1) {
			waitFail(e1);
		}
		clickElementBy("css", setStartDate);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOf(dateDropDown("year")));
		} catch (Exception e) {
			waitFail(e);
		}
		selectDropDown(dateDropDown("year"), "2020");
		Thread.sleep(250);
		selectDropDown(dateDropDown("month"), "01");
		Thread.sleep(250);
		selectDropDown(dateDropDown("day"), "01");
		Thread.sleep(250);
		selectDropDown(dateDropDown("hour"), "01");
		Thread.sleep(250);
		selectDropDown(dateDropDown("minute"), "00");
		Thread.sleep(250);
		clickElementBy("css",acceptEndDate);
		Thread.sleep(2000);
		return tempElementBy("css", setStartDate).getText().contains("2020");
	}
	/**
	 * @author nwisnewski
	 * Waits for the cms search to return 1 result.
	 * @param property
	 * @param identifier
	 * @throws InterruptedException
	 */
	protected void zetaCMSWait(String property, String identifier) throws InterruptedException{
		List<WebElement> temp = null;
		int count = 0;
		int exit = 0;
		do{
			Thread.sleep(250);
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
			count = temp.size();
			System.out.println(count);
			exit++;
		}while(count!=1&&exit<240);
		if(exit==240){
			report.runStep(false, "Failed waiting for serch results to filter down to 1.");
		}
	}
	/**
	 * Method for searching throughout the zeta pages, except in the add component and media sections
	 * @param searchText
	 * @param page
	 * @throws Exception
	 */
	protected void zetaSearch(String searchText, String page) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("css", ZetaSearch).sendKeys(searchText);
		tempElementBy("css", ZetaSearch).sendKeys(Keys.ENTER);
		String searchObj = EditAPW;
		//doc def and document have their own edit button
		if(page.contains("doc")){
			searchObj = editDocOrDocDef(page);
		}
		zetaCMSWait("css", searchObj);
	}
	/**
	 * returns the css selector of the edit button of doc and doc def
	 * @param which - Fixed: doc, docdef
	 * @return
	 */
	public String editDocOrDocDef(String which){
		String editLink = which;
		if(which.equalsIgnoreCase("doc")){
			editLink="home";
		}
		return ".btn.btn-primary.btn-xs."+editLink+"-core-buttons"; 
	}
	
	/**
	 * @author nwisnewski
	 * Returns the dropdown object of the select list size
	 * @param page - Fixed: asset, widget, docdefs, people
	 * @return
	 */
	protected WebElement tableLength(String page){
		//return tempElementBy("xpath", "//div[@id='zeta_"+page+"_table_length']/label/select");
		return tempElementBy("css", "div#zeta_"+page+"_table_length.dataTables_length>label>select.form-control.input-sm");
	}
	/**
	 * Clicks the refresh icon
	 * @throws InterruptedException
	 */
	public void refreshTestSession() throws InterruptedException{
		RunFactory.getBrowser().navigate().refresh();
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ZetaSearch)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
	}
	
}
