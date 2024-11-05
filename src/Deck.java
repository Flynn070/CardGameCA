import java.util.Queue;

public class Deck {
    private volatile Queue<Card> deck;
    private int deckID;

    public Deck(Queue<Card>_deck, int _deckID) {
        this.deck = _deck;
        this.deckID = _deckID;
    }

    //Getter and Setter methods
    public Queue<Card> getDeck() {
        return this.deck;
    }
    public int getDeckID() {
        return this.deckID;
    }

    //Used when card is discarded to the deck from a hand
    synchronized void discard(Card newCard){
        this.deck.add(newCard);
    }
    //Draws a card from the deck
    synchronized Card draw(){
        return deck.remove();
    }
}
