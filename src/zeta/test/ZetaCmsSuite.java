package zeta.test;



import java.util.Hashtable;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import zeta.utilities.DataManager;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaApplication;
import zeta.utilities.ZetaTestDriver;


@Listeners(zeta.utilities.TestListener.class)
public class ZetaCmsSuite extends ZetaTestDriver{
	/*
	 * Validates the dashboard links, validates the project name in the top left corner,
	 * and user details.
	 */
	@Parameters({"project"})
	@Test(priority=21)
	public void dashBoard(String project) throws InterruptedException{
		RunFactory.getZeta().validateDashboard(runInfo.getAdminFirstName(), project);
	}
	/*
	 * Creates a doc def, then a document referencing the doc def, then validates
	 * what was entered into document displays in the UI.
	 */
	@Test(priority=1)
	public void createNewDocDefAndDocument() throws Exception{
		int now = new Random().nextInt(10000000);
		String docDefTitle = "AutoDocDef"+now;
		String[][] fieldDetails = {{"image", "Image Selector"},{"wysiwyg", "WYSIWYG"}, {"textarea", "Textarea"}};
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().createNewDocDefinition(docDefTitle, pageTemplate);
		RunFactory.thisDocDef().addFields(docDefTitle, fieldDetails, "docdef");
		RunFactory.thisDocDef().navToUI("docdef").addHeader("Header", "Auto Test "+now, docDefTitle).addContentComponent("wysiwyg", docDefTitle, "docdef");
		RunFactory.thisDocDef().addRegion("DocDef Region", docDefTitle).clickOnSectionAddedToUI("DocDef Region", "row").addRow("DocDef Row", docDefTitle);
		RunFactory.thisDocDef().clickOnSectionAddedToUI("DocDef Row", "column").addColumn("Image Column", docDefTitle);
		RunFactory.thisDocDef().clickOnSectionAddedToUI("DocDef Row", "column").addColumn("Text Column", docDefTitle);
		RunFactory.thisDocDef().clickOnSectionAddedToUI("Image Column", "cmp").addImage("image", docDefTitle, "docdef");
		RunFactory.thisDocDef().clickOnSectionAddedToUI("Text Column", "cmp").addContentComponent("textarea", docDefTitle, "docdef");
		RunFactory.thisDocDef().saveAndPublish(docDefTitle, "docdef");
		String docTitle = "AutoDoc"+now;
		resetZetaCms();
		RunFactory.getZeta().navigateToDocuments(true);
		RunFactory.thisDocument().createNewDocument(docTitle, docDefTitle, baseDoc);
		RunFactory.thisDocument().addToWysiwyg(fieldDetails[1][0], "").addImageToDoc(testImage, false).addToTextArea("").publishContent("doc",docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().headerPresent("Auto Test "+now, docTitle).wysiwyg(docTitle, "").imageTextRow(testImage, docTitle, "");
		if(DataManager.getData().loadTestData("createNewDocDefAndDocument").get("Status").equalsIgnoreCase("null")){
			DataManager.getData().writeTestData("createNewDocDefAndDocument", "DocDef",docDefTitle);
			DataManager.getData().writeTestData("createNewDocDefAndDocument", "Document", docTitle);
			DataManager.getData().writeTestData("createNewDocDefAndDocument", "URL",RunFactory.getBrowser().getCurrentUrl());
			DataManager.getData().writeTestData("createNewDocDefAndDocument", "Status", "Passed");
		}
	}
	/*
	 * Creates a person/author with an image, job title, job description, then
	 * validates search, column sorting, and pagination.
	 */
	@Test(priority=2)
	public void zetaPersonPage() throws Exception{
		String firstName = "Auto"+ new Random().nextInt(10000000);
		String lastName = "Tester"+ new Random().nextInt(10000000);
		RunFactory.getZeta().navigateToPeople(true);
		RunFactory.thisPeople().addPeople(firstName, lastName, testImage);
		RunFactory.thisPeople().searchForNewPerson(firstName, lastName).columnSortCheck().paginationCheck();
	}
	/*
	 * Uploads an image to the media library, validates media search.
	 */
	@Test(priority=3)
	public void uploadImageToMedia() throws Exception{
		RunFactory.getZeta().navigateToMedia(true);
		int rand = new Random().nextInt(10000000);
		String newImgFileName = "test"+rand+".png";
		String imgTitle = "AutoImage"+rand;
		RunFactory.thisMedia().uploadImage(imgTitle,newImgFileName).searchUploadedImage(imgTitle,newImgFileName, imageSrc);
	}
	/*
	 * Uploads then removes an image from media library.
	 */
	@Test(priority=4)
	public void uploadThenRemoveImageMedia() throws Exception{
		RunFactory.getZeta().navigateToMedia(true);
		RunFactory.thisMedia().uploadThenRemoveImg();
	}
	/*
	 * Creates a widget, creates a doc def then adds the widget to the UI, then creates
	 * a document referencing the doc def containing the widget,then validates
	 * what was entered into document displays in the UI.
	 */
	@Test(priority=5)
	public void createNewWidgetAndDocDefAndDocument() throws Exception{
		int now = new Random().nextInt(10000000);
		String widgetTitle = "AutoWidget"+now;
		String[][] fieldDetails = {{"wysiwyg", "Text Field"}, {"textarea", "Text Field"}};
		RunFactory.getZeta().navigateToWidgets().createWidget(widgetTitle);
		RunFactory.thisWidgets().addFields(widgetTitle, fieldDetails, "widget").navToUI("widget");
		RunFactory.thisWidgets().addRegion("Widget Region", widgetTitle).clickOnSectionAddedToUI("Widget Region", "row").addRow("Widget Row", widgetTitle);
		RunFactory.thisWidgets().clickOnSectionAddedToUI("Widget Row", "column").addColumn("WYSIWYG Column", widgetTitle);
		RunFactory.thisWidgets().clickOnSectionAddedToUI("Widget Row", "column").addColumn("Text Column", widgetTitle);
		RunFactory.thisWidgets().clickOnSectionAddedToUI("WYSIWYG Column", "cmp").addContentComponent("wysiwyg", widgetTitle, "widget");
		RunFactory.thisWidgets().clickOnSectionAddedToUI("Text Column", "cmp").addContentComponent("textarea", widgetTitle, "widget");
		RunFactory.thisWidgets().saveAndPublish(widgetTitle, "widget");
		resetZetaCms();
		//The field should be a wysiwg in doc def.
		fieldDetails[0][1] = "WYSIWYG";
		fieldDetails[1][1] = "Textarea";
		RunFactory.getZeta().navigateToDocDef(true);
		String docDefTitle = "AutoDocDefWithWidget"+ now;
		RunFactory.thisDocDef().createNewDocDefinition(docDefTitle, pageTemplate).addFields(docDefTitle, fieldDetails, "docdef").navToUI("docdef");
		RunFactory.thisDocDef().addWidget(widgetTitle, "New Widget", docDefTitle).saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		String docTitle = "AutoDocWithWiget"+ now;
		RunFactory.getZeta().navigateToDocuments(true).createNewDocument(docTitle, docDefTitle, baseDoc);
		RunFactory.thisDocument().addToWysiwyg(fieldDetails[0][0], "").addToTextArea("").publishContent("doc",docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().validateDocWithWidget(docTitle);
		if(DataManager.getData().loadTestData("createNewWidgetAndDocDefAndDocument").get("Status").equalsIgnoreCase("null")){
			DataManager.getData().writeTestData("createNewWidgetAndDocDefAndDocument", "DocDef",docDefTitle);
			DataManager.getData().writeTestData("createNewWidgetAndDocDefAndDocument", "Document", docTitle);
			DataManager.getData().writeTestData("createNewWidgetAndDocDefAndDocument", "Widget", widgetTitle);
			DataManager.getData().writeTestData("createNewWidgetAndDocDefAndDocument", "URL",RunFactory.getBrowser().getCurrentUrl());
			DataManager.getData().writeTestData("createNewWidgetAndDocDefAndDocument", "Status", "Passed");
		}
	}
	
	/*
	 * Validates expected functionality of the doc def page.
	 */
	@Test(priority=6)
	public void docDefUIValidations() throws Exception{
		String docDefTitle = "DocDefSanity"+new Random().nextInt(10000000);
		RunFactory.getZeta().navigateToDocDef(true).titleRequired().createNewDocDefinition(docDefTitle, pageTemplate);
		RunFactory.thisDocDef().verifyInDocDefGrid(docDefTitle).docDefPagesSanity().addRegion("Added Region", docDefTitle);
		RunFactory.thisDocDef().rememberLastAction(docDefTitle, "Added Region").editDeleteUISection("Added Region").deleteDocDef(docDefTitle);
	}
	
	/*
	 * Validates expected functionality of the document page.
	 */
	@Test(priority=7)
	public void documentUIValidations() throws Exception{
		String docTitle = "DocumentSanity"+new Random().nextInt(10000000);
		RunFactory.getZeta().navigateToDocuments(true).titleRequired(docTitle, baseDocDef);
		RunFactory.thisDocument().documentSanityTest(docTitle).deleteDocument(docTitle);
	}
	
	/*
	 * Creates a doc def with a repeator, creates a document referencing the doc def with repeator, 
	 * then validates what was entered into document displays in the UI.
	 */
	@Test(priority=8)
	public void createDocDefWithRepeator() throws Exception{
		int rand = new Random().nextInt(10000000);
		String docDefTitle = "AutoDocDefWithRepeator"+rand;
		String[][] fieldDetails = {{"title", "Text Field"},{"author", "Person Selector"},{"radio", "Radio Button"},{"check", "Checkbox"}};
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().createNewDocDefinition(docDefTitle, pageTemplate).addFields(docDefTitle, fieldDetails, "docdef");
		//Field details need to be edited for adding fields to data list
		fieldDetails[1][1] = "Person selector";
		fieldDetails[3][1] = "Check Box";
		RunFactory.thisDocDef().addDataList(docDefTitle, fieldDetails).navToUI("docdef");
		RunFactory.thisDocDef().addRegion("Repeater Region",docDefTitle).clickOnSectionAddedToUI("Repeater Region", "repeater");
		RunFactory.thisDocDef().addRepeater("Repeater Test", docDefTitle).addStoryHeadline("title","author", "repeater", docDefTitle);
		RunFactory.thisDocDef().addContentComponent("radio",docDefTitle,"repeater").addContentComponent("check", docDefTitle, "repeater");
		RunFactory.thisDocDef().saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		String[] repeaterComp = {"title", "author", "radio", "check"};
		RunFactory.getZeta().navigateToDocuments(true);
		String docTitle = "AutoDocWithRepeater"+rand;
		RunFactory.thisDocument().createNewDocument(docTitle, docDefTitle, baseDoc).addRepeater(2, repeaterComp).publishContent("doc", docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().repeaterStoryTitle(docTitle, 2).repeaterAuthor(docTitle, 2);
		RunFactory.thisUI().repeaterCheckboxAndRadio(docTitle, 2, "Checkbox");
		if(DataManager.getData().loadTestData("createDocDefWithRepeator").get("Status").equalsIgnoreCase("null")){
			DataManager.getData().writeTestData("createDocDefWithRepeator", "DocDef",docDefTitle);
			DataManager.getData().writeTestData("createDocDefWithRepeator", "Document", docTitle);
			DataManager.getData().writeTestData("createDocDefWithRepeator", "URL",RunFactory.getBrowser().getCurrentUrl());
			DataManager.getData().writeTestData("createDocDefWithRepeator", "Status", "Passed");
		}
	}
	/*
	 * Verifies a user can create and delete an environment.
	 */
	@Test(priority=9)
	public void createDeleteEnvironment() throws InterruptedException{
		RunFactory.getZeta().navigateToEnvironment(true).createAndDeleteEnvironment();
	}
	
	/*
	 * Verifies the active edition of a doc def displays in the UI, creates a new edition of doc def created in test
	 * createNewDocDefAndDocument, then opens the document created in same test createNewDocDefAndDocument, verify that 
	 * the UI refects the new edition, ends the doc def editions validates the document UI reverts back to original state.
	 */
	@Test(priority=10)
	public void docDefEdition() throws Exception{
		ZetaApplication getDocument = new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails());
		int rand = new Random().nextInt(10000000);
		String docDefEditionTitle = "AutoDocDefEdition"+rand;
		String docDefTitle = null;
		String docTitle = null;
		String docUrl = null;
		Hashtable<String, String> data = DataManager.getData().loadTestData("createNewDocDefAndDocument");	
		if(data.get("Status").equalsIgnoreCase("passed")){
			docDefTitle = data.get("DocDef");
			docTitle = data.get("Document");
			docUrl = data.get("URL");
		}else{
			RunFactory.getReport().runStep(false, "createNewDocDefAndDocument test failed.");
		}
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().editExistingDocDef(docDefTitle).createNewEditionDocDef(docDefTitle, docDefEditionTitle, pageTemplate);
		RunFactory.thisDocDef().navToUI("docdef").addImage("image", docDefEditionTitle, "docDef").saveAndPublish(docDefEditionTitle, "docdef");
		resetZetaCms();
		getDocument.openZetaDocumnet(docUrl);
		RunFactory.thisUI().docDefEditionTest(testImage, docTitle, docDefTitle, docDefEditionTitle);
		resetZetaCms();
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().endDocDefEdition(docDefTitle, docDefEditionTitle).publishContent("docdef", docDefEditionTitle );
		getDocument.openZetaDocumnet(docUrl);
		RunFactory.thisUI().editionEnded(docTitle, docDefTitle, docDefEditionTitle, testImage);
	}
	
	/*
	 * Verifies the active edition of a document displays in the UI, creates a new edition of document created in test
	 * createNewDocDefAndDocument, validates the new edition is displayed in UI, ends editions, then verifies the document reverts back
	 * original state.
	 */
	@Test(priority=16)
	public void documentEdition() throws Exception{
		int rand = new Random().nextInt(10000000);
		String docDefTitle = null;
		String docTitle = null;
		Hashtable<String, String> data = DataManager.getData().loadTestData("createNewDocDefAndDocument");
		//Checks if upstream test created document
		if(data.get("Status").equalsIgnoreCase("passed")){
			docDefTitle = data.get("DocDef");
			docTitle =  data.get("Document");
		}else{
			RunFactory.getReport().runStep(false, "createNewDocDefAndDocument test failed.");
		}
		String docEditionTitle = "AutoDocEdition"+ rand;
		String newText = "Edition testing "+rand+"!!!!!!";
		RunFactory.getZeta().navigateToDocuments(true);
		RunFactory.thisDocument().editExistingDoc(docTitle, true);
		RunFactory.thisDocument().createNewAutoDocEdition(docEditionTitle, docTitle, docDefTitle);
		RunFactory.thisDocument().addToWysiwyg("wysiwyg", newText).addImageToDoc(testImage, false).addToTextArea(newText).publishContent("doc",docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().documentEditionTest(docTitle, docEditionTitle, newText, testImage);
		closeOpenDoc();
		RunFactory.thisDocument().endDocumentEdition(docTitle, docEditionTitle).publishContent("doc", docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().editionEnded(docTitle, docDefTitle, docEditionTitle, testImage);
	}
	/*
	 * Validates the import of Doc Def files.
	 */
	@Test(priority=12)
	public void importADocDef() throws Exception{
		String docDefTitle = "AutoImportDocDef"+new Random().nextInt(10000000);
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().importDocDef(docDefTitle, pageTemplate);
	}
	/*
	 * Creates a dummy java script asset, adds to the doc def created in createNewDocDefAndDocument,
	 * opens the document created in test createNewDocDefAndDocument, and verifies the js assset is displayed
	 * in the html.
	 */
	@Test(priority=13)
	public void createJsAsset() throws Exception{
		int rand = new Random().nextInt(10000000);
		String assetTitle = "AutoJSAsset"+rand;
		String docDefTitle = null;
		String docTitle = null;
		String docUrl = null;
		Hashtable<String, String> data = DataManager.getData().loadTestData("createNewDocDefAndDocument");
		//Checks if upstream test created document
		if(data.get("Status").equalsIgnoreCase("passed")){
			docDefTitle = data.get("DocDef");
			docTitle =  data.get("Document");
			docUrl = data.get("URL");
		}else{
			RunFactory.getReport().runStep(false, "createNewDocDefAndDocument test failed.");
		}
		RunFactory.getZeta().navigateToAssets().createAsset(assetTitle, "js");
		resetZetaCms();
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().editExistingDocDef(docDefTitle).addAssetToHeader(assetTitle, docDefTitle).saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()).openZetaDocumnet(docUrl);
		RunFactory.thisUI().assetTest(docTitle, assetTitle);
		if(DataManager.getData().loadTestData("createJsAsset").get("Status").equalsIgnoreCase("null")){
			DataManager.getData().writeTestData("createJsAsset", "DocDef",docDefTitle);
			DataManager.getData().writeTestData("createJsAsset", "Document", docTitle);
			DataManager.getData().writeTestData("createJsAsset", "Asset", assetTitle);
			DataManager.getData().writeTestData("createJsAsset", "URL", docUrl);
			DataManager.getData().writeTestData("createJsAsset", "Status", "Passed");
		}	
	}
	/*
	 * Creates a dummy java script asset, adds to the doc def created in createNewDocDefAndDocument,
	 * opens the document created in test createNewDocDefAndDocument, and verifies the css assset is displayed
	 * in the html.
	 */
	@Test(priority=18)
	public void createCssAsset() throws Exception{
		int rand = new Random().nextInt(10000000);
		String assetTitle = "AutoCSSAsset"+rand;
		String docDefTitle = null;
		String docTitle = null;
		String docUrl = null;
		Hashtable<String, String> data = DataManager.getData().loadTestData("createNewDocDefAndDocument");
		//Checks if upstream test created document
		if(data.get("Status").equalsIgnoreCase("passed")){
			docDefTitle = data.get("DocDef");
			docTitle =  data.get("Document");
			docUrl = data.get("URL");
		}else{
			RunFactory.getReport().runStep(false, "createNewDocDefAndDocument test failed.");
		}
		RunFactory.getZeta().navigateToAssets().createAsset(assetTitle, "css");
		resetZetaCms();
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().editExistingDocDef(docDefTitle).addAssetToHeader(assetTitle, docDefTitle).saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		new ZetaApplication(RunFactory.getBrowser(), RunFactory.getReport(), RunFactory.getRunDetails()).openZetaDocumnet(docUrl);
		RunFactory.thisUI().assetTest(docTitle, assetTitle);	
		if(DataManager.getData().loadTestData("createCssAsset").get("Status").equalsIgnoreCase("null")){
			DataManager.getData().writeTestData("createCssAsset", "DocDef",docDefTitle);
			DataManager.getData().writeTestData("createCssAsset", "Document", docTitle);
			DataManager.getData().writeTestData("createCssAsset", "Asset", assetTitle);
			DataManager.getData().writeTestData("createCssAsset", "URL", docUrl);
			DataManager.getData().writeTestData("createCssAsset", "Status", "Passed");
		}
	}
	/*
	 * Uploads a youtube video to meadia library, validats search, video thumbnail and video player
	 */
	@Test(priority=15)
	public void uploadYoutubeVideoToMedia() throws Exception{
		String videoTitle = "AutoTestYoutube"+new Random().nextInt(10000000);
		String videoUrl = "https://www.youtube.com/watch?v=F3eVgmwgq9U";
		RunFactory.getZeta().navigateToMedia(true);
		String thumbNail = runUrl + RunFactory.thisMedia().imageTitle("url");
		System.out.println("In thumb nail -- "+thumbNail);
		RunFactory.thisMedia().uploadVideo(videoTitle, "youtube", videoUrl, thumbNail).searchAndPlayVideo(videoTitle, "youtube", videoUrl, thumbNail);
	}
	/*
	 * Uploads a mp4 video to meadia library, validats search, video thumbnail and video player
	 */
	@Test(priority=11)
	public void uploadMp4VideoToMedia() throws Exception{
		String videoTitle = "AutoTestMp4 "+new Random().nextInt(10000000);
		String videoUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
		RunFactory.getZeta().navigateToMedia(true);
		String thumbNail = runUrl + RunFactory.thisMedia().imageTitle("url");
		RunFactory.thisMedia().uploadVideo(videoTitle, "mp4", videoUrl, thumbNail).searchAndPlayVideo(videoTitle, "mp4", videoUrl, thumbNail);
	}
	
	/*
	 * Creates a group with access to documents only, assigns testingacct_02 to group, logs in as testingacct_02 and verifies 
	 * access restriction.
	 */
	@Test(priority=17)
	public void createDocumentOnlyGroup() throws Exception {
		int rand = new Random().nextInt(10000000);
		String groupName = "AutoTestGroup"+rand;
		String[][] permissions = {{"Documents", "1"},{"DocumentsPublishProd", "19"}, {"DocumentsPublishNonProd", "20"}};
		RunFactory.getZeta().navigateToRoleManagement();
		RunFactory.thisRoles().removeFromGroup().addNewGroup(groupName).addUserToGroup(groupName).setPermissions(groupName, permissions);
		loginAsRestrictedUser();
		RunFactory.getZeta().accessDocuments(groupName);
		String docTitle = "AutoTestCreteAndDelete"+rand;
		RunFactory.thisDocument().createNewDocument(docTitle, baseDocDef, baseDoc).deleteDocument(docTitle);
		RunFactory.thisDocument().editExistingDoc(baseDoc, false);
	}
	
	/*
	 * Creates a group with access to doc defs only, assigns testingacct_02 to group, logs in as testingacct_02 and verifies 
	 * access restriction.
	 */
	@Test(priority=14)
	public void createDocDefOnlyGroup() throws Exception {
		int rand = +new Random().nextInt(10000000);
		String groupName = "AutoTestGroup"+rand;
		String[][] permissions = {{"DocDefs", "2"},{"DocDefsPublishProd", "26"}, {"DocDefsPublishNonProd", "25"}};
		RunFactory.getZeta().navigateToRoleManagement();
		RunFactory.thisRoles().removeFromGroup().addNewGroup(groupName).addUserToGroup(groupName).setPermissions(groupName, permissions);
		loginAsRestrictedUser();
		RunFactory.getZeta().accessDocDecDefs(groupName);
		String docDef = "AutoTestCreateAndDelete"+rand;
		RunFactory.thisDocDef().createNewDocDefinition(docDef, pageTemplate).deleteDocDef(docDef);
	}
	
	/*
	 * Creates a group with access to media libarary only, assigns testingacct_02 to group, logs in as testingacct_02 and verifies 
	 * access restriction.
	 */
	@Test(priority=19)
	public void createMediaGroup() throws Exception {
		String groupName = "AutoTestGroup"+new Random().nextInt(10000000);
		String[][] permissions = {{"Media", "4"},{"Video", "15"}, {"Image", "16"}};
		RunFactory.getZeta().navigateToRoleManagement();
		RunFactory.thisRoles().removeFromGroup().addNewGroup(groupName).addUserToGroup(groupName).setPermissions(groupName, permissions);
		loginAsRestrictedUser();
		RunFactory.getZeta().accessMeda(groupName);
	}
	/*
	 * Creates a doc def with just an image component in UI, creates document referening the doc def, adds alternate text to image,
	 * then verifies the document image in UI and alt text tag is contained in html.
	 */
	@Test(priority=20)
	public void alternateTextImageComponent() throws Exception{
		int rand = new Random().nextInt(10000000);
		String docDefTitle = "AutoDocDefAltTextImg"+rand;
		String docTitle = "AutoDocAltTextImg"+rand;
		String imageTitle = "AutoAltTextImg"+rand;
		String imageFile = "test"+rand+".png";
		String altText = "AutoAltText"+rand;
		String[][] fieldDetails = {{"image", "Image Selector"}};
		RunFactory.getZeta().navigateToDocDef(true);
		RunFactory.thisDocDef().createNewDocDefinition(docDefTitle, pageTemplate);
		RunFactory.thisDocDef().addFields(docDefTitle, fieldDetails, "docdef");
		RunFactory.thisDocDef().navToUI("docdef").addImage("image", docDefTitle, "docdef");
		RunFactory.thisDocDef().saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		RunFactory.getZeta().navigateToDocuments(true);
		RunFactory.thisDocument().createNewDocument(docTitle, docDefTitle, baseDoc);
		RunFactory.thisDocument().uploadImageFromFileSysToDoc(imageFile, imageTitle).addImageToDoc(imageTitle, false).addAltTextToImage(imageTitle, altText);
		RunFactory.thisDocument().publishContent("doc", docTitle);
		RunFactory.thisDocument().openDocument(docTitle, docDefTitle);
		RunFactory.thisUI().altTextImageTest(docTitle, imageTitle, altText);
	}
}
