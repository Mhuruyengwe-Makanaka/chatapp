package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Shania Mhuruyengwe
 */
public class TestLogin {

    Login login = new Login();

    // *****assertEquals Tests****

    // Test 1: Username correctly formatted
    @Test
    public void testUsernameCorrectlyFormatted() {
 login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        boolean success = login.loginUser("kyl_1", "Ch&&sec@ke99!");
         assertEquals("Welcome Kyle, Mhuruyengwe it is great to see you again.",
                     login.returnLoginStatus(success));
    }

    // Test 2: Username incorrectly formatted
    @Test
    public void testUsernameIncorrectlyFormatted() {
          String result = login.registerUser("Kyle", "Mhuruyengwe", "kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.", result);
    }

    // Test 3: Password meets the complexity
    @Test
    public void testPasswordMeetsComplexity() {
       String result = login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
         assertTrue(result.contains("Password successfully captured."));
    }

    // Test 4: Password does NOT meet the complexity
    @Test
    public void testPasswordFailsComplexity() {
       String result = login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "password", "+27838968976");
      assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", result);
    }

    // Test 5: Cell phone number correctly formatted
    @Test
    public void testCorrectlyFormatted() {
        String result = login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(result.contains("Cell number successfully captured."));
    }

    // Test 6: Cell phone incorrectly formatted
    @Test
    public void testPhoneIncorrectlyFormatted() {
         String result = login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.", result);
    }

    // (assertTrue /False)

             // Test 7: Login successful
    @Test
    public void testLoginSuccessful() {
        login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

 // Test 8: Login failed
    @Test
    public void testLoginFailed() {
        login.registerUser("Kyle", "Mhuruyengwe", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongpassword"));
    }

 // Test 9: Username correctly formatted 
    @Test
      public void testCheckUserTrue() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    // Test 10: Username incorrectly formatted 
    @Test
    public void testCheckUserNameFalse() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    // Test 11: Password meets complexity 
    @Test
    public void testCheckPasswordTrue() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    // Test 12: Password does not meet complexity 
    @Test
   public void testCheckPasswordFalse() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // Test 13: Cell phone number correctly formatted 
    @Test
    public void testCheckCellTrue() {
         assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    // Test 14: Cell phone incorrectly formatted 
    @Test
  public void testCheckCellFalse() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }
}