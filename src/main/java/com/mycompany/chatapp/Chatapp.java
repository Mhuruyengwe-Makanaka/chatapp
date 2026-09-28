/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapp;

/**
 *
 * @author Shania Mhuruyengwe
 */
//Connected Java login class to main chatapp class
import java.util.Scanner;

public class Chatapp {

    public static void main(String[] args) {

        Login login = new Login();

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("*** REGISTRATION ***");

            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Enter cell number (+27): ");
            String cellNumber = scanner.nextLine();

            String message = login.registerUser(firstName, lastName, username, password, cellNumber);

            System.out.println(message);
            
            System.out.println("\n *** LOGIN***");

            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            boolean success = login.loginUser(loginUsername, loginPassword);
           
            System.out.println(login.returnLoginStatus(success));
        }
    }
}
