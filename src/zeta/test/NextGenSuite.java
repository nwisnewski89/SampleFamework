package zeta.test;

import java.util.Random;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import zeta.pages.Document;
import zeta.pages.DocumentDefinitions;
import zeta.pages.Media;
import zeta.pages.UIValidation;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;

@Listeners(zeta.utilities.TestListener.class)
public class NextGenSuite extends ZetaTestDriver {
	
	@Test
	public void uploadVideoAddToDocument() throws Exception{
		int rand = new Random().nextInt(10000000);
		String videoTitle = "AutoTestMp4 "+rand;
		String docDefTitle = "AutoTestMp4DocDef"+rand;
		String docTitle = "AutoTestMp4Doc"+rand;
		String videoUrl = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
		Media media = RunFactory.getZeta().navigateToMedia(true);
		String thumbNail = runUrl + media.imageTitle("url");
		media.uploadVideo(videoTitle, "mp4", videoUrl, thumbNail);
		resetZetaCms();
		DocumentDefinitions docDef = RunFactory.getZeta().navigateToDocDef(true);
		String[][] fieldDetails = {{"video", "Video Selector"}};
		docDef.createNewDocDefinition(docDefTitle, pageTemplate).addFields(docDefTitle, fieldDetails, "docdef");
		docDef.navToUI("docdef").addVideoComponent("Video Component", docDefTitle, fieldDetails[0][0]);
		docDef.saveAndPublish(docDefTitle, "docdef");
		resetZetaCms();
		Document doc = RunFactory.getZeta().navigateToDocuments(true);
		doc.createNewDocument(docTitle, docDefTitle, baseDoc).addVideoToDoc(videoTitle, thumbNail);
		doc.publishContent("doc", docTitle);
		UIValidation ui = doc.openDocument(docTitle, docDefTitle);
		ui.videoComponentTest(docTitle, videoTitle);
	}

}
