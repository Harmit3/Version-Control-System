import java.util.*;

public class Game {

    public void calculateScores(List<Integer> scores) {
        int totalScore = 0;
        System.out.println("Calculating game scores...");
        for(int score : scores) {
            System.out.println("Score: " + score);
            totalScore += score;
        }
        if(totalScore > 50) {
            System.out.println("Player did great!");
        }
        System.out.println("Total score: " + totalScore);
        System.out.println("Game finished!");
    }

    public static void main(String[] args) {
        List<Integer> scores = Arrays.asList(10, 20, 25);
        Game game = new Game();
        game.calculateScores(scores);
    }
}
