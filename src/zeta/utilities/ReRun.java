package zeta.utilities;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class ReRun implements IRetryAnalyzer {
	private static int RUN_LIMIT = 2;
	private AtomicInteger count = new AtomicInteger(RUN_LIMIT);
	@Override
	public boolean retry(ITestResult result) {
		if(result.getStatus()==ITestResult.FAILURE){
			if(canRetry()){
				count.decrementAndGet();
				result.setStatus(ITestResult.FAILURE);
				return true;
			}else{
				result.setStatus(ITestResult.FAILURE);
			}
		}else{
			result.setStatus(ITestResult.SUCCESS);
		}
		return false;
	}
	
	private boolean canRetry(){
		return (count.intValue()>0);
	}

}
