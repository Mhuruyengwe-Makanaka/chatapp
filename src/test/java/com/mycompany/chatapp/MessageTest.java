package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Shania Mhuruyengwe
 */
public class MessageTest {

    public MessageTest() {
    }

    // 1. test method length (not more than 250)
    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight"
        );

        assertTrue(msg.checkMessageLength());

        assertEquals(
                "Message ready to send.",
                msg.checkMessageLengthStatus()
        );
    }

    @Test
    public void testMessageLengthFailure() {
        // build a message exactly 260 characters long (10 over the limit)
        StringBuilder longText = new StringBuilder();

        for (int i = 0; i < 260; i++) {
            longText.append("a");
        }

        Message msg = new Message(
                "+27718693002", longText.toString()
        );

        assertFalse(msg.checkMessageLength());

        assertEquals(
                "Message exceeds 250 characters by 10, please reduce size.",
                msg.checkMessageLengthStatus()
        );
    }

    // 2. test recipient cell number
    @Test
    public void testRecipientSuccess() {
        Message msg = new Message("+27718693002", "Hi");

        assertEquals(1, msg.checkRecipientCell());

        assertEquals(
                "Cell phone number successfully captured.",
                msg.checkRecipientStatus()
        );
    }

    @Test
    public void testRecipientFailure() {
        Message msg = new Message("08575975889", "Hi");

        assertEquals(0, msg.checkRecipientCell());

        assertEquals(
                "Cell phone number is incorrectly formatted or does "
                + "not contain an international code. Please correct "
                + "the number and try again.",
                msg.checkRecipientStatus()
        );
    }

    // 3. test message id
    @Test
    public void testMessageIDCreated() {
        Message msg = new Message("+27718693002", "Hi");

        assertTrue(
                msg.checkMessageID(),
                "Message ID generated: " + msg.getMessageID()
        );

        assertEquals(10, msg.getMessageID().length());

        assertTrue(msg.getMessageID().matches("[0-9]{10}"));
    }

    // 4. test message hash for test case 1
    //    id is random, so first two digits aren't always "00".
    //    we build the expected value from the actual id.
    @Test
    public void testMessageHashTestCase1() {
        Message msg = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight"
        );

        String expected = msg.getMessageID().substring(0, 2)
                + ":" + msg.getMessageNumber()
                + ":HITONIGHT";

        assertEquals(expected, msg.createMessageHash());
    }

    // test the remainder of the message hashes in a loop
    @Test
    public void testMessageHashesInLoop() {
        String[] messages = {
            "Hi Mike, can you join us for dinner tonight",
            "Hi Keegan, did you receive the payment?",
            "Hello world",
            "See you tomorrow"
        };

   String[] expectedWords = {
            "HITONIGHT",
            "HIPAYMENT",
            "HELLOWORLD",
            "SEETOMORROW"
        };

        for (int i = 0; i < messages.length; i++) {
            Message msg = new Message(
                    "+27718693002", messages[i]
            );

            String expected = msg.getMessageID().substring(0, 2)
                    + ":" + msg.getMessageNumber()
                    + ":" + expectedWords[i];

            assertEquals(expected, msg.createMessageHash());
        }
    }

    // 5. test SentMessage options
    //    needs the SentMessage(int) overload in Message.java
    @Test
    public void testSentMessageSend() {
        Message msg = new Message("+27718693002", "Hi Mike");

        int totalBefore = Message.returnTotalMessagess();

        assertEquals(
                "Message successfully sent.",
                msg.SentMessage(1)
        );

        assertEquals(
                totalBefore + 1,
                Message.returnTotalMessagess()
        );

        assertEquals(
                "This message has already been sent.",
                msg.SentMessage(1)
        );

        assertEquals(
                totalBefore + 1,
                Message.returnTotalMessagess()
        );
    }

    @Test
    public void testSentMessageDisregard() {
        Message msg = new Message(
                "08575975889",
                "Hi Keegan, did you receive the payment?"
        );

        int totalBefore = Message.returnTotalMessagess();

        assertEquals(
                "Press 0 to delete message.",
                msg.SentMessage(2)
        );

        assertEquals(
                totalBefore,
                Message.returnTotalMessagess()
        );
    }

    @Test
    public void testSentMessageStore() {
        Message msg = new Message("+27718693002", "Hi Mike");

        int totalBefore = Message.returnTotalMessagess();

        assertEquals(
                "Message successfully stored.",
                msg.SentMessage(3)
        );

        assertEquals(
                totalBefore,
                Message.returnTotalMessagess()
        );
    }
}