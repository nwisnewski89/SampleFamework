package zeta.utilities;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Report {
	
	protected ExtentTest runStep;
	protected WebDriver browser;
	
	public Report(ExtentTest runStep,WebDriver browser){
		this.runStep = runStep;
		this.browser = browser;
	}
	/**@author nwisnewski
	 * Records a pass/fail, if fail screenshot is attached to step.
	 * @param test 
	 * @param testStepName
	 */
	public void runStep(Boolean test, String testStepName){
		if(test){
			runStep.log(LogStatus.PASS, "Test step passed: " + testStepName);
		}
		else{
			runStep.log(LogStatus.FAIL, "Test step failed: " + testStepName + runStep.addScreenCapture(this.capture(testStepName.substring(0, testStepName.length()-1))));
		}
		Assert.assertTrue(test, testStepName);
	}
	
	public void infoStep(String information){
		runStep.log(LogStatus.INFO, information);
	}
	
	public void afterTestFailed(String testName){
		runStep.log(LogStatus.FAIL, testName+" failure: "+runStep.addScreenCapture(this.capture(testName+System.currentTimeMillis())));
	}
	
	/**@author nwisnewski
	 * Captures screen shot returns the image file location.
	 * @param stepName
	 * @return
	 */
	private String capture(String stepName){
			TakesScreenshot capture = (TakesScreenshot)browser;
			File image = capture.getScreenshotAs(OutputType.FILE);
			String imageName = stepName+".png";
			
			File writeTo = new File(ZetaTestDriver.suiteReportPath, imageName);
			try {
				FileUtils.copyFile(image, writeTo);
			} catch (Exception e) {
				System.out.println("Screen Shot failed"+e.getMessage());
			}	
			return writeTo.getAbsolutePath();
		}

}
