import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

public class Main {

    private static int checkIfValid(String numberInput) throws InvalidNumberException, NumberFormatException, NullPointerException {
        //Checks if a string can be cast to a positive integer
        int number = Integer.parseInt(numberInput); // Casts input to an integer.
        if (number <= 0) {
            throw new InvalidNumberException();
        }
        return number;
    }


    public static void main(String[] args) {
        final CyclicBarrier turnCoordinator;

        //initialisation --------------------------------------------------------------------------------
        Scanner usrInput = new Scanner(System.in);

        System.out.println("Please enter the number of players: ");
        boolean validNum = false;   //flag to exit while loop
        int numPlayers = 1; // 1 is a default value, will change based on user input
        while (!validNum) {
            String numPlayersInput = usrInput.nextLine(); // Reads the input from the terminal.
            try {
                numPlayers = checkIfValid(numPlayersInput); //validate input
                validNum = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid type of input for number of players. Please enter a integer greater than 0");
            } catch (InvalidNumberException e) {
                System.out.println("Integer cannot be less than or to 0 for number of players.");
            } catch (NullPointerException e) {
                System.out.println("Input was empty. Please input a valid integer.");
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }

        System.out.println("Please enter the path to the pack file.");
        boolean validFile = false;  //flag to exit while loop
        File packFile;
        Scanner fileReader;
        ArrayList<Card> pack = new ArrayList<Card>();
        while (!validFile) {
            String filePath = usrInput.nextLine();
            pack.clear();
            try {
                packFile = new File(filePath);
                fileReader = new Scanner(packFile);
                if (filePath.length() < 4) {    //checking file can be a text file
                    throw new IncorrectFileTypeException();
                }
                if (!(filePath.endsWith(".txt"))) {
                    throw new IncorrectFileTypeException();
                }

                int numLines = 0;
                while (fileReader.hasNextLine()) { //reads each file line
                    String fileLine = fileReader.nextLine();
                    numLines++;
                    pack.add(new Card(checkIfValid(fileLine)));  //if line is a valid integer, make a card with that value and add to pack
                }

                if (numLines / numPlayers != 8) {
                    // if the number of lines in the file is not 8 * the number of players, the file should be considered invalid.
                    throw new InvalidFileLengthException();
                }

                validFile = true;
            } catch (FileNotFoundException e) {
                System.out.println("Cannot find file using given path. Please enter a valid path.");
            } catch (NumberFormatException e) {
                System.out.println("Type of data in file is invalid. Each line must contain an integer.");
            } catch (IncorrectFileTypeException e) {
                System.out.println("Pack file should be a .txt file. Please enter a valid .txt file path.");
                e.printStackTrace(System.out);
            } catch (InvalidNumberException e) {
                System.out.println("Each line must have a positive integer (> 0).");
            } catch (InvalidFileLengthException e) {
                System.out.println("The number of lines in the file, must be 8 times the number of players.");
            }
        }

        // used to make the distribution of starting hands from the pack less deterministic, using the current time as the seed.
        Random rand = new Random(System.currentTimeMillis());

        // Holds the pointers to all the deck and player objects in the game.
        Deck[] decks = new Deck[numPlayers];
        Player[] players = new Player[numPlayers];

        turnCoordinator = new CyclicBarrier(numPlayers);
        List<Thread> playerThreads = new ArrayList<Thread>(numPlayers);

        //Putting cards into each players hand
        for (int currPlayerNum = 1; currPlayerNum <= numPlayers; currPlayerNum++) {
            Card[] startingHand = new Card[4];
            for (int handPos = 0; handPos <= 3; handPos++) {    //hands are of 4 cards
                int randLnNum = rand.nextInt(pack.size());  //picks index of random card from pack
                startingHand[handPos] = pack.get(randLnNum); //sets value of current hand position to the randomly picked card
                pack.remove(randLnNum); //removes chosen card from pack
            }
            players[currPlayerNum - 1] = new Player(currPlayerNum, startingHand, turnCoordinator);  //instantiates the player with created hand
            playerThreads.add(players[(currPlayerNum - 1)].getPlayerThread());
        }

        //Putting cards into each deck
        for (int currDeckNum = 1; currDeckNum <= numPlayers; currDeckNum++) {
            Queue<Card> startingDeck = new LinkedList<Card>();
            for (int deckPos = 0; deckPos <= 3; deckPos++) {   //decks will always have 4 cards, regardless of number of players
                int randLnNum = rand.nextInt(pack.size());  //picks random card from pack
                startingDeck.add(pack.get(randLnNum));
                pack.remove(randLnNum);
            }
            decks[currDeckNum - 1] = new Deck(startingDeck, currDeckNum);
        }

        // initialise the player objects, and start their threads.
        for (Player currPlayer : players) {
            currPlayer.setDrawDeck(decks[currPlayer.getID() - 1]);  //assigns deck for player to draw from
            if (currPlayer.getID() != numPlayers) {
                currPlayer.setDiscardDeck(decks[currPlayer.getID()]);   //assigns next deck for player to discard to, unless final player
            } else {
                currPlayer.setDiscardDeck(decks[0]);    //assigns final player the first deck to discard to
            }

            currPlayer.start();

        }

        //main program loop ----------------------------------------------------------------------------------

        boolean gameWon = false;
        while (!gameWon) {                                              //loop until game won
            for (Player currPlayer : players) {                         //each loop, check every player
                System.out.println("Deck " + Arrays.toString(decks[currPlayer.getID() - 1].getDeck().toArray()));
                if (currPlayer.hasWon()) {                              //check if each player has won
                    gameWon = true;
                    System.out.println("A player has won");
                    for (Player loser : players) {                      //for every player
                        if (!(loser.getID() == currPlayer.getID())){    //if the player is not the player that has won, inform it that another player has won
                            loser.otherPlayerWon(currPlayer.getID());
                        }
                    }
                    currPlayer.stop();
                    break;
                }
            }
        }

        //Ending game messages -------------------------------------------------------------------------------
        System.out.println(Arrays.toString(decks));
        System.out.println(Arrays.toString(players));

        for (Deck currDeck : decks) {
            for (Card currCard : currDeck.getDeck()) {
                System.out.println(currCard.getRank());
            }

        }

        System.out.println("yippee");

    }
}