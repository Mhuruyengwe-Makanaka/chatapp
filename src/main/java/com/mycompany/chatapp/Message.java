package com.mycompany.chatapp;

/**
 *
 * @author Shania Mhuruyengwe
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Message {

    // Information for each message
    private String msgID;
    private int msgNum;
    private String recipient;
    private String message;

    // Checks if a message has been sent or stored
    private boolean sent = false;
    private boolean stored = false;
    private boolean disregarded = false;

    // Shared by all Message objects while the program is running.
    private static int msgcounter= 0;

    // Arrays that store messages according to their status
    private static final ArrayList<Message> sentMsgs = new ArrayList<>();

    private static final ArrayList<Message> disregardedMsgs = new ArrayList<>();

    private static final ArrayList<Message> storedMsgs = new ArrayList<>();

    // Arrays that store message hashes and IDs
    private static final ArrayList<String> messageHashes = new ArrayList<>();

    private static final ArrayList<String> messageIDs = new ArrayList<>();

    // Prevent duplicate message IDs
    private static final Set<String> idList = new HashSet<>();

    //Random number generator for message ID's
    private static final SecureRandom rng = new SecureRandom();

    // JSON file
    private static final Path jsonFile = Paths.get("storedMessages.json");

    //Constructor that  runs whenever a new message is created.
    public Message(String recipient, String message) {
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();

        this.msgID = genID();
        this.msgNum = msgcounter;
        msgcounter++;
    }

    //Constructor used when reading messages back from the JSON file.
    //keeps the existing id and number so the hash stays the same
    private Message(String recipient, String message, String msgID, int msgNum) {
        this.recipient = recipient == null ? "" : recipient.trim();
        this.message = message == null ? "" : message.trim();
        this.msgID = msgID;
        this.msgNum = msgNum;

        // keep the counter ahead of anything we loaded
        if (msgNum >= msgcounter) {
            msgcounter = msgNum + 1;
        }
    }

    // Generate a unique ten-digit ID for this program session.
    private static String genID() {
        String newID;

        do {
            newID = (rng.nextInt(9) + 1)
                    + String.format(
                            "%09d",
                            rng.nextInt(1_000_000_000)
                    );
        } while (!idList.add(newID));

        return newID;
    }

    //  Check that the message ID has no more than ten characters.
    public boolean checkMessageID() {
        return msgID != null
                && msgID.matches("[0-9]{10}");
    }

    // Return 1 for a valid recipient or 0 for an invalid one.
    public int checkRecipientCell() {
        if (recipient.matches("^\\+27[0-9]{9}$")) {
            return 1;
        }

        return 0;
    }

    public String recipientMsg() {
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

    public String lengthMsg() {
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

    // Register the message hash and ID so both arrays stay in sync
    private void registerMessageDetails() {

        if (!messageHashes.contains(createMessageHash())) {
            messageHashes.add(createMessageHash());
        }

        if (!messageIDs.contains(msgID)) {
            messageIDs.add(msgID);
        }
    }

    //  Generate the message hash.
    public String createMessageHash() {
        if (message.isEmpty()) {
            return "";
        }

        //Array to store text when there is white space.
        String[] words = message.split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        // Remove punctuation from the first and last words
        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        return (
                msgID.substring(0, 2)
                + ":" + msgNum
                + ":" + firstWord + lastWord
        ).toUpperCase();
    }

    // Allow the user to send and store the message.
    public String SentMessage() {
        String validation = checkAll();

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
                disregardedMsgs.add(this);
                registerMessageDetails();
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

        String validation = checkAll();

        if (validation != null) {
            return validation;
        }

        //Running cases for a selected button.
        switch (choice) {
            case 1:
                sent = true;
                sentMsgs.add(this);
                registerMessageDetails();

                return "Message successfully sent.";

            case 3:
                return storeMessage();

            default:
                return "Message action cancelled.";
        }
    }

    private String checkAll() {
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
            return recipientMsg();
        }

        if (!checkMessageLength()) {
            return lengthMsg();
        }

        return null;
    }

    //  Return all messages sent during this program .
    public String printMessages() {
        if (sentMsgs.isEmpty()) {
            return "No messages have been sent.";
        }

        StringBuilder result = new StringBuilder();

        for (Message sentMessage : sentMsgs) {
            result.append(sentMessage.getMessageDetails());
            result.append("\n\n");
        }

        return result.toString().trim();
    }

    // Return the number actually sent.
    public static int returnTotalMessagess() {
        return sentMsgs.size();
    }

    //  Save this message in a JSON array.
    public String storeMessage() {
        String validation = checkAll();

        if (validation != null) {
            return validation;
        }

        String jsonMessage =
                "  {\n"
                + "    \"messageID\": \""
                + escapeJson(msgID) + "\",\n"
                + "    \"messageNumber\": "
                + msgNum + ",\n"
                + "    \"messageHash\": \""
                + escapeJson(createMessageHash()) + "\",\n"
                + "    \"recipient\": \""
                + escapeJson(recipient) + "\",\n"
                + "    \"message\": \""
                + escapeJson(message) + "\"\n"
                + "  }";

        try {
            String existing = "[]";

            if (Files.exists(jsonFile)) {
                existing = new String(
                        Files.readAllBytes(jsonFile),
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
                    jsonFile,
                    updatedJson.getBytes(StandardCharsets.UTF_8)
            );

            stored = true;
            storedMsgs.add(this);
            registerMessageDetails();

            return "Message successfully stored.";

        } catch (IOException e) {
            return "Message could not be stored: " + e.getMessage();
        }
    }

    //Geeting method
    private String getMessageDetails() {
        return "MessageID: " + msgID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    // getter so tests and other classes can read the message id
    public String getMessageID() {
        return msgID;
    }

    public int getMessageNumber() {
        return msgNum;
    }

    // getter for the message text
    public String getMessageText() {
        return message;
    }

    // getter for the recipient
    public String getRecipient() {
        return recipient;
    }

    // set the message id (used by unit tests)
    public void setMessageID(String id) {
        this.msgID = id;
    }

    // mark this message as disregarded (used by unit tests and the GUI)
    public void disregard() {
        if (!disregarded) {
            disregarded = true;
            disregardedMsgs.add(this);
            registerMessageDetails();
        }
    }

    // getters for the arrays so tests and Chatapp can use them
    public static ArrayList<Message> getSentMessages() {
        return sentMsgs;
    }

    public static ArrayList<Message> getDisregardedMessages() {
        return disregardedMsgs;
    }

    public static ArrayList<Message> getStoredMessages() {
        return storedMsgs;
    }

    public static ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public static ArrayList<String> getMessageIDs() {
        return messageIDs;
    }

    // clear all the static data (used by unit tests)
    public static void clearAllData() {
        msgcounter = 0;
        sentMsgs.clear();
        disregardedMsgs.clear();
        storedMsgs.clear();
        messageHashes.clear();
        messageIDs.clear();
        idList.clear();
    }

    // ================= Part 3 methods =================

    // a. Display the sender and recipient of all stored messages
    public static String displayStoredSenderRecipient() {
        if (storedMsgs.isEmpty()) {
            return "No stored messages.";
        }

        StringBuilder result = new StringBuilder();

        for (Message storedMessage : storedMsgs) {
            result.append("Sender: Developer, Recipient: ")
                  .append(storedMessage.recipient)
                  .append("\n");
        }

        return result.toString().trim();
    }

    // b. Display the longest stored message
    public static String displayLongestStoredMessage() {
        if (storedMsgs.isEmpty()) {
            return "No stored messages.";
        }

        Message longest = storedMsgs.get(0);

        for (Message storedMessage : storedMsgs) {
            if (storedMessage.message.length() > longest.message.length()) {
                longest = storedMessage;
            }
        }

        return longest.message;
    }

    // c. Search for a message ID and return the recipient and message
    public static String searchByMessageID(String id) {
        for (Message sentMessage : sentMsgs) {
            if (sentMessage.msgID.equals(id)) {
                return "Recipient: " + sentMessage.recipient
                        + "\nMessage: " + sentMessage.message;
            }
        }

        for (Message storedMessage : storedMsgs) {
            if (storedMessage.msgID.equals(id)) {
                return "Recipient: " + storedMessage.recipient
                        + "\nMessage: " + storedMessage.message;
            }
        }

        return "Message ID not found.";
    }

    // d. Search all sent or stored messages for a particular recipient
    public static String searchByRecipient(String recipient) {
        StringBuilder result = new StringBuilder();

        for (Message sentMessage : sentMsgs) {
            if (sentMessage.recipient.equals(recipient)) {
                result.append(sentMessage.message).append("\n");
            }
        }

        for (Message storedMessage : storedMsgs) {
            if (storedMessage.recipient.equals(recipient)) {
                result.append(storedMessage.message).append("\n");
            }
        }

        if (result.length() == 0) {
            return "No messages found for recipient " + recipient;
        }

        return result.toString().trim();
    }

    // e. Delete a stored message using its message hash
    public static String deleteByMessageHash(String hash) {
        Message toDelete = null;

        for (Message storedMessage : storedMsgs) {
            if (storedMessage.createMessageHash().equals(hash)) {
                toDelete = storedMessage;
                break;
            }
        }

        if (toDelete == null) {
            return "Message with hash " + hash + " not found.";
        }

        storedMsgs.remove(toDelete);
        messageHashes.remove(hash);
        messageIDs.remove(toDelete.msgID);
        rewriteJsonFile();

        return "Message: \"" + toDelete.message
                + "\" successfully deleted.";
    }

    // rewrite the JSON file from the current storedMsgs list
    private static void rewriteJsonFile() {
        StringBuilder result = new StringBuilder("[\n");

        for (int i = 0; i < storedMsgs.size(); i++) {
            Message storedMessage = storedMsgs.get(i);

            result.append("  {\n")
                  .append("    \"messageID\": \"")
                  .append(escapeJson(storedMessage.msgID)).append("\",\n")
                  .append("    \"messageNumber\": ")
                  .append(storedMessage.msgNum).append(",\n")
                  .append("    \"messageHash\": \"")
                  .append(escapeJson(storedMessage.createMessageHash())).append("\",\n")
                  .append("    \"recipient\": \"")
                  .append(escapeJson(storedMessage.recipient)).append("\",\n")
                  .append("    \"message\": \"")
                  .append(escapeJson(storedMessage.message)).append("\"\n")
                  .append("  }");

            if (i < storedMsgs.size() - 1) {
                result.append(",");
            }

            result.append("\n");
        }

        result.append("]");

        try {
            Files.write(
                    jsonFile,
                    result.toString().getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            // nothing we can do - the file will be recreated on next store
        }
    }

    // f. Display a report of all sent messages
    public static String displayReport() {
        if (sentMsgs.isEmpty()) {
            return "No sent messages.";
        }

        StringBuilder result = new StringBuilder();

        for (Message sentMessage : sentMsgs) {
            result.append("Message Hash: ")
                  .append(sentMessage.createMessageHash()).append("\n")
                  .append("Recipient: ")
                  .append(sentMessage.recipient).append("\n")
                  .append("Message: ")
                  .append(sentMessage.message).append("\n\n");
        }

        return result.toString().trim();
    }

    // Read the JSON file and put the stored messages into the storedMsgs array
    public static void loadStoredMessagesFromJson() {
        storedMsgs.clear();

        if (!Files.exists(jsonFile)) {
            return;
        }

        try {
            String content = new String(
                    Files.readAllBytes(jsonFile),
                    StandardCharsets.UTF_8
            ).trim();

            if (content.isEmpty()
                    || !content.startsWith("[")
                    || !content.endsWith("]")) {
                return;
            }

            String inner = content.substring(1, content.length() - 1).trim();

            if (inner.isEmpty()) {
                return;
            }

            int index = 0;

            while (index < inner.length()) {
                int start = inner.indexOf("{", index);

                if (start == -1) {
                    break;
                }

                int end = inner.indexOf("}", start);

                if (end == -1) {
                    break;
                }

                String object = inner.substring(start, end + 1);

                String id = extractJsonValue(object, "messageID");
                String numberText = extractJsonValue(object, "messageNumber");
                int number = numberText.isEmpty()
                        ? 0
                        : Integer.parseInt(numberText);
                String to = extractJsonValue(object, "recipient");
                String text = extractJsonValue(object, "message");

                Message loaded = new Message(to, text, id, number);
                loaded.stored = true;
                storedMsgs.add(loaded);

                if (!messageIDs.contains(id)) {
                    messageIDs.add(id);
                }

                String hash = loaded.createMessageHash();

                if (!messageHashes.contains(hash)) {
                    messageHashes.add(hash);
                }

                index = end + 1;
            }

        } catch (IOException e) {
            // no stored messages to load
        }
    }

    // helper to pull a string value out of a small json object
    private static String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*\"?([^\",\\n}]*)\"?"
        );
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    private static String escapeJson(String text) {
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