package com.mycompany.chatapp;

/**
 *
 * @author Shania Mhuruyengwe
 */
//Connected Java login class to main chatapp class
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Chatapp {

    public static void main(String[] args) {

        Login login = new Login();

        try (Scanner scanner = new Scanner(System.in)) {

            // ================= REGISTRATION =================
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

            String message = login.registerUser(
                    firstName, lastName, username, password, cellNumber
            );

            System.out.println(message);

            // *****LOGIN*****
            System.out.println("\n *** LOGIN***");

            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            boolean success = login.loginUser(
                    loginUsername, loginPassword
            );

            System.out.println(login.returnLoginStatus(success));

            // Part 2 ~ Allow messages if login was successful
            if (!success) {
                System.out.println("Login failed. Closing program.");
                return;
            }

            // *****QUICKCHAT*****

            // Part 2 ~Welcome message
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

            boolean running = true;
            int messagesToSend = 0;
            int messagesEntered = 0;

            //Asks a user how many messages to enter
            while (messagesToSend <= 0) {
                String numberInput = JOptionPane.showInputDialog(
                        "How many messages do you wish to enter?"
                );

                if (numberInput == null) {
                    return;
                }

                try {
                    messagesToSend = Integer.parseInt(numberInput.trim());

                    if (messagesToSend <= 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Please enter a number greater than 0."
                        );
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Please enter a valid whole number."
                    );
                }
            }

            // lOOP that runs  until the user quits
            while (running) {

                //numeric menu
                String menu = "Choose an option:\n"
                        + "1) Send Messages\n"
                        + "2) Show recently sent messages\n"
                        + "3) Quit";

                String menuChoice = JOptionPane.showInputDialog(menu);

        // If a user clicks x the loop cancels
                if (menuChoice == null) {
                    break;
                }

                int choice;

                try {
                    choice = Integer.parseInt(menuChoice.trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                            null, "Please enter 1, 2 or 3."
                    );
                    continue;
                }

                switch (choice) {

                    case 1:
                        if (messagesEntered >= messagesToSend) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "You have entered all "
                                    + messagesToSend + " messages."
                            );
                            break;
                        }

                        //Repeat Loop
                        while (messagesEntered < messagesToSend) {

                            String recipient = JOptionPane.showInputDialog(
                                    "Enter recipient cell number (+27...):"
                            );

                            if (recipient == null) {
                                break;
                            }

                            if (!login.checkCellPhoneNumber(recipient.trim())) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Cell phone number is incorrectly formatted "
                                        + "or does not contain an international code. "
                                        + "Please correct the number and try again."
                                );
                                continue;
                            }

                            // Get the message text
                            String text = JOptionPane.showInputDialog(
                                    "Enter your message (max 250 characters):"
                            );

                            if (text == null) {
                                break;
                            }

                            text = text.trim();

                            if (text.isEmpty()) {
                                JOptionPane.showMessageDialog(
                                        null, "Please enter a message."
                                );
                                continue;
                            }

                            // Check the message length
                            if (text.length() > 250) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Message exceeds 250 characters by "
                                        + (text.length() - 250)
                                        + ", please reduce size."
                                );
                                continue;
                            }

                            // Code to make message object - it generates random ID and number and stores them
                            Message msg = new Message(recipient, text);

                            // Check the recipient using message method
                            JOptionPane.showMessageDialog(
                                    null, msg.checkRecipientStatus()
                            );

                            JOptionPane.showMessageDialog(
                                    null, msg.checkMessageLengthStatus()
                            );

                            // User makes a choice to send or disregard message
                            String result = msg.SentMessage();

                            // Show the result of the choice
                            JOptionPane.showMessageDialog(null, result);

                            if (result.equals("Message action cancelled.")
                                    || result.startsWith("Message could not be stored")) {
                                continue;
                            }

                            // Message passed all checks
                            messagesEntered++;

                            // messages  displayed
                            if (messagesEntered == messagesToSend) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Total messages sent: "
                                        + Message.returnTotalMessagess()
                                );
                            }
                        }
                        break;

                    case 2:
                        JOptionPane.showMessageDialog(null, "Coming Soon.");
                        break;

                    case 3:
                        JOptionPane.showMessageDialog(null, "Goodbye!");
                        running = false;
                        break;

                    default:
                        JOptionPane.showMessageDialog(
                                null,
                                "Invalid choice. Please pick 1, 2 or 3."
                        );
                }
            }
        }
    }
}