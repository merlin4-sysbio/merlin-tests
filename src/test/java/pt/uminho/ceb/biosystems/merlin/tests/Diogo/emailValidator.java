package pt.uminho.ceb.biosystems.merlin.tests.Diogo;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.dataAccess.InitDataAccess;

public class emailValidator {

	
	//@Test
	public static boolean isEmailValid (String email) {
		boolean isValid = true;
		try {
			InternetAddress emailAddress = new InternetAddress(email);
			emailAddress.validate();
			
		} catch (AddressException e) {
			e.printStackTrace();
			isValid = false;
		}
		return isValid;
	}
	
	@Test
	public void tester () {
		
		try {
			System.out.println(isEmailValid("pg32938.com.çdoutorsampaiosampaio"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
