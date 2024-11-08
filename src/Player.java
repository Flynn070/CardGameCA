import java.util.concurrent.atomic.AtomicBoolean;

public class Player implements Runnable {

    private final int playerID;
    private volatile Card[] hand;
    private Deck drawDeck; // deck with matching id
    private Deck discardDeck; // deck of the next player (can wrap around if this is the last player)
    private final AtomicBoolean running = new AtomicBoolean(false);

    //TODO ? hand in constructor seems cleaner?
    public Player(int playerNum, Card[] hand) {
        this.playerID = playerNum;
        this.hand = hand;
    }
    //Getter and Setter methods
    public int getID() {
        return playerID;
    }


    synchronized void endTurn() {
        try {
            wait();
        } catch (InterruptedException e) {
            System.out.println("help meeeeeee");
        }
    }


    public void setDrawDeck(Deck _drawDeck) {
        this.drawDeck = _drawDeck;
    }

    public void setDiscardDeck(Deck _discardDeck) {
        this.discardDeck = _discardDeck;
    }

    synchronized void notifyPlayers() {
        notifyAll();
    }

    public void stop() {
        running.set(false);
    }

    //TODO The combination of a card draw, and a discard should be treated as a single atomic action. - from the spec
    public void takeTurn(){
        //drawing and discarding oldest card that isn't players preferred denomination
        for (int i = 0; i < 4; i++) {
            if (this.hand[i].getRank() != playerID) {
                this.discardDeck.discard(this.hand[i]);
                System.out.printf("Player %d discards a %d to deck %d%n", this.playerID, this.hand[i].getRank(), this.discardDeck.getDeckID());
                //pushes the cards back so they retain age order
                for (int j = i; j < 3; j++){
                    this.hand[j] = this.hand[j+1];
                }
                this.hand[3] = this.discardDeck.draw();
                System.out.printf("Player %d draws a %d from deck %d%n", this.playerID, this.hand[3].getRank(), this.drawDeck.getDeckID());
                System.out.printf("Player %d current hand is %d %d %d %d%n", this.playerID, this.hand[0].getRank(), this.hand[1].getRank(), this.hand[2].getRank(), this.hand[3].getRank());
                break;
            }
        }


        if (checkIfWon()) {

        }
    }

    public boolean checkIfWon(){
        //TODO make this better?
        return this.hand[0].getRank() == this.hand[1].getRank() && this.hand[1].getRank() == this.hand[2].getRank() && this.hand[2].getRank() == this.hand[3].getRank();
    }

    public void run(){
        running.set(true);
        while (this.running.get()){    //loops until interrupted

            this.takeTurn();
            this.endTurn();
        }

        System.out.println("player " + Integer.toString(this.playerID) + " yippee");
    }
}
