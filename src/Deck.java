import java.io.FileWriter;
import java.io.IOException;
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

    public void outputDeck(){
        String currFileName = "deck" + this.deckID + "_output.txt";    //builds name of file to output deck to
        StringBuilder deckOutput = new StringBuilder("deck" + this.deckID + " contents:");
        for (Card currCard: this.deck){
            deckOutput.append(" ").append(currCard.getRank());
        }
        try{
            FileWriter deckWriter = new FileWriter(currFileName);
            deckWriter.write(deckOutput.toString());
            deckWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

