import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.FileWriter;

public class Player implements Runnable {

    private final int playerID;
    private volatile Card[] hand;
    private Deck drawDeck;                                              //deck with id matching the player
    private Deck discardDeck;                                           //deck of the next player (can wrap around if this is the last player)
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean won = new AtomicBoolean(false);
    private Thread playerThread;
    private CyclicBarrier barrier;
    private FileWriter playerOutputFile;

    public Player(int playerNum, Card[] hand, CyclicBarrier _barrier) {
        this.playerID = playerNum;
        this.hand = hand;
        this.barrier = _barrier;
        playerThread = new Thread(this);
        String playerOutputFilePath = "player" + this.playerID + "_output.txt";    //builds name of file to output player to
        try {
            this.playerOutputFile = new FileWriter(playerOutputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Getter and Setter methods ----------------------------------------------------------------------------------------
    public int getID() {return playerID;}

    public boolean hasWon() {return won.get();}

    public void setDrawDeck(Deck _drawDeck) {this.drawDeck = _drawDeck;}

    public void setDiscardDeck(Deck _discardDeck) {this.discardDeck = _discardDeck;}

    //Thread methods ---------------------------------------------------------------------------------------------------

    public void start() {
        playerThread.start();
    }

    public void stop() {
        try {
            playerOutputFile.close();
        } catch (IOException e) {
            System.out.println("Error while closing file");
        }
        running.set(false);
    }

    private boolean checkIfWon(){
        return this.hand[0].getRank() == this.hand[1].getRank() && this.hand[1].getRank() == this.hand[2].getRank() && this.hand[2].getRank() == this.hand[3].getRank();
    }

    //called by the main class if another player wins the game before this one, which will write the relevant
    public void otherPlayerWon(int winnerID) {
        try {
            this.playerOutputFile.write("Player " + this.playerID + " is informed Player " + winnerID + " won.\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        this.stop();
    }

    private void takeTurn(){
        //dealing with a player win
        if (checkIfWon()) {
            won.set(true);
            System.out.println("Player " + this.playerID + " won.");
            try {
                this.playerOutputFile.write("Player " + this.playerID + " won." + "\n");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            int numOtherPlayers = barrier.getParties() - 1;
            while (barrier.getNumberWaiting() != numOtherPlayers){      //waits until all other players are waiting at the barrier
                continue;
            }
            barrier.reset();
            this.stop();
        }

        //taking a turn - drawing a card and discarding the oldest card that isn't players preferred denomination
        for (int i = 0; i < 4; i++) {
            if (this.hand[i].getRank() != playerID) {                   //loops through hand until a non-preferred card is found
                this.discardDeck.discard(this.hand[i]);
                try {
                    this.playerOutputFile.write("Player " + this.playerID + " discards a " + this.hand[i].getRank() + " to deck " + this.discardDeck.getDeckID() + "\n");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                for (int j = i; j < 3; j++){                            //shifts the cards back in the hand so they retain age order
                    this.hand[j] = this.hand[j+1];
                }
                this.hand[3] = this.drawDeck.draw();
                try {
                    this.playerOutputFile.write("Player " + this.playerID + " draws a " + this.hand[3].getRank() + " from deck " + this.drawDeck.getDeckID() + "\n");
                    this.playerOutputFile.write("Player " + this.playerID + " current hand is " + this.hand[0].getRank() + ", " + this.hand[1].getRank() + ", " + this.hand[2].getRank() + ", " + this.hand[3].getRank() + "\n");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    private synchronized void endTurn() {
        try {
            barrier.await();                                                //when turn finished, thread should wait at the barrier
        } catch (InterruptedException e) {
            System.out.println("Thread " + this.playerID + " interrupted upon ending turn");
        } catch (BrokenBarrierException e) {                                //if the barrier is broken/reset, the thread needs to terminate
            running.set(false);
        }
    }

    public void run(){
        running.set(true);
        while (this.running.get()){    //loops until thread is told to stop
            if (!this.won.get()) {
                this.takeTurn();
            }
            this.endTurn();             //endTurn handles if player has won
        }
    }
}
