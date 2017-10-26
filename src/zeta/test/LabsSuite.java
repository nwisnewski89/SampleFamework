package zeta.test;

import java.util.Random;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver;
@Listeners(zeta.utilities.TestListener.class)
public class LabsSuite extends ZetaTestDriver{
	
	@Test
	public void createArticlePage() throws Exception{
		int rand = new Random().nextInt(100000000);
		String docTitle = "AutoTestArticle"+rand;
		String imageTitle = RunFactory.getZeta().navigateToMedia(true).imageTitle("cms");
		String imageUrl = RunFactory.thisMedia().imageTitle("url");
		String authorName = RunFactory.getZeta().navigateToPeople(false).getAuthorName();
		RunFactory.getZeta().navigateToDocuments(false);
		RunFactory.thisDocument().createNewDocument(docTitle, "Labs Articles", baseDoc);
		RunFactory.thisDocument().addToTextField("Title", docTitle).addToTextField("Subtitle", "Subtitle of "+docTitle);
		RunFactory.thisDocument().todayInDateField("Date").addToWysiwyg("Content", "").addToTextField("Link", "http://www.google.com");
		RunFactory.thisDocument().addToWysiwyg("Content1", "").addToTextField("Title1", "Story 1 "+rand).addToTextField("Title2", "Story 2 "+rand);
		RunFactory.thisDocument().todayInDateField("Date1").todayInDateField("Date2").switchTab("Media").addImageToDoc(imageTitle, true);
		RunFactory.thisDocument().switchTab("Author").addAuthorToDoc(authorName);
		
	}

}
