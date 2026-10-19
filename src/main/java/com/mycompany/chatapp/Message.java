package com.mycompany.chatapp;

/**
 *
 * @author Shania Mhuruyengwe
 */

import javax.swing.JOptionPane;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Message {

    // Information for each message
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;

    // Checks if a message has been sent or stored
    private boolean sent = false;
    private boolean stored = false;
    private boolean disregarded = false;

    // Shared by all Message objects while the program is running.
    private static int nextMessageNumber = 0;

    // Array that stores all sent messages
    private static final ArrayList<Message> sentMessages = new ArrayList<>();

    private static final Set<String> usedIDs = new HashSet<>();

    //Random number generator for message ID's
    private static final SecureRandom random = new SecureRandom();

    // JSON file
    private static final Path storageFile = Paths.get("storedMessages.json");

    //Constructor that  runs whenever a new message is created.
    public Message(String recipient, String message) {
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();

        this.messageID = generateMessageID();
        this.messageNumber = nextMessageNumber;
        nextMessageNumber++;
    }

    // Generate a unique ten-digit ID for this program session.
    private static String generateMessageID() {
        String newID;

        do {
            newID = (random.nextInt(9) + 1)
                    + String.format(
                            Locale.ROOT,
                            "%09d",
                            random.nextInt(1_000_000_000)
                    );
        } while (!usedIDs.add(newID));

        return newID;
    }

    // 1. Check that the message ID has no more than ten characters.
    public boolean checkMessageID() {
        return messageID != null
                && messageID.matches("[0-9]{10}");
    }

    // 2. Return 1 for a valid recipient or 0 for an invalid one.
    public int checkRecipientCell() {
        if (recipient.matches("^\\+27[0-9]{9}$")) {
            return 1;
        }

        return 0;
    }

    public String checkRecipientStatus() {
        if (checkRecipientCell() == 1) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does "
                + "not contain an international code. Please correct "
                + "the number and try again.";
    }

    public boolean checkMessageLength() {
        return !message.isEmpty() && message.length() <= 250;
    }

    public String checkMessageLengthStatus() {
        if (message.isEmpty()) {
            return "Please enter a message.";
        }

        if (message.length() > 250) {
            return "Message exceeds 250 characters by "
                    + (message.length() - 250)
                    + ", please reduce size.";
        }

        return "Message ready to send.";
    }

    // 3. Generate the message hash.
    public String createMessageHash() {
        if (message.isEmpty()) {
            return "";
        }

        //Array to store text when there is white space.
        String[] words = message.split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        // Remove punctuation from the first and last words
        firstWord = firstWord.replaceAll("[^\\p{L}\\p{N}]", "");
        lastWord = lastWord.replaceAll("[^\\p{L}\\p{N}]", "");

        return (
                messageID.substring(0, 2)
                + ":" + messageNumber
                + ":" + firstWord + lastWord
        ).toUpperCase(Locale.ROOT);
    }

    // 4. Allow the user to send and store the message.
    public String SentMessage() {
        String validation = validateMessage();

        if (validation != null) {
            return validation;
        }

        String[] options = {
            "Send Message",
            "Disregard Message",
            "Store Message"
        };

        int choice = JOptionPane.showOptionDialog(
                null,
                "What would you like to do with this message?",
                "QuickChat",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        String result = SentMessage(choice + 1);

        if (choice == 0 && result.equals("Message successfully sent.")) {
            // Display the details in the required order.
            JOptionPane.showMessageDialog(
                    null,
                    getMessageDetails(),
                    "Message sent",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        if (choice == 1) {
            String deleteChoice = JOptionPane.showInputDialog(result);

            if ("0".equals(deleteChoice)) {
                disregarded = true;
                return "Message disregarded.";
            }

            return "Message action cancelled.";
        }

        return result;
    }

    // Sent message matched with unit tests
    public String SentMessage(int choice) {
        if (choice < 1 || choice > 3) {
            return "Message action cancelled.";
        }

        if (sent) {
            return "This message has already been sent.";
        }

        if (stored) {
            return "This message has already been stored.";
        }

        if (disregarded) {
            return "This message has already been disregarded.";
        }

        if (choice == 2) {
            return "Press 0 to delete message.";
        }

        String validation = validateMessage();

        if (validation != null) {
            return validation;
        }

        //Running cases for a selected button.
        switch (choice) {
            case 1:
                sent = true;
                sentMessages.add(this);
                return "Message successfully sent.";

            case 3:
                return storeMessage();

            default:
                return "Message action cancelled.";
        }
    }

    private String validateMessage() {
        if (sent) {
            return "This message has already been sent.";
        }

        if (stored) {
            return "This message has already been stored.";
        }

        if (disregarded) {
            return "This message has already been disregarded.";
        }

        if (!checkMessageID()) {
            return "Invalid message ID.";
        }

        if (checkRecipientCell() == 0) {
            return checkRecipientStatus();
        }

        if (!checkMessageLength()) {
            return checkMessageLengthStatus();
        }

        return null;
    }

    // 5. Return all messages sent during this program .
    public String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder result = new StringBuilder();

        for (Message sentMessage : sentMessages) {
            result.append(sentMessage.getMessageDetails());
            result.append("\n\n");
        }

        return result.toString().trim();
    }

    // 6. Return the number actually sent.
    public static int returnTotalMessagess() {
        return sentMessages.size();
    }

    // 7. Save this message in a JSON array.
    public String storeMessage() {
        String validation = validateMessage();

        if (validation != null) {
            return validation;
        }

        String jsonMessage =
                "  {\n"
                + "    \"messageID\": \""
                + escapeJson(messageID) + "\",\n"
                + "    \"messageNumber\": "
                + messageNumber + ",\n"
                + "    \"messageHash\": \""
                + escapeJson(createMessageHash()) + "\",\n"
                + "    \"recipient\": \""
                + escapeJson(recipient) + "\",\n"
                + "    \"message\": \""
                + escapeJson(message) + "\"\n"
                + "  }";

        try {
            String existing = "[]";

            if (Files.exists(storageFile)) {
                existing = new String(
                        Files.readAllBytes(storageFile),
                        StandardCharsets.UTF_8
                ).trim();
            }

            //If programme refuses to overwrite an unexpected file format.
            if (!existing.startsWith("[") || !existing.endsWith("]")) {
                return "Message could not be stored: "
                        + "the existing JSON file has an invalid format.";
            }

            String contents = existing.substring(
                    1, existing.length() - 1
            ).trim();

            String updatedJson;

            if (contents.isEmpty()) {
                updatedJson = "[\n" + jsonMessage + "\n]";
            } else {
                updatedJson = "[\n" + contents
                        + ",\n" + jsonMessage + "\n]";
            }

            Files.write(
                    storageFile,
                    updatedJson.getBytes(StandardCharsets.UTF_8)
            );

            stored = true;

            return "Message successfully stored.";

        } catch (IOException e) {
            return "Message could not be stored: " + e.getMessage();
        }
    }

    //Geeting method
    private String getMessageDetails() {
        return "MessageID: " + messageID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    // getter so tests and other classes can read the message id
    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    private String escapeJson(String text) {
        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;

                case '\\':
                    escaped.append("\\\\");
                    break;

                case '\n':
                    escaped.append("\\n");
                    break;

                case '\r':
                    escaped.append("\\r");
                    break;

                case '\t':
                    escaped.append("\\t");
                    break;

                default:
                    if (character < 32) {
                        escaped.append(
                                String.format("\\u%04x", (int) character)
                        );
                    } else {
                        escaped.append(character);
                    }
            }
        }

        return escaped.toString();
    }
}