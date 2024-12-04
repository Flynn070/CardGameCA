import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck testDeck;

    @BeforeEach
    void setUp() {
        Queue<Card> testCards = new LinkedList<Card>(Arrays.asList(new Card(1), new Card(2), new Card(3), new Card(4)));    //deck of 1,2,3,4
        testDeck = new Deck(testCards, 1);
    }

    @Test
    void testGetDeckID() {
        assertEquals(testDeck.getDeckID(), 1);
    }

    @Test
    void testDiscard() {
        Card testCard = new Card(5);
        assertFalse(testDeck.getDeck().contains(testCard)); // we know that 5 is not in the test deck we defined
        testDeck.discard(testCard); // should add 5 to the test deck
        assertTrue(testDeck.getDeck().contains(testCard)); // checks if the card has been added
    }

    @Test
    void testDraw() {
        for (int i = 0; i<4; i++){
            assertNotNull(testDeck.draw());                                 //empties deck of all cards
        }
        assertThrows(NoSuchElementException.class, () -> testDeck.draw());  //ensures deck has been emptied
    }

    @Test
    void testOutputDeck() {
        testDeck.outputDeck();
        assertTrue(new File("deck" + 1 + "_output.txt").isFile()); // checks if the output file has been made
    }
}