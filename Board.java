package ColonelBySS.ICS3U;

import java.util.ArrayList;

/**
 * The Board class handles all components related to the Mastermind game board. At the beginning of each round of Mastermind, a new Board object is generated to handle the succession of moves and other information for that specific round.
 * The Board class completes the tasks of generating the answer code, setting the number of permitted guesses, setting the code length, checking the user's guesses, computing the user's score, and storing and printing the complete game board.
 */
public class Board {
    private final int numGuesses; //number of guesses permitted
    private int codeLength; //the length of the answer (depending on the selected game mode)
    private String answer = ""; //the String that holds the randomly generated answer
    /*currentBoard is a 2-dimensional array with currentBoard[0] representing the array of guesses and currentBoard[1] representing the array of results (i.e. x, o, _)
    * The second [] is left empty until numGuesses is defined - then, each of the two arrays takes on numGuesses as its length, since currentBoard will need to hold data relating to each of the user's guesses*/
    private final String[][] currentBoard = new String[2][];

    /**
     * The Board constructor method defines the value of numGuesses and codeLength based on the selected game mode, then generates an answer code that meets the criteria for that game mode (see comments below).
     * @param mode - either EASY, HARD, or EXPERT is chosen, which alters the criteria for a valid answer code
     */
    public Board (String mode) {
        if (mode.equals("EASY")) {
            numGuesses = 10;
        }
        else if (mode.equals("HARD")) {
            numGuesses = 12;
        }
        else { //since game mode choices have been filtered, else is the choice of "EXPERT" mode
            numGuesses = 15;
        }
        //Creating enough spaces in the currentBoard to hold all of the user's guesses and subsequent results
        currentBoard[0] = new String[numGuesses];
        currentBoard[1] = new String[numGuesses];
        codeLength = 4; //EASY and HARD both have a codeLength of 4. EXPERT has a codeLength in the range {5-8}, which is equivalent to 4 + {1-4}, so the initial codeLength can be set to 4 regardless of the game mode choice.
        /*In the EASY game mode, duplicate colours are NOT permitted in the answer.
        So, a separate ArrayList is used to temporarily hold all colour options. Then, after a colour is selected to be part of the answer, it is removed from the ArrayList to avoid duplicates.*/
        if (mode.equals("EASY")) {
            ArrayList<String> coloursRemovable = new ArrayList<>();
            for (int i=0; i<8; i++) { //filling in the ArrayList with the list of COLOURS from the Main class
                coloursRemovable.add(Mastermind.COLOURS[i]);
            }
            int randNum;
            for (int i=0; i<codeLength; i++) {
                randNum = (int) Math.floor(Math.random()*coloursRemovable.size()); //randomly select the index of a colour from the ArrayList of colours that have yet to be selected (hence the use of the .size() method)
                answer += coloursRemovable.get(randNum); //add the colour corresponding to the index to the answer
                coloursRemovable.remove(randNum); //remove the selected colour from the ArrayList to force non-duplicate colour generation
            }
        }
        /*In both the HARD and EXPERT game modes, duplicate colours ARE permitted, so they may generate colours in the same way (looped according to the codeLength).
        * For the EXPERT game mode, the codeLength must first be decided.*/
        else {
            if (mode.equals("EXPERT")) {
                codeLength += Math.floor(Math.random()*4+1); //in order to obtain a length of 5-8, add a random number from 1-4 onto the initial length of 4
                System.out.println(Mastermind.BOLDINVERT + "CODE LENGTH: " + codeLength + Mastermind.RESET); //the user must be told what this length is in order to guess the code
            }
            for (int i=0; i<codeLength; i++) { //generate the answer code by randomly selecting any COLOUR each time
                answer += Mastermind.COLOURS[(int) Math.floor(Math.random()*8)];
            }
        }
        //Please uncomment this line to see the answer: System.out.println(answer);
    }

    public int getNumGuesses() { //method to give the Main class access to the value of the private field numGuesses
        return numGuesses;
    }

    public int getCodeLength() { //method to give the Main class access to the value of the private field codeLength
        return codeLength;
    }

    public String getAnswer() { //method to give the Main class access to the value of the private field answer in colour (for easier printing in the Main class)
        String colourAnswer = ""; //to store the coloured version of the answer
        for (int i=0; i<codeLength; i++) {
            colourAnswer += Mastermind.colourConversion.get(answer.substring(i,i+1)) + answer.charAt(i) + Mastermind.RESET + " ";
        }
        return colourAnswer.substring(0,colourAnswer.length()-1); //remove extra space at the end
    }

    /**
     * Compares the user's guess against the answer, outputting x's for perfect matches (same position and letter), o's for partial matches (different position but same letter), and _'s for no matches
     * @param roundNum - the current round number, which is needed for proper storage in the currentBoard 2D array
     * @param guess - the user's valid guess
     * @return whether or not the game was won with the provided guess
     */
    public boolean check(int roundNum, String guess) {
        String correctAnswer = answer; //temporary String that stores the correct answer and can be modified freely (see comments below)
        //x and o store x's and o's as they are identified
        String x = "";
        String o = "";
        currentBoard[0][roundNum-1] = guess; //add the guess to the correct place in the 2D array
        //Perfect matches are handled first. Once such a match is found, add an "x" and clear the letters to avoid duplicate counting
        for (int i=0; i<codeLength; i++) {
            if (guess.charAt(i)==correctAnswer.charAt(i)) { //perfect match
                x += "x";
                //spaces will be used as placeholders at positions where the letter has already been counted
                guess = guess.substring(0,i) + " " + guess.substring(i+1);
                correctAnswer = correctAnswer.substring(0,i) + " " + correctAnswer.substring(i+1);
            }
        }
        //Next, nested for loops are used to search for partial matches in the remaining letters (i.e. those that haven't been replaced by " ")
        for (int i=0; i<codeLength; i++) {
            //If the guess at character i is a space, then the if statement below will be false and this iteration of the loop will do nothing. Otherwise, if it is not a space, further checks are made:
            if (guess.charAt(i)!= ' ') {
                for (int j=0; j<codeLength; j++) {
                    if (guess.charAt(i)==correctAnswer.charAt(j)) { //if the current letter of the guess (at i) exists at some other place in the correct answer (at j), add an "o" and clear the letters
                        o += "o";
                        guess = guess.substring(0,i) + " " + guess.substring(i+1); //again, spaces are used as placeholders
                        correctAnswer = correctAnswer.substring(0,j) + " " + correctAnswer.substring(j+1);
                        break; //once a partial match is found, no need to keep looping (risk of counting spaces as matching otherwise)
                    }
                }
            }
        }
        //Store the result of the guess in the 2D array by adding in x and o, then filling in the rest with _ for each entirely incorrect letter
        currentBoard[1][roundNum-1] = x+o;
        for (int i=currentBoard[1][roundNum-1].length(); i<codeLength; i++) {
            currentBoard[1][roundNum-1] += "_";
        }
        return x.length() == codeLength; //if the result of the guess is composed of only x's, the guess must have been fully correct; return true because the game has been won
    }

    /**
     * The Main class calls this method when a game has either been won or lost to determine the score of the Board based on the score formula. In the Main class, the returned value will be added onto the current score.
     * @param roundNum - the number of rounds taken to win (or roundNum = numGuesses if the game has been lost)
     * @return the calculated score of the Board
     */
    public int getScore(int roundNum) {
        return (int) ((numGuesses-roundNum)*numGuesses + Math.round((double) getNumXO('x', roundNum)/roundNum*2 + (double) getNumXO('o', roundNum)/roundNum));
    }

    /**
     * Used by the getScore method to determine the number of x's or o's earned by the user over the course of their guesses (i.e. across the entirety of currentBoard[1])
     * @param c - either x or o
     * @param roundNum - how many elements of currentBoard[1] must be included in the tally
     * @return the number of either x's or o's
     */
    private int getNumXO(char c, int roundNum) {
        int num = 0;
        for (int i=0; i<roundNum; i++) {
            for (int j=0; j<codeLength; j++) {
                if (currentBoard[1][i].charAt(j) == c) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Prints all values in the board (in colour!) up until the current guess to show the succession of moves.
     * @param numRounds - the number of guesses made up until that point
     */
    public void print(int numRounds) {
        String currentLetter;
        System.out.println();
        System.out.println(Mastermind.BOLDINVERT + "CURRENT BOARD" + Mastermind.RESET); //board header
        for (int i=numRounds; i>0; i--) { //print the board from most to least recent
            if (numRounds>=10 && i<=9) { //once the round number has reached 10, an additional space will be needed to maintain the formatting of the single digit numbers (i.e. 1-9)
                System.out.print(" ");
            }
            System.out.print(i + "| "); //prints the guess number
            for (int j=0; j<codeLength; j++) { //for every letter in each guess, print it in colour (String currentLetter used for cleanness)
                currentLetter = currentBoard[0][i-1].substring(j, j+1);
                System.out.print(Mastermind.colourConversion.get(currentLetter) + currentLetter + Mastermind.RESET + " ");
            }
            System.out.println("| " + currentBoard[1][i-1]); //then, print the result of that guess
        }
        System.out.println();
    }
}
