import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testCheckIfValidValidInput() {
        try {
            Method checkIfValidMethod = Main.class.getDeclaredMethod("checkIfValid", String.class);
            checkIfValidMethod.setAccessible(true);
            assertEquals((int)checkIfValidMethod.invoke(Main.class, "3"), 3);       //Checks if string is cast to integer correctly
        } catch (NoSuchMethodException e) {
            fail("checkIfValid method not found");
        } catch (InvocationTargetException e) {
            fail("checkIfValid method threw exception");
        } catch (IllegalAccessException e) {
            fail("cannot access checkIfValid method");
        }
    }

    @Test
    void testCheckIfValidInvalidInput() throws NoSuchMethodException {
        try {
            Method checkIfValidMethod = Main.class.getDeclaredMethod("checkIfValid", String.class);
            checkIfValidMethod.setAccessible(true);
            assertThrows(InvocationTargetException.class, () -> checkIfValidMethod.invoke(Main.class, "0"));    //checks that correct error is thrown when non integer is passed in
        } catch (NoSuchMethodException e) {
            fail("checkIfValid method not found");
        } catch (SecurityException e) {
            fail("cannot access checkIfValid method");
        }
    }

    @Test
    void testPlayerAmountInput() {
        Scanner usrInput = new Scanner("abc\n\n0\n4");          //list of invalid inputs followed by a valid one
        try {
            Method PlayerAmountInputMethod = Main.class.getDeclaredMethod("playerAmountInput", Scanner.class);
            PlayerAmountInputMethod.setAccessible(true);
            int playerNum = (int)PlayerAmountInputMethod.invoke(Main.class, usrInput);

            assertInstanceOf(Integer.class, playerNum);             //asserts returned value is integer in valid range
            assertTrue(playerNum > 0);
        } catch (NoSuchMethodException e) {
            fail("playerAmountInput method not found");
        } catch (InvocationTargetException e) {
            fail("playerAmountInput method threw exception");
        } catch (IllegalAccessException e) {
            fail("cannot access playerAmountInput method");
        }
    }

    @Test
    void testCompilePack(){
        //This test assumes test_pack.txt exists, and contains 32 lines, each with a valid number for a card
        Scanner usrInput = new Scanner("abc\ntest_pack.txt");
        int numPlayers = 4;

        try {
            Method compilePackMethod = Main.class.getDeclaredMethod("compilePack", Scanner.class, int.class);
            compilePackMethod.setAccessible(true);
            ArrayList<Card> pack = (ArrayList<Card>)compilePackMethod.invoke(Main.class, usrInput, numPlayers);
            assertEquals(pack.size(), 32);              //checks that all lines of pack file are accounted for

        } catch (NoSuchMethodException e) {
            fail("compilePack method not found");
        } catch (IllegalAccessException e) {
            fail("cannot access compilePack method");
        } catch (InvocationTargetException e) {
            fail("compilePack method threw exception");
        }
    }

    @Test
    void testInitialisePlayers(){
        //initialising conditions
        int numPlayers = 4;
        ArrayList<Card> pack = new ArrayList<>(Arrays.asList(new Card(1), new Card(2), new Card(3), new Card(4), new Card(5), new Card(6), new Card(7), new Card(8), new Card(9), new Card(10), new Card(11), new Card(12), new Card(13), new Card(14), new Card(15), new Card(16)));
        CyclicBarrier turnCoordinator = new CyclicBarrier(numPlayers);
        Random rand = new Random(System.currentTimeMillis());

        try{
            Method initialisePlayersMethod = Main.class.getDeclaredMethod("initialisePlayers", int.class, ArrayList.class, CyclicBarrier.class, Random.class);
            initialisePlayersMethod.setAccessible(true);
            Player[] players = (Player[])initialisePlayersMethod.invoke(Main.class, numPlayers, pack, turnCoordinator, rand);
            for(int i = 0; i < 4; i++){
                try {
                    Field privateHand = Player.class.getDeclaredField("hand");
                    privateHand.setAccessible(true);
                    Card[] hand = (Card[]) privateHand.get(players[i]);
                    assertEquals(hand.length, 4);                           //checks that each players hand is of length 4
                } catch (NoSuchFieldException e) {
                    fail("Cannot find field 'hand'");
                } catch (IllegalAccessException e) {
                    fail("cannot access field 'hand'");
                }
            }
        } catch (NoSuchMethodException e) {
            fail("initialisePlayers method not found");
        } catch (InvocationTargetException e) {
            fail("initialisePlayers method threw exception");
        } catch (IllegalAccessException e) {
            fail("cannot access initialisePlayers method");
        }
    }

    @Test
    void testInitialiseDecks(){
        //initialising conditions
        int numPlayers = 4;
        ArrayList<Card> pack = new ArrayList<>(Arrays.asList(new Card(1), new Card(2), new Card(3), new Card(4), new Card(5), new Card(6), new Card(7), new Card(8), new Card(9), new Card(10), new Card(11), new Card(12), new Card(13), new Card(14), new Card(15), new Card(16)));
        Random rand = new Random(System.currentTimeMillis());

        try{
            Method initialiseDecksMethod = Main.class.getDeclaredMethod("initialiseDecks", int.class, ArrayList.class, Random.class);
            initialiseDecksMethod.setAccessible(true);
            Deck[] decks = (Deck[])initialiseDecksMethod.invoke(Main.class, numPlayers, pack, rand);
            for(int i = 0; i < 4; i++){
                assertEquals(decks[i].getDeck().size(), 4);             //checks that all decks for each player have 4 cards
            }
        } catch (NoSuchMethodException e) {
            fail("cannot find method 'initialiseDecks'");
        } catch (InvocationTargetException e) {
            fail("initialiseDecks method threw exception");
        } catch (IllegalAccessException e) {
            fail("cannot access initialiseDecks method");
        }
    }
}