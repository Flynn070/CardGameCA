public class Main {

    public static void main(String[] args) {
        Card card1 = new Card();
        Card card2 = new Card();
        card1.setRank(1);
        card2.setRank(1);
        if (card1.getRank() == card2.getRank()) {
            System.out.println("yippee");
        }
    }
}