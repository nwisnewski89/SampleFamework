package zeta.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class UIValidation extends ZetaCMS{
	
	private static final String TextArea = ".post-content";
	

	
	public UIValidation(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser, report, threadRun);
	}
	/**
	 * widget test validation
	 * @param docTitle
	 */
	public void validateDocWithWidget(String docTitle){
		System.out.println(browser.getCurrentUrl());
		String wysiwygUiXpath = expectedRegion("Widget Region")+expectedRow("Widget Row")+expectedColumn("WYSIWYG Column");
		String wysiwyg = wysiwygCss();
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(wysiwyg)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String wysiwygUiText = tempElementBy("css", wysiwyg).getText();
		report.runStep(isElementPresentBy("css", wysiwyg)&&wysiwygUiText.equals(DummyText)&&isElementPresentBy("xpath", wysiwygUiXpath)
				, "Wysywyg component is displayed in UI with Lorem impsum dummy text in bold font; The region, row and column created in widget to contain wysisyg is present in the html.");
		String textAreaUiXpath = expectedRegion("Widget Region")+expectedRow("Widget Row")+expectedColumn("Text Column");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(TextArea)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String textAreaUiText = tempElementBy("css", TextArea).getText();
		report.runStep(isElementPresentBy("css",TextArea)&&textAreaUiText.equals(DummyText)&&isElementPresentBy("xpath",textAreaUiXpath)
				, "Text Area component is displayed in UI of '"+docTitle+"' with Lorem impsum dummy text; The region, row and column created in widget to contain text area is present in the html.");
		
	}
	/**
	 * header test validation
	 * @param headerTitle
	 * @param docTitle
	 * @return
	 */
	public UIValidation headerPresent(String headerTitle, String docTitle){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("headerTest")));
		} catch (Exception e) {
			waitFail(e);	
		}
		String headerContainerXpath = expectedRegion("Header Region")+expectedRow("Header Row");
		String headerText = tempElementBy("id", "headerTest").getText();
		report.runStep(isElementPresentBy("id", "headerTest")&&headerText.equals(headerTitle)&&isElementPresentBy("xpath", headerContainerXpath)
				,"The expected header title '"+headerTitle+"' is displayed in the UI of '"+docTitle+"'; "
				+ "The region and row created in doc definition to contain header is present in the html.");
		return this;
	}
	/**
	 * wywiwyg test validation
	 * @param docTitle
	 * @param text
	 * @return
	 */
	public UIValidation wysiwyg(String docTitle, String text){
		String wysiwyg = wysiwygCss();
		System.out.println(wysiwyg);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(wysiwyg)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String reptText = text;
		if(text.equals("")){
			text = DummyText;
			reptText = "Lorem impsum dummy";
		}
		System.out.println(tempElementBy("css", wysiwyg).getText());
		System.out.println(text);
		report.runStep(tempElementBy("css", wysiwyg).getText().equals(text)
				, "Wysiwyg component is displayed in the UI of '"+docTitle+"' with '"+reptText+"' text with bold font.");
		return this;
	}
	/**
	 * Check the image and text area are contained in column and row as added in the doc def ui
	 * @param imageTitle
	 * @param docTitle
	 * @param text
	 * @return
	 */
	public UIValidation imageTextRow(String imageTitle, String docTitle, String text){
		String colCss = ".cmp-column.col-xs-12.col-sm-12.col-md-6.col-lg-6";
		String imageCss = colCss+">img";
		String textAreaCss = colCss+">section>"+TextArea;
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(imageCss)));
		} catch (Exception e) {
			waitFail(e);	
		}
		String imageContainerXpath = expectedRegion("DocDef Region")+expectedRow("DocDef Row")+expectedColumn("Image Column");
		System.out.println(tempElementBy("css", imageCss).getAttribute("src"));
		report.runStep(!tempElementBy("css", imageCss).getAttribute("src").isEmpty()&&isElementPresentBy("xpath", imageContainerXpath)
				, "The image '"+imageTitle+"' is displayed in the UI of '"+docTitle+"'; The region, row and column created in doc def to contain the image is present in the html.");
		String textAreaXpath = expectedRegion("DocDef Region")+expectedRow("DocDef Row")+expectedColumn("Text Column");
		String textAreaText = tempElementBy("css", textAreaCss).getText();
		System.out.println(textAreaText);
		System.out.println(text);
		String reptText = text;
		if(text.equals("")){
			text = DummyText;
			reptText = "Lorem impsum dummy";
		}
		report.runStep(isElementPresentBy("css", textAreaCss)&&textAreaText.equals(text)&&isElementPresentBy("xpath", textAreaXpath)
				, "Text area component is displayed in the UI of '"+docTitle+"' with '"+reptText+"' text; The region, row and column created in doc def to contain the text area is present in the html.");
		return this;
	}
	/**
	 * checks that dec def edition with just an image displays
	 * @param imageTitle
	 * @param docTitle
	 * @param docDef
	 * @param docDefEdition
	 */
	public void docDefEditionTest(String imageTitle, String docTitle, String docDef, String docDefEdition){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(imageCss())));
		} catch (Exception e) {
			waitFail(e);	
		}
		report.runStep(!(tempElementBy("css", imageCss()).getAttribute("src").isEmpty()||isElementPresentBy("css", wysiwygCss()))
				, "The image '"+imageTitle+"' is displayed in the UI of '"+docTitle+"'"
				+ ";The edition '"+docDefEdition+"' of doc def '"+docDef+"' renders in the UI correctly because just the image component is displayed.");
	}
	/**
	 * Checks the document created by test createNewDocDefAndDocument reverts back to original state after the edition of 
	 * doc def is ended
	 * @param docTitle
	 * @param docDefTitle
	 * @param docDefEdition
	 * @param imageTitle
	 * @throws Exception
	 */
	public void editionEnded(String docTitle, String docDefTitle, String docDefEdition, String imageTitle) throws Exception{
		int i = 0;
		boolean editionUpdated = true;
		do{
			//Check for DocDef edition
			if(browser.findElements(By.cssSelector(wysiwygCss())).isEmpty()){
				RunFactory.getBrowser().navigate().refresh();
				Thread.sleep(2000);
			//Check for document edition
			}else if(browser.findElement(By.cssSelector(wysiwygCss())).getText().contains("Edition testing")){
				RunFactory.getBrowser().navigate().refresh();
				Thread.sleep(2000);
			}else{
				editionUpdated = false;
			}
			i++;
		}while(editionUpdated&&i<4);
		String parseTag = docDefTitle.split("f")[1];
		headerPresent("Auto Test "+parseTag, docTitle);
		wysiwyg(docTitle, "");
		imageTextRow(imageTitle, docTitle, "");
		report.runStep(true, "The doc def edition '"+docDefEdition+"' is no longer displayed. The UI of document '"+docTitle+"' references original doc def '"+docDefTitle+"'.");
	}
	/**
	 * Checks the document displays the new values entered in the new edition
	 * @param docTitle
	 * @param docEditionTitle
	 * @param editionText
	 * @param imageTitle
	 */
	public void documentEditionTest(String docTitle, String docEditionTitle, String editionText, String imageTitle){
		wysiwyg(docTitle, editionText);
		imageTextRow(imageTitle, docTitle,editionText);
		report.runStep(true, "The document edition '"+docEditionTitle+"' of document '"+docTitle+"' is displayed correctly.");
	}
	/**
	 * Checks the document created by test createNewDocDefAndDocument reverts back to original state after the edition of 
	 * document is ended
	 * @param docTitle
	 * @param docEditionTitle
	 * @param imageTitle
	 */
	public void documentEditionEnded(String docTitle, String docEditionTitle,String imageTitle){
		wysiwyg(docTitle, "");
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(imageCss())));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(!tempElementBy("css", imageCss()).getAttribute("src").isEmpty(), "The image '"+imageTitle+"' is displayed in the Ui of document '"+docTitle+"'.");
		report.runStep(true, "The document edition '"+docEditionTitle+"' of document '"+docTitle+"' is no longer displayed. The UI reference original document with Lorem ipsum dummy text and the image '"+imageTitle+"'.");
	}
	/**
	 * Checks the story title in document UI for each row added to the repeater
	 * @param docTitle 
	 * @param row - number of rows in the repeater
	 * @return
	 */
	public UIValidation repeaterStoryTitle(String docTitle, int row){
		int i = 1;
		String storyTitle = ".page-header.post-header>h1";
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(storyTitle)));
		} catch (Exception e) {
			waitFail(e);
		}
		List<WebElement> rowTitles = browser.findElements(By.cssSelector(storyTitle));
		System.out.println(rowTitles.size());
		report.runStep(rowTitles.size()==row, "The number of story title components displayed in the UI of '"+docTitle+"' is'"+rowTitles.size()+"', which should equal the number added to document '"+row+"'.");
		for(WebElement title: rowTitles){
			String rowText = "Title Row "+i;
			System.out.println(title.getText().trim());
			report.runStep(title.getText().trim().equals(rowText)
					, "Text entered for the title of row '"+i+"' should be '"+rowText+"', title displayed for row '"+i+"' is '"+title.getText()+"'.");
			i++;
		}
		return this;
	}
	/**
	 * Checks the check box/radio selections are displayed in the document UI for each row of the repeater
	 * @param docTitle
	 * @param row - number of rows in the repeater
	 * @param which - Fixed: check box, radio
	 * @return
	 */
	public UIValidation repeaterCheckboxAndRadio(String docTitle, int row, String which){
		int i = 1;
		String checkOrRadXapth = "//section/div[contains(text(), '"+which.toLowerCase()+"')]";
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(checkOrRadXapth)));
		} catch (Exception e) {
			waitFail(e);
		}
		List<WebElement> checkOrRad = browser.findElements(By.xpath(checkOrRadXapth));
		report.runStep(checkOrRad.size()==row, "The number of selected "+which+" displayed in the UI of '"+docTitle+"' is'"+checkOrRad.size()+"', which should equal the number selected in document '"+row+"'.");
		for(WebElement text: checkOrRad){
			report.runStep(text.getText().trim().equals(which.toLowerCase()+i), "The "+which+" selected for row "+i+" was "+which.toLowerCase()+i+", the "+which+" displayed for row "+i+" is "+text.getText().trim()+"'.");
			i++;
		}
		return this;
	}
	/**
	 * Checks an author is displayed in the document UI for each row of the repeater
	 * @param docTitle
	 * @param row
	 * @return
	 */
	public UIValidation repeaterAuthor(String docTitle, int row){
		int i = 1;
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".post-author-info")));
		} catch (Exception e) {
			waitFail(e);
		}
		List<WebElement> rowAuthor = browser.findElements(By.cssSelector(".post-author-info"));
		List<WebElement> authorPic = browser.findElements(By.cssSelector(".post-author-info > img"));
		System.out.println(rowAuthor.size());
		report.runStep(rowAuthor.size()==row, "The number of story authors displayed in the UI of '"+docTitle+"' is'"+rowAuthor.size()+"', which should equal the number added to document '"+row+"'.");
		for(WebElement author: rowAuthor){
			boolean imgDisplayed = !authorPic.get(i-1).getAttribute("src").isEmpty();
			String authorName =author.getText().trim().replace("|", "");
			System.out.println(authorName);
			report.runStep(authorName.toLowerCase().contains("auto")&&imgDisplayed
					, "The author of row '"+i+"' should display with name and image, the author of the row is '"+authorName+"' and author image is present '"+imgDisplayed+"'.");
			i++;
		}
		return this;
	}
	/**
	 * Verifies the corporate bio page is displayed in expected layout with the content added to document in previous step.
	 * Only Corporate project
	 * @param docTitle
	 * @param imageTitle
	 * @param name - Test Name given to name field in document
	 * @param jobTitle -Job title given to the field in document
	 * @param wysiwygText - text entered in wysiwyg field of document
	 * @return
	 */
	public UIValidation corprateBioPage(String docTitle, String imageTitle, String name, String jobTitle, String wysiwygText){
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("cmp-corporate-bio-headshot-hero")));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(isElementPresentBy("xpath", "//div[@class='gradient gray']"), "Corporate bio headshote hero is displayed with a grey gradient in UI of bio page document.");
		String bioInfo = tempElementBy("xpath", "//div[@class='container']/div/h2").getText().toLowerCase();
		System.out.println(bioInfo);
		System.out.println(tempElementBy("xpath", "//div[@class='container']/img").getAttribute("src"));
		boolean nameAndJobTitleDisplayed = bioInfo.contains(name.toLowerCase())&&bioInfo.contains(jobTitle.toLowerCase());
		System.out.println(nameAndJobTitleDisplayed);
		report.runStep(isElementPresentBy("xpath", "//div[@class='container']/img")&&nameAndJobTitleDisplayed
				, "Head shot hero contains Name, Job Title Auto Tester, QA Engineer and an image entered in UI of bio page document '"+docTitle+"'.");
		String reptText = "Lorem ipsum dummy";
		String pageText = DummyText;
		if(!wysiwygText.equals("")){
			reptText = "'"+wysiwygText+"'";
			pageText = wysiwygText;
		}
		report.runStep(tempElementBy("css", wysiwygCss()).getText().equals(pageText), reptText+" text is displayed in content section of the UI of bio page documnet '"+docTitle+"'");
		report.runStep(isElementPresentBy("css", ".addthis_button"), "Share print corporate component is displayed in UI of bio page document.");
		return this;
	}
	/**
	 * Validates the css or js asset added to the doc def is present in the 
	 * html of the UI of document referencing cede doc def
	 * @param docTitle
	 * @param assetTitle
	 * @return
	 */
	public UIValidation assetTest(String docTitle, String assetTitle){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("headerTest")));
		} catch (Exception e) {
			waitFail(e);	
		}
		String assetXpath = "";
		String rept = "";
		if(assetTitle.toLowerCase().contains("js")){
			assetXpath = "//head/script[@src='/assets/js/"+assetTitle+".js']";
			rept = "Javascript";
		}else{
			assetXpath = "//head/link[@href='/assets/css/"+assetTitle+".css']";
			rept = "Css";
		}
		System.out.println(isElementPresentBy("xpath", assetXpath));
		report.runStep(isElementPresentBy("xpath", assetXpath), "The "+rept+" asset '"+assetTitle+"' is displayed in the header of document '"+docTitle+"'.");
		return this;
	}
	/**
	 * Verifies the video component is displayed in the UI
	 * @param docTitle
	 * @param videoTitle
	 * @return
	 */
	public UIValidation videoComponentTest(String docTitle, String videoTitle){
		String videoXpath = "//div/video[@id='cmp_video_html5_api']";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(videoXpath)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(isElementPresentBy("xpath", videoXpath), "The video '"+videoTitle+"' is displayed in the UI of the document '"+docTitle+"'.");
		return this;
	}
	/**
	 * Validate the alt text added to an image of a document is 
	 * present in the html of the UI of the documnet
	 * @param docTitle
	 * @param image
	 * @param altText
	 * @return
	 */
	public UIValidation altTextImageTest(String docTitle, String image, String altText){
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(imageCss())));
		} catch (Exception e) {
			waitFail(e);
		}
		System.out.println(tempElementBy("css",imageCss()).getAttribute("alt"));
		report.runStep(tempElementBy("css",imageCss()).getAttribute("alt").equalsIgnoreCase(altText)	
				, "The image '"+image+"' is displayed in the document '"+docTitle+"' should have the alternate text '"+altText+"', does have the alternate text '"+tempElementBy("css",imageCss()).getAttribute("alt")+"'.");
		return this;
	}
	/**
	 * Difference between browsers
	 * @return - css selector of wysiwyg object in UI of document
	 */
	private String wysiwygCss(){
		String wysiwyg = "";
		if(RunFactory.getRunDetails().runBrowser().equalsIgnoreCase("ie")){
			wysiwyg = TextArea+">strong";
		}else{
			wysiwyg = TextArea+">span";
		}
		return wysiwyg;
	}
	/**
	 * Verifies the corporate DI Page is displayed in expected layout with the content added to document in previous step.
	 * Only DNI project
	 * @param docTitle
	 * @param imageTitle
	 * @param imageUrl - source url of the image, used to check image in the hero component
	 * @return
	 */
	public UIValidation diPageValidation(String docTitle, String imageTitle, String imageUrl){
		String heroImage = "section.cmp-hero-fixed.hero-xl";
		String title = "div.page-header.post-header>h1";
		String body = "div.story-body";
		String ccFooter = "div.story-copyright>div.comcast-logo-light";
		String news = "div.latest-news-item";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(heroImage)));
		} catch (Exception e) {
			waitFail(e);
		}
		System.out.println(tempElementBy("css",heroImage).getAttribute("style").contains(imageUrl));
		report.runStep(tempElementBy("css",heroImage).getAttribute("style").contains(imageUrl)
				, "The image '"+imageTitle+"' is displayed as the hero image in document '"+docTitle+"'.");
		report.runStep(tempElementBy("css",title).getText().equalsIgnoreCase(docTitle)
				, "The expected title is '"+docTitle+"', title displayed is '"+tempElementBy("css",title).getText()+"'.");
		report.runStep(tempElementBy("css",body).getText().equalsIgnoreCase(DummyText)
				, "The lorem ipsum dummy text is displayed the body of document '"+docTitle+"'.");
		report.runStep(isElementPresentBy("css", ccFooter), "Comcast logo footer is displayed in the document '"+docTitle+"'.");
		List<WebElement> newsItems = browser.findElements(By.cssSelector(news));
		report.runStep(!newsItems.isEmpty(), "There are '"+newsItems.size()+"' news articles displayed in the sidebar feed.");
		return this;
	}
	/**
	 * Xpath of row added to UI section of doc def in the UI of document
	 * @param rowTitle - name of the row added to UI of doc def
	 * @return
	 */
	private String expectedRow(String rowTitle){
		return "/div[@data-row-name='"+rowTitle+"']";
	}
	/**
	 * Xpath of column added to UI section of doc def in the UI of document
	 * @param  columnTitle - name of the column added to UI of doc def
	 * @return
	 */
	private String expectedColumn(String columnTitle){
		return "/div[@data-col-name='"+columnTitle+"']";
	}
	/**
	 * Xpath of region added to UI section of doc def in the UI of document
	 * @param  regionTitle - name of the region added to UI of doc def
	 * @return
	 */
	private String expectedRegion(String regionTitle){
		return "//div[@data-region-name='"+regionTitle+"']";
	}
	/**
	 * Xpath of row added to UI section of doc def in the UI of document
	 * @param rowTitle - name of the row added to UI of doc def
	 * @return
	 */
	private String imageCss(){
		String imageCss = "div.page-wrapper>img";
		if(ZetaTestDriver.project.equalsIgnoreCase("nextgen")){
			imageCss = "div#page-wrapper>div#divcontent>img";
		}
		return imageCss;
	}
}
