import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {
    private Player testPlayer;
    private CyclicBarrier testCoordinator;
    Queue<Card> defaultDrawCards = new LinkedList<>(Arrays.asList(new Card(5), new Card(5), new Card(5), new Card(5)));
    Queue<Card> defaultDiscardCards = new LinkedList<>(Arrays.asList(new Card(6), new Card(6), new Card(6), new Card(6)));
    Deck defaultDrawDeck = new Deck(defaultDrawCards, 3);
    Deck defaultDiscardDeck = new Deck(defaultDiscardCards, 4);

    @BeforeEach
    void setUp() {
        Card[] testHand = {new Card(1), new Card(2), new Card(3), new Card(4)};
        testPlayer = new Player(1, testHand, testCoordinator);
        // these default decks are needed to test some of the functions individually
        testPlayer.setDrawDeck(defaultDrawDeck);
        testPlayer.setDiscardDeck(defaultDiscardDeck);
    }

    @Test
    void testGetID() {
        assertEquals(testPlayer.getID(), 1);
    }

    @Test
    void testHasWon() {
        AtomicBoolean result = new AtomicBoolean(testPlayer.hasWon());              //gets attribute through function
        try{
            Field privateWon = Player.class.getDeclaredField("won");
            privateWon.setAccessible(true);
            AtomicBoolean testWon = (AtomicBoolean)privateWon.get(testPlayer);      //manually gets attribute
            assertEquals(testWon.get(), result.get());                              //checks manually obtained attribute is equivalent
        } catch (NoSuchFieldException e) {
            fail("Cannot find field 'won'");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetDrawDeck() {
        assertEquals(testPlayer.getDrawDeck(), defaultDrawDeck);
    }

    @Test
    void testGetDiscardDeck() {
        assertEquals(testPlayer.getDiscardDeck(), defaultDiscardDeck);
    }

    @Test
    void testSetDrawDeck() {
        Queue<Card> testCards = new LinkedList<>(Arrays.asList(new Card(1), new Card(2), new Card(3), new Card(4)));
        Deck testDrawDeck = new Deck(testCards, 1);
        testPlayer.setDrawDeck(testDrawDeck);
        assertEquals(testDrawDeck, testPlayer.getDrawDeck());                       //checks deck is intact after setting
    }

    @Test
    void testSetDiscardDeck() {
        Queue<Card> testCards = new LinkedList<>(Arrays.asList(new Card(4), new Card(5), new Card(6), new Card(7)));
        Deck testDiscardDeck = new Deck(testCards, 2);
        testPlayer.setDiscardDeck(testDiscardDeck);
        assertEquals(testDiscardDeck, testPlayer.getDiscardDeck());                 //checks deck is intact after setting
    }

    @Test
    void testCheckIfWon() {
        try {
            Method checkIfWonMethod = Player.class.getDeclaredMethod("checkIfWon");
            checkIfWonMethod.setAccessible(true);
            assertFalse((boolean)checkIfWonMethod.invoke(testPlayer));              //checks newly made players are not set as winning
        } catch (NoSuchMethodException e) {
            fail("checkIfWon method not found");
        } catch (InvocationTargetException e) {
            fail("checkIfWon threw exception");
        } catch (IllegalAccessException e) {
            fail("Do not have permission to access checkIfWon");
        }
    }

    @Test
    void testOtherPlayerWon() {
        testPlayer.otherPlayerWon(2);
        String outputFile = "player1_output.txt";
        BufferedReader fileReader;
        String lastLine = "";
        try {
            fileReader = new BufferedReader(new FileReader(outputFile));                     //attempts to read last line of file
            lastLine = fileReader.readLine();
        } catch (FileNotFoundException e) {
            fail("File not found");
        } catch (NoSuchElementException e) {
            fail("File was empty");
        } catch (IOException e) {
            fail("Could not read file.");
        }

        assertEquals(lastLine, "Player 1 is informed Player 2 won.");                   //last line should be other player wins message
    }

    @Test
    void testTakeTurn() {
        try {
            Method takeTurnMethod = Player.class.getDeclaredMethod("takeTurn");
            takeTurnMethod.setAccessible(true);
            takeTurnMethod.invoke(testPlayer);                              //takes players turn so outputs are ready to test
        } catch (NoSuchMethodException e) {
            fail("takeTurn method not found");
        } catch (InvocationTargetException e) {
            fail("takeTurn threw exception");
        } catch (IllegalAccessException e) {
            fail("Do not have permission to access takeTurn");
        }

        try {
            Field privateHand = Player.class.getDeclaredField("hand");
            privateHand.setAccessible(true);
            Card[] hand = (Card[]) privateHand.get(testPlayer);
            assertEquals(hand[3].getRank(), 5);                         //deck to draw from only contains 5s
        } catch (NoSuchFieldException e) {
            fail("Cannot find field 'hand'");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Card[] expectedTurnResult = {new Card(6), new Card(6), new Card(6), new Card(6), new Card(2)};
        for (int i = 0; i <= 4; i++) {
            assertEquals(testPlayer.getDiscardDeck().getDeck().remove().getRank(), expectedTurnResult[i].getRank());    //checks each card is as expected
        }
        assertEquals(testPlayer.getDrawDeck().getDeck().size(), 3);      //size should be 3 as a card is removed
    }
}