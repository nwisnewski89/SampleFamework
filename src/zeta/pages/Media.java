package zeta.pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import zeta.utilities.DataManager;
import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class Media extends ZetaCMS{

	public Media(WebDriver browser, Report report, RunDetails threadRun) {
		super(browser,report, threadRun);
	}
	
	@FindBy(name = "desc")
	private WebElement imageDesc;
	
	private static final String OpenUploadImageBtn = "//a[@data-modal-link='/media/upload-modal']";
	
	private static final String OpenUploadVideoBtn = "//a[@data-modal-link='/media/video/create-modal']";
	
	private static final String MediaRefreshBtn = "zeta_media_image_refresh";
	
	private static final String UploadImageBtnBig = ".btn.btn-default.fileinput-upload.fileinput-upload-button";
	
	private static final String RemoveImageBtnBig = ".btn.btn-default.fileinput-remove.fileinput-remove-button";
	
	private static final String EmptyImage = ".file-drop-zone-title";
	
	private static final String ImageUploadSuccess = ".progress-bar.progress-bar-success";
	
	private static final String SaveMediaBtn = "//button[@data-dismiss='modal']";
	
	private static final String ImageTitleAndLink = "//div[@class='col-sm-6 col-md-4']/div/div[@class='img-container']/a";
	
	private static final String VideoTab = "//ul[@role='tablist']/li[2]/a[@href='#videos_index']";
	
	private static final String VideoThumbnail = "img.video_thumbnail";
	/**
	 * Uploads an image to the media library
	 * @param imageTitle - title of the image
	 * @param imgFileName - name of the new image file
	 * @return
	 * @throws InterruptedException
	 * @throws AWTException
	 * @throws IOException
	 */
	public Media uploadImage(String imageTitle, String imgFileName) throws InterruptedException, AWTException, IOException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OpenUploadImageBtn)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", OpenUploadImageBtn);
		uploadImageFile(imgFileName, imageTitle, "media library");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(MediaRefreshBtn)));
		return this;
	}
	/**
	 * Uploads then a removes an image
	 * @return
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	public Media uploadThenRemoveImg() throws AWTException, InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OpenUploadImageBtn)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", OpenUploadImageBtn);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(TitleField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		WebElement fileInput = tempElementBy("id", "js-upload-files");
		String imageLocation = System.getProperty("user.dir")+File.separator+"testData"+File.separator+"hero_dog.png";
		if(RunFactory.getRunDetails().nodeIp().equals("local")){
			fileInput.sendKeys(imageLocation);
		}else{
			RemoteWebElement remoteFileInput = (RemoteWebElement) fileInput;
			remoteFileInput.setFileDetector(new LocalFileDetector());
			remoteFileInput.sendKeys(imageLocation);
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(TitleField)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(1000);
		report.runStep(isElementPresentBy("css", RemoveImageBtnBig), "An image was selected from the file directory.");
		Thread.sleep(1000);
		clickElementBy("css",RemoveImageBtnBig);
		Thread.sleep(1000);
		report.runStep(tempElementBy("css", EmptyImage).getText().equals("Drag & drop files here …"), "User can remove image uploaded from file directory.");
		return this;
	}
	/**
	 * Searches the media library for an image
	 * @param imagetitle - title of image in zeta
	 * @param imgFileName - name of the file uploaded to zeta
	 * @param imageSrc - project specific source tag, set in project variables
	 * @return
	 * @throws InterruptedException
	 */
	public Media searchUploadedImage(String imagetitle, String imgFileName, String imageSrc) throws InterruptedException{
		clickElementBy("id", MediaRefreshBtn);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(ImageTitleAndLink)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		tempElementBy("id", "searchImages").sendKeys(imagetitle);
		tempElementBy("id", "searchImages").sendKeys(Keys.ENTER);
		zetaCMSWait("xpath", ImageTitleAndLink);
		System.out.println(browser.findElement(By.xpath(ImageTitleAndLink)).getAttribute("data-title").equalsIgnoreCase(imagetitle));
		report.runStep(browser.findElement(By.xpath(ImageTitleAndLink)).getAttribute("data-title").equalsIgnoreCase(imagetitle)
				, "Image with title '"+imagetitle+"' uploaded in previous step was located in image search");
		Date today = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM");
		//link displayed under the image in the media library
		String imageLink = "/"+fmt.format(today)+"/"+imageSrc+imgFileName;
		System.out.println(imageLink);
		System.out.println(browser.findElements(By.linkText(imageLink)).size());
		Thread.sleep(1000);
		browser.findElement(By.cssSelector(".caption>h5>a")).click();
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(imageSizeUrls(imageLink))));
		} catch (Exception e) {
			waitFail(e);
			
		}
		//verifies the image is available in 16 sizes once uploaded
		int imageSizes = browser.findElements(By.xpath(imageSizeUrls(imageLink))).size();
		report.runStep(imageSizes==16, "Clicking the image url of '"+imagetitle+"' displays urls for '"+imageSizes+"' different image sizes available.");
		return this;
	}
	/**
	 * Copies the hero_dog.png image and renames it 
	 * @param newImgName - random title generated for copied image
	 * @return - filepath to new image
	 * @throws IOException
	 */
	public String newImageFile(String newImgName) throws IOException{
		 File original = new File(System.getProperty("user.dir")+File.separator+"testData"+File.separator+"hero_dog.png");
		 String fileName = System.getProperty("user.dir")+File.separator+"testData"+File.separator+newImgName;
		 File newFile = new File(fileName);
		 FileUtils.copyFile(original, newFile);
		 System.out.println(newFile.getAbsolutePath());
		 return newFile.getAbsolutePath();
	}
	/**
	 * Uploads a video to the media library
	 * @param videoTitle - title of video
	 * @param videoType - Fixed: youbtube, mp4
	 * @param videoUrl - url of yourtube or mp4 video
	 * @param thumbNail - url of thumbnail image
	 * @return
	 * @throws Exception
	 */
	public Media uploadVideo(String videoTitle, String videoType, String videoUrl, String thumbNail) throws Exception{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OpenUploadImageBtn)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(isElementPresentBy("xpath", VideoTab), "Video tab is displayed in the media section of the CMS.");
		Thread.sleep(1000);
		clickElementBy("xpath", VideoTab);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(VideoThumbnail)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
		clickElementBy("xpath", OpenUploadVideoBtn);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(TitleField)));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("name", TitleField).sendKeys(videoTitle);
		Thread.sleep(500);
		clickElementBy("xpath",editVideoBtns("Public", 2));
		Thread.sleep(1000);
		//Youtube url is default choice
		if(videoType.equalsIgnoreCase("youtube")){
			setVideoUrl(videoType).sendKeys(videoUrl);
		}else{
			//If not youtube, then click the URL button to display other options
			clickElementBy("xpath", editVideoBtns("Video Type", 2));
			Thread.sleep(2000);
			setVideoUrl(videoType).sendKeys(videoUrl);
		}
		Thread.sleep(500);
		tempElementBy("name", "video_info.poster_image").sendKeys(thumbNail);
		clickElementBy("css", "button.btn.btn-default.zeta-btn-video-create");
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OpenUploadVideoBtn)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(true, "The '"+videoType+"' video link with title '"+videoTitle+"' was uploaded to the video section of the media library.");
		clickElementBy("css", RefreshZeta);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OpenUploadVideoBtn)));
		} catch (Exception e) {
			waitFail(e);
		}
		return this;
	}
	/**
	 * Searches the medial library for the video uploaded in previous step, then verfies the video can play
	 * @param videoTitle - title of video
	 * @param videoType - Fixed: youbtube, mp4
	 * @param videoUrl - url of yourtube or mp4 video
	 * @param thumbNail - url of thumbnail image
	 * @return
	 * @throws Exception
	 */
	public Media searchAndPlayVideo(String videoTitle, String videoType, String videoUrl, String thumbNail) throws Exception{
		String vidSearch = "zeta_video_search";
		String videoPlayer = "";
	
		switch(videoType.toLowerCase()){
			case "youtube":
				videoPlayer = "cmp_video_video";
				break;
			case "mp4":
				videoPlayer = "cmp_video_video_html5_api";
				break;
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(vidSearch)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(1000);
		tempElementBy("id", vidSearch).sendKeys(videoTitle);
		tempElementBy("id", vidSearch).sendKeys(Keys.ENTER);
		zetaCMSWait("css", VideoThumbnail);
		System.out.println("Can it see?!? -- " +browser.findElements(By.cssSelector(VideoThumbnail)).size());
		System.out.println(thumbNail);
		//Noticed that after search the tempElementBy and clickElementBy methods threw stale element references
		report.runStep(browser.findElement(By.cssSelector(VideoThumbnail)).getAttribute("src").equalsIgnoreCase(thumbNail)&&
				browser.findElement(By.xpath("//div[@id='zeta_media_video_list']/div/div/div[@class='caption']/h3")).getText().equalsIgnoreCase(videoTitle)
				, "Video '"+videoTitle+"' added in previous step was located in the media page video search with the thumbnail entered in previous step displayed.");
		browser.findElement(By.id("play")).click();
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(videoPlayer)));
		} catch (Exception e) {
			waitFail(e);
		}
		boolean videoCheck = false;
		switch(videoType.toLowerCase()){
			case "youtube":
				videoCheck = tempElementBy("id", videoPlayer).getAttribute("data-setup").toLowerCase().contains(videoUrl.toLowerCase());
				break;
			case "mp4":
				videoCheck = tempElementBy("id", videoPlayer).getAttribute("src").equalsIgnoreCase(videoUrl);
				break;
		}
		report.runStep(videoCheck, "Clicking on thumbnail icon on the video '"+videoTitle+"' opened video player, stream the '"+videoType+" video url added in previous step.");
		return this;
	}
	/**
	 * Deletes the temp image file created for upload test
	 * @param newImgName
	 * @throws IOException
	 */
	public void deleteTempImg(String newImgName) throws IOException{
		Files.delete(FileSystems.getDefault().getPath(System.getProperty("user.dir")+File.separator+"testData"+File.separator+newImgName));
	}
	/**
	 * Gets the url of title of the first image in media library
	 * @param cmsOrUrl
	 * @return
	 */
	public String imageTitle(String cmsOrUrl){
		String imageTitle =  "//div[@id = 'zeta_media_img_list']/div/div[@class='thumbnail']/div/h3";
		if(cmsOrUrl.equalsIgnoreCase("url")){
			imageTitle =  "//div[@id = 'zeta_media_img_list']/div/div[@class='thumbnail']/div/h5";
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(imageTitle)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		String title = tempElementBy("xpath", imageTitle).getText();
		if(cmsOrUrl.equalsIgnoreCase("cms")){
			report.infoStep("Retrieved image '"+title+"' from media library to be added to document.");
		}else{
			report.infoStep("Retrieved image source url'"+title+"' from media library to be added to video thumbnail image.");
		}
		return title;
	}
	/**
	 * xpath for the public - no,yes or video type - yourtub, url button in the add video pop up module
	 * @param btnGroup - Fixed: Public, Video Type
	 * @param button - Fixed: 1-> 1st choice, 2-> 2nd choice
	 * @return
	 */
	private String editVideoBtns(String btnGroup, int button){
		return "//label[text()='"+btnGroup+"']/following-sibling::div[@class='col-sm-6']/div/label["+button+"]";
	}
	
	private String imageSizeUrls(String link){
		return "//h3[contains(text(), 'Sizes')]/following-sibling::a[contains(@href ,'"+link+"')]";
	}
	/**
	 * Returns the input field for the video url
	 * @param vidType - Fixed: youtube, mp4
	 * @return
	 */
	private WebElement setVideoUrl(String vidType){
		return tempElementBy("name", "video_info.src_"+vidType.toLowerCase());
	}
	/**
	 * Interacts with the modal window when adding an image, also called in the Document classs
	 * @param imgFileName - name of file uploaded to zeta
	 * @param imageTitle - title image displayed in zeta
	 * @param page - Fixed: media library, document
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void uploadImageFile(String imgFileName, String imageTitle, String page) throws IOException, InterruptedException{
		String imageLocation = newImageFile(imgFileName);
		String enterTitle = TitleField;
		if(page.equalsIgnoreCase("document")){
			enterTitle = "q";
		}
		try {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(enterTitle)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(250);
		WebElement fileInput = tempElementBy("id", "js-upload-files");
		if(threadRun.nodeIp().equals("local")){
			fileInput.sendKeys(imageLocation);
		}else{
			RemoteWebElement remoteFileInput = (RemoteWebElement) fileInput;
			remoteFileInput.setFileDetector(new LocalFileDetector());
			remoteFileInput.sendKeys(imageLocation);
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(enterTitle)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		Thread.sleep(1000);
		report.runStep(isElementPresentBy("css", RemoveImageBtnBig), "An image was selected from the file directory.");
		tempElementBy("name", TitleField).sendKeys(imageTitle);
		imageDesc.sendKeys("Auto images test test test.");
		clickElementBy("css", UploadImageBtnBig);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ImageUploadSuccess)));
		} catch (Exception e) {
			waitFail(e);
			
		}
		System.out.println(browser.findElements(By.cssSelector(ImageUploadSuccess)));
		report.runStep(!browser.findElements(By.cssSelector(ImageUploadSuccess)).isEmpty(), "Image with title '"+imageTitle+"' was successfully uploaded to zeta "+page+".");
		clickElementBy("xpath", SaveMediaBtn);
	}
}
