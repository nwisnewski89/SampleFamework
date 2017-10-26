package zeta.test;

import java.util.Random;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
@Listeners(zeta.utilities.TestListener.class)
public class DNISuite extends ZetaTestDriver {
	
	@Test
	public void createDIPage() throws Exception{
		String imageTitle = RunFactory.getZeta().navigateToMedia(true).imageTitle("cms");
		String imgSrc = RunFactory.thisMedia().imageTitle("url");
		int rand = new Random().nextInt(1000000);
		String docTitle = "AutoDIPageTest"+rand;
		RunFactory.getZeta().navigateToDocuments(false);
		RunFactory.thisDocument().createNewDocument(docTitle, "DI Page", baseDoc);
		RunFactory.thisDocument().addToTextField("Title", docTitle).addToWysiwyg("Content", "");
		RunFactory.thisDocument().switchTab("media").addImageToDoc(imageTitle, true).publishContent("doc", docTitle);
		RunFactory.thisDocument().openDocument(docTitle, "DI Page");
		RunFactory.thisUI().diPageValidation(docTitle, imageTitle, imgSrc);
	}

}
