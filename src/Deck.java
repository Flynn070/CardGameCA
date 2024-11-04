import java.util.Queue;

public class Deck {
    private Queue<Card> deck;
    private int deckID;

    public Deck(Queue<Card>_deck, int _deckID) {
        this.deck = _deck;
        this.deckID = _deckID;
    }

    //Getter and Setter methods
    public Queue<Card> getDeck() {
        return this.deck;
    }

    //Used when card is discarded to the deck from a hand
    public void discard(Card newCard){
        this.deck.add(newCard);
    }
    //Draws a card from the deck
    public Card draw(){
        return deck.remove();
    }
}
