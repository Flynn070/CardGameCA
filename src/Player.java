import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Player implements Runnable {

    private int playerNum;
    private Card[] hand = new Card[4];

    //Getter and Setter methods
    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
    public int getPlayerNum() {
        return playerNum;
    }

    public boolean takeTurn(Deck playerDeck, Deck nextDeck){
        //drawing and discarding oldest card that isn't players preferred denomination
        for (int i = 0; i < 4; i++) {
            if (this.hand[i].getRank() != playerNum) {
                nextDeck.discard(this.hand[i]);
                //pushes the cards back so they retain age order
                for (int j = i; j < 3; j++){
                    this.hand[j] = this.hand[j+1];
                }
                this.hand[3] = nextDeck.draw();
                break;
            }
        }

        return checkIfWon();

    }

    public boolean checkIfWon(){
        if (this.hand[0].getRank()==this.hand[1].getRank() && this.hand[1].getRank()==this.hand[2].getRank() && this.hand[2].getRank()==this.hand[3].getRank()){
            return true;
        }
        return false;
    }

    public void run(){

    }
}
