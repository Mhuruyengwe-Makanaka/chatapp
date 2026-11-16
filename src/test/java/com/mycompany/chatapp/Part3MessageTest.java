/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.chatapp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Shania Mhuruyengwe
 */
public class Part3MessageTest {

    public Part3MessageTest() {
    }

    // Set up the 5 test messages from the Part 3 brief before each test
    @BeforeEach
    public void setUp() throws Exception {

        // remove any old JSON file and clear all static arrays
        Files.deleteIfExists(Paths.get("storedMessages.json"));
        Message.clearAllData();

        // Test Data Message 1 - Flag: Sent
        Message msg1 = new Message(
                "+27834557896", "Did you get the cake?"
        );
        msg1.SentMessage(1);

        // Test Data Message 2 - Flag: Stored
        Message msg2 = new Message(
                "+27838884567",
                "Where are you? You are late! I have asked you to be on time."
        );
        msg2.SentMessage(3);

        // Test Data Message 3 - Flag: Disregard
        Message msg3 = new Message(
                "+27834484567", "Yohoooo, I am at your gate."
        );
        msg3.disregard();

        // Test Data Message 4 - Flag: Sent, forced ID for the search test
        Message msg4 = new Message(
                "+27834557896", "It is dinner time !"
        );
        msg4.setMessageID("0838884567");
        msg4.SentMessage(1);

        // Test Data Message 5 - Flag: Stored
        Message msg5 = new Message(
                "+27838884567", "Ok, I am leaving without you."
        );
        msg5.SentMessage(3);
    }

    // 1. Sent Messages array correctly populated
    @Test
    public void testSentMessagesArrayPopulated() {
        ArrayList<Message> sent = Message.getSentMessages();

        assertEquals(2, sent.size());

        assertEquals(
                "Did you get the cake?",
                sent.get(0).getMessageText()
        );

        assertEquals(
                "It is dinner time !",
                sent.get(1).getMessageText()
        );
    }

    // 2. Display the longest stored message
    @Test
    public void testDisplayLongestMessage() {
        String longest = Message.displayLongestStoredMessage();

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                longest
        );
    }

    // 3. Search for message ID 0838884567
    @Test
    public void testSearchByMessageID() {
        String result = Message.searchByMessageID("0838884567");

        assertTrue(result.contains("It is dinner time !"));

        assertTrue(result.contains("+27834557896"));
    }

    // 4. Search all messages for a particular recipient
    @Test
    public void testSearchByRecipient() {
        String result = Message.searchByRecipient("+27838884567");

        assertTrue(result.contains(
                "Where are you? You are late! I have asked you to be on time."
        ));

        assertTrue(result.contains(
                "Ok, I am leaving without you."
        ));
    }

    // 5. Delete a message using its message hash
    @Test
    public void testDeleteByMessageHash() {
        String hash = null;

        // find the hash of message 2
        for (Message msg : Message.getStoredMessages()) {
            if (msg.getMessageText().equals(
                    "Where are you? You are late! I have asked you to be on time.")) {
                hash = msg.createMessageHash();
                break;
            }
        }

        assertNotNull(hash, "Message 2 hash not found");

        String result = Message.deleteByMessageHash(hash);

        assertEquals(
                "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                result
        );

        // confirm it is no longer in the stored messages
        for (Message msg : Message.getStoredMessages()) {
            assertNotEquals(
                    "Where are you? You are late! I have asked you to be on time.",
                    msg.getMessageText()
            );
        }
    }

    // 6. Display Report for sent messages
    @Test
    public void testDisplayReport() {
        String report = Message.displayReport();

        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time !"));
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient: +27834557896"));
    }
}