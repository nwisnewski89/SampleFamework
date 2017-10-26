package zeta.utilities;

import java.io.IOException;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener, IInvokedMethodListener {

	@Override
	public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub

	}
	/**
	 * Only need to use the on test failure/skip methods
	 * To ensure the browser is closed and all results
	 */
	@Override
	public void onTestFailure(ITestResult arg0) {
		System.out.println("In failure over ride method!");
		if(!(RunFactory.getBrowser()==null)){
			RunFactory.getReport().afterTestFailed(RunFactory.getRunDetails().testName());
		}
		RunFactory.flushTest();
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		System.out.println("In skip over ride method!");
		RunFactory.flushTest();
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

}
