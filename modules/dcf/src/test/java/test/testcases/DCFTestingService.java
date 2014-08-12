package test.testcases;

import org.xito.testing.*;

public class DCFTestingService extends AbstractTestingService {

	public static void main(String args[]) {
		//process our DCF Test Cases
		processTestCases(DCFTestingService.class.getClassLoader());
	}

}