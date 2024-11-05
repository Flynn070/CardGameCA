
public class Player implements Runnable {

    private final int playerID;
    private volatile Card[] hand;

    //TODO ? hand in constructor seems cleaner?
    public Player(int playerNum, Card[] hand) {
        this.playerID = playerNum;
        this.hand = hand;
    }
    //Getter and Setter methods
    public int getPlayerNum() {
        return playerID;
    }

    //TODO The combination of a card draw, and a discard should be treated as a single atomic action. - from the spec
    public boolean takeTurn(Deck playerDeck, Deck nextDeck){
        //drawing and discarding oldest card that isn't players preferred denomination
        for (int i = 0; i < 4; i++) {
            if (this.hand[i].getRank() != playerID) {
                nextDeck.discard(this.hand[i]);
                System.out.println(String.format("Player %d discards a %d to deck %d", this.playerID, this.hand[i].getRank(), nextDeck.getDeckID()));
                //pushes the cards back so they retain age order
                for (int j = i; j < 3; j++){
                    this.hand[j] = this.hand[j+1];
                }
                this.hand[3] = nextDeck.draw();
                System.out.println(String.format("Player %d draws a %d from deck %d", this.playerID, this.hand[3].getRank(), playerDeck.getDeckID()));
                System.out.println(String.format("Player %d current hand is %d %d %d %d", this.playerID, this.hand[0].getRank(), this.hand[1].getRank(), this.hand[2].getRank(), this.hand[3].getRank()));
                break;
            }
        }

        return checkIfWon();
    }

    public boolean checkIfWon(){
        //TODO make this better?
        return this.hand[0].getRank() == this.hand[1].getRank() && this.hand[1].getRank() == this.hand[2].getRank() && this.hand[2].getRank() == this.hand[3].getRank();
    }

    public void run(){

    }
}
