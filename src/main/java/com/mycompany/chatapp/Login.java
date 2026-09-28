/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

/**
 *
 * @author Shania Mhuruyengwe
 */
public class Login {
    // storing  user details
    private String storedUsername;
    private String storedPassword;
    private String storedCellNumber;
    private String firstName;
    private String lastName;

    // 1. Check username if it contains  underscore and is no more than five characters long.
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // 2. Check password complexity
    public boolean checkPasswordComplexity(String password) {
        boolean hasLength = password.length() >= 8;
        boolean hasCapital = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasLength && hasCapital && hasNumber && hasSpecial;
    }

    // 3. Check cell number 
    // Reference: GeeksforGeeks (2025) ‘Validate Phone Numbers (with Country Code extension) using Regular Expression’. Available at: https://www.geeksforgeeks.org/dsa/validate-phone-numbers-with-country-code-extension-using-regular-expression/ (Accessed: 28 September 2026).
    public boolean checkCellPhoneNumber(String cellNumber) {
    if (cellNumber == null || cellNumber.isEmpty()) {
        return false;
    }
    // +27 followed by  9 digits 
    String pattern = "^\\+27[0-9]{9}$";
    return cellNumber.matches(pattern);
}

    // 4. Register user - returns message
    public String registerUser(String firstName, String lastName, String username, String password, String cellNumber) {
    if (!checkUserName(username)) {
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }
    if (!checkPasswordComplexity(password)) {
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }
    if (!checkCellPhoneNumber(cellNumber)) {
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }
    this.firstName = firstName;
    this.lastName = lastName;
    this.storedUsername = username;
    this.storedPassword = password;
    this.storedCellNumber = cellNumber;
    return "Username successfully captured.\nPassword successfully captured.\nCell number successfully captured.";
}

    // 5. Logging in  user
    public boolean loginUser(String username, String password) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

   // 6. Return login status massage
public String returnLoginStatus(boolean success) {
    if (success) {
        return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
    }
    return "Username or password incorrect, please try again.";
}
}