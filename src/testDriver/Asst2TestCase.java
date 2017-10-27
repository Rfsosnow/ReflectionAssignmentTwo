package testDriver;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Asst2TestCase {	
	
	
	@BeforeEach
	static void setUp() throws Exception {
		ClassA clssA = new ClassA();
		ClassB clssB = new ClassB();
		ClassD clssD = new ClassD();
		
		int[] intArray = {0,1,2,3};
	}

	@AfterEach
	static void tearDown() throws Exception {
		ClassA clssA = null;
		ClassB clssB = null;
		ClassD clssD = null;
		
		int[] intArray = null;
	}

	@Test
	void classArrayDetailsTest() {
		
		
	}

}
