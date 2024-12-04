import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

public class Deck {
    private volatile Queue<Card> deck;
    private final int deckID;

    public Deck(Queue<Card>_deck, int _deckID) {
        this.deck = _deck;
        this.deckID = _deckID;
    }

    //Getter method ----------------------------------------------------------------------------------------------------

    public int getDeckID() {
        return this.deckID;
    }

    public Queue<Card> getDeck() {return this.deck;}

    //Deck methods -----------------------------------------------------------------------------------------------------

    //Used when card is discarded to the deck from a hand
    synchronized void discard(Card newCard){
        this.deck.add(newCard);
    }

    //Draws a card from the deck
    synchronized Card draw(){
        return deck.remove();
    }

    //Called at the end of the game
    public void outputDeck(){
        String currFileName = "deck" + this.deckID + "_output.txt";         //builds name of file to output deck to
        StringBuilder deckOutput = new StringBuilder("deck" + this.deckID + " contents:");
        for (Card currCard: this.deck){                                     //adds each card in the deck to a string to output
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

