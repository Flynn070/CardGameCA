import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testGetRankValidCard() {
        Card validCard = new Card(2);
        assertEquals(validCard.getRank(), 2);   //checks value is intact after getting
    }
}