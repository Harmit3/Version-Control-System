import java.util.*;

public class Gamenew {

    public void calculateScores(List<Integer> scores) {  
        int totalScore = 0;
        int bonus = 0;                                     
        System.out.println("Starting score calculation..."); 

        for(int score : scores) {                        
            System.out.println("Processing score: " + 
            score); 
            totalScore += score;
            if(score > 15) {                               
                bonus += 5;
                System.out.println("Bonus points awarded!");
            }
        }

        totalScore += bonus;                                 
        if(totalScore > 50) {                                
            System.out.println("Player achieved high score!");
        }

        System.out.println("Final score: " + 
        totalScore);   
        System.out.println("Game session ended!");          
    }

    public void announceWinner(String player) {           
        System.out.println("Winner is: " + 
        player);
    }

    public static void main(String[] args) {
        List<Integer> scores = Arrays.asList(10, 20, 25);
        Gamenew gameSession = new Gamenew();          
        gameSession.calculateScores(scores);
        gameSession.announceWinner("Player1");              
    }
}
