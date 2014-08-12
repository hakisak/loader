package test.testcases;

import org.xito.testing.*;

public class BLXTestingService extends AbstractTestingService {

	public static void main(String args[]) {
		//process our BLX Test Cases
		processTestCases(BLXTestingService.class.getClassLoader());
	}

}