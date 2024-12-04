import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.sql.Array;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayer {
    private Player testPlayer;
    private CyclicBarrier testCoordinator;

    @BeforeEach
    void setUp() {
        Card[] testHand = {new Card(1), new Card(2), new Card(3), new Card(4)};
        testPlayer = new Player(1, testHand, testCoordinator);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getID() {
        assertEquals(testPlayer.getID(), 1);
    }

    @Test
    void hasWon() {
        AtomicBoolean result = new AtomicBoolean(testPlayer.hasWon());
        try{
            Field privateWon = Player.class.getDeclaredField("won");
            privateWon.setAccessible(true);
            AtomicBoolean testWon = new AtomicBoolean(testPlayer.hasWon());
            assertEquals(testWon.get(), result.get());
        } catch (NoSuchFieldException e) {
            fail("Cannot find field 'won'");
        }
    }

    @Test
    void setDrawDeck() {
    }

    @Test
    void setDiscardDeck() {
    }

    @Test
    void start() {
    }

    @Test
    void stop() {
    }

    @Test
    void checkIfWon() {
        try {
            Method checkIfWonMethod = Player.class.getDeclaredMethod("checkIfWon");
            checkIfWonMethod.setAccessible(true);
            assertFalse((boolean)checkIfWonMethod.invoke(testPlayer));
        } catch (NoSuchMethodException e) {
            fail("checkIfWon method not found");
        } catch (InvocationTargetException e) {
            fail("checkIfWon threw exception");
        } catch (IllegalAccessException e) {
            fail("Do not have permission to access checkIfWon");
        }
    }

    @Test
    void otherPlayerWon() {
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

        assertEquals(lastLine, "Player 1 is informed Player 2 won.");
    }

    @Test
    void takeTurn() {
        //TODO make 2 decks and assign via setDiscardDeck and setDrawDeck
    }

    @Test
    void endTurn() {
        //TODO make 2 decks and assign via setDiscardDeck and setDrawDeck
    }

    @Test
    void run() {
    }
}