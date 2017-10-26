package zeta.test;

import java.util.Random;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
@Listeners(zeta.utilities.TestListener.class)
public class CorporateSuite extends ZetaTestDriver{
	
	@Test
	public void createBioPage() throws InterruptedException{
		String imageTitle = RunFactory.getZeta().navigateToMedia(true).imageTitle("cms");
		String docTitle = "AutoBioTest"+new Random().nextInt(1000000);
		RunFactory.getZeta().navigateToDocuments(false);
		RunFactory.thisDocument().createNewDocument(docTitle, "Bio Page", baseDoc);
		RunFactory.thisDocument().addToTextField("Bio-Name", "Auto Tester").addToTextField("Job Title", "QA Engineer").addToWysiwyg("content", "");
		RunFactory.thisDocument().switchTab("media").addImageToDoc(imageTitle, true).publishContent("doc", docTitle);
		RunFactory.thisDocument().openDocument(docTitle, "Bio Page");
		RunFactory.thisUI().corprateBioPage(docTitle, imageTitle, "Auto Tester", "QA Engineer", "");
	}
}
