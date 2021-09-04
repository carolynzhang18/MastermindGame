package ColonelBySS.ICS3U;

/*
Carolyn Zhang
Mrs. Kutschke
May 8th, 2021
*/

import java.util.HashMap;
import java.util.Scanner;

public class Mastermind {
    private static Scanner s = new Scanner (System.in);
    //final Strings that contain the ANSI colours codes
    public static final String[] COLOURLIST = {"\u001b[40m", "\u001b[41m", "\u001b[42m\u001b[30m", "\u001b[0m\u001b[43m", "\u001b[44m", "\u001b[45m", "\u001b[46m", "\u001b[47m\u001b[30m"};
    public static final String RESET = "\u001b[0m";
    public static final String BOLDINVERT = "\u001b[1m\u001b[7m";
    //String array to store colour letters; will be combined with the colours codes in COLOURLIST to create a HashMap (key is letter, value is code)
    public static final String[] COLOURS = {"B", "R", "G", "Y", "I", "P", "C", "W"};
    public static HashMap<String, String> colourConversion = new HashMap<>();
    //variables to store the user's profile: name, score
    private static String userName;
    private static int score = 0;

    /**
     * The main method sets up the Mastermind game, contains loops to allow the user to play multiple rounds and games, and concludes the game
     */
    public static void main (String[] args) {
        setColourCodes(); //set up coloursConversion HashMap
        welcome(); //method to welcome the user
        boolean newGame, newRound;
        String gameMode;
        //The nested do-while loops allow for multiple rounds to be played within multiple games of Mastermind (do-while used because at least 1 iteration will occur)
        do {
            gameMode = setupGameMode(); //method to get the user's choice of a (valid) game mode
            do {
                startRound(gameMode); //method to start a new round of the selected game mode
                System.out.print("\nWould you like to play another round in the " + gameMode + " game mode? Enter YES or NO: ");
                newRound = getContinuation(); //method to get a valid YES/NO input from the user
                System.out.println();
            }
            while (newRound);
            System.out.print("Would you like to play another game? If you enter YES, you will be able to try out a different game mode. If you enter NO, the Mastermind game will end. Enter YES or NO: ");
            newGame = getContinuation();
        }
        while (newGame);
        goodbye(); //method to say goodbye to the user
    }

    /**
     * Sets up the public static HashMap colourConversion (key letter, value colour code), which will be used throughout both the Main and Board classes
     */
    private static void setColourCodes() {
        for (int i=0; i<8; i++) {
            colourConversion.put(COLOURS[i], COLOURLIST[i]);
        }
    }

    /**
     * Creates time and space between successive instruction outputs to improve readability and build a game environment
     * @param message - specific instruction to be delivered by the pressEnter method
     */
    private static void pressEnter(String message) {
        System.out.println("\n[PRESS ENTER " + message + "]");
        s.nextLine();
    }

    /**
     * Welcomes the user and asks for their name, then explains the game instructions
     */
    private static void welcome() {
        System.out.println(       BOLDINVERT + " • • • • • • • • • • • • • • " + RESET +
                           "\n" + BOLDINVERT + " •  Welcome to Mastermind! • " + RESET +
                           "\n" + BOLDINVERT + " • • • • • • • • • • • • • • " + RESET);
        System.out.print("\nPlease enter your name: ");
        userName = s.nextLine();
        System.out.println("Hi " + userName + "! Enjoy the game :)");
        pressEnter("FOR GAME INSTRUCTIONS");
        //basic game instructions
        System.out.println("Mastermind is a one player game where you, the player, are trying to crack the colour code generated by the computer. " +
                "\nIf you crack the code before you run out of guesses, then you win! Otherwise, if you run out of guesses, you lose :(");
        pressEnter("TO CONTINUE");
        System.out.println("At each turn, you will be prompted to enter your guess. For each guess, you will receive feedback from the computer as follows:\n" +
                "   • You will receive an x for a character that is of the correct colour AND position\n" +
                "   • You will receive an o for a character that is of the correct colour BUT of an incorrect position\n" +
                "   • You will receive a _ for a character that is entirely incorrect.\n" +
                "Note that the x's, o's, and _'s will always appear in this order, meaning that the order in which they appear does not necessarily match the order of the pins.");
        System.out.println("\nFor example, if the correct code is " + colourConversion.get("B") + "B" + colourConversion.get("R") + "R" + colourConversion.get("G") + "G" + colourConversion.get("Y") + "Y" + RESET + ", " +
                "and your guess is " + colourConversion.get("P") + "P" + colourConversion.get("R") + "R" + colourConversion.get("Y") + "Y" + colourConversion.get("C") + "C" + RESET + ", " +
                "then your feedback would be xo__ → x for " + colourConversion.get("R") + "R" + RESET + ", o for " + colourConversion.get("Y") + "Y" + RESET + ", and __ for " +
                colourConversion.get("P") + "P" + RESET + " and " + colourConversion.get("C") + "C" + RESET +".");
        pressEnter("TO CONTINUE");
        //specific instructions for this game: colour specifications
        System.out.println("In this game of Mastermind, the colours are: " + colourConversion.get("B") + " B (black) " + RESET + ", " + colourConversion.get("R") + " R (red) " + RESET + ", "
                + colourConversion.get("G") + " G (green) " + RESET + ", " + colourConversion.get("Y") + " Y (yellow) " + RESET + ", " + colourConversion.get("I") + " I (indigo) " + RESET + ", "
                + colourConversion.get("P") + " P (purple) " + RESET + ", " + colourConversion.get("C") + " C (cyan) " + RESET + ", " + colourConversion.get("W") + " W (white) " + RESET);
        System.out.println("To select a colour as part of your guess, enter the single uppercase letter that matches the colour. This game is case sensitive and all required input is in uppercase, so it may be a good idea to hit your \"Caps Lock\" button!");
        System.out.println("During a game, if you ever need a reminder of these colours and their corresponding letters, simply type \"COLOURS\" at the guess prompt, and a band showing all the colours will appear. " +
                "Afterwards, you will be prompted to get back to the game and continue guessing.");
        pressEnter("TO CONTINUE");
        //explanation about the score and the round & game format
        System.out.println("Your SCORE will be calculated based on the number of guesses you took, the difficulty of the game mode, and the number of x's and o's you earned throughout the round. Your score will continue increasing as you play and will only reset to 0 if you terminate the Mastermind program.");
        System.out.println("\nA ROUND of Mastermind consists of one colour code. At the end of a round, you will be asked if you would like to play a new round, with a new code." +
                "\nA GAME of Mastermind consists of one or more rounds of Mastermind. If you choose to end a GAME, you will have the option of trying a different game mode.");
        pressEnter("TO VIEW THE GAME MODES");
        //game mode description
        System.out.println("There are 3 game modes in this game of Mastermind: EASY, HARD, and EXPERT." +
                "\nIn the EASY game mode, the 4 characters in the colour code will be unique (i.e. no colour appears more than once in the code). You will have 10 guesses." +
                "\nIn the HARD game mode, there may be duplicate colours in the 4 character code. You will have 12 guesses." +
                "\nIn the EXPERT game mode, the code will be anywhere from 5 to 8 characters (you will be told the length), and there may be duplicate colours. You will have 15 guesses.");
        pressEnter("TO CONTINUE");
    }

    /**
     * Prompts the user to choose a game mode and loops to prevent invalid entries
     * @return the user's choice of a valid game mode
     */
    private static String setupGameMode() {
        System.out.print("Please select a game mode - enter EASY, HARD, or EXPERT: ");
        String gameMode_in = s.nextLine();
        while (!(gameMode_in.equals("EASY") || gameMode_in.equals("HARD") || gameMode_in.equals("EXPERT"))) { //while "EASY", "HARD", or "EXPERT" have not been entered
            System.out.print("Invalid game mode choice. ");
            if (!gameMode_in.toUpperCase().equals(gameMode_in)) { //if the user has inputted lowercase letters, remind them to use uppercase
                System.out.print("Please ensure your input is in uppercase throughout the game (it is a good idea to press your \"Caps Lock\" key!). ");
            }
            System.out.print("Enter EASY, HARD, or EXPERT: ");
            gameMode_in = s.nextLine();
        }
        System.out.println();
        return gameMode_in;
    }

    /**
     * Begins a new round of Mastermind by creating a new Board. Gives the user the correct number of tries to guess the code and determines whether or not they have won the game.
     * @param mode - the game mode selected by the user is used to create the correct Board
     */
    private static void startRound(String mode) {
        boolean won;
        Board round = new Board(mode); //new Board object created for each round
        for (int i=1; i<=round.getNumGuesses(); i++) { //access the number of guesses through the getNumGuesses instance method, then loop the correct number of times
            System.out.print("Enter your guess: ");
            won = round.check(i, getGuess(round.getCodeLength())); //instance method in the Board class that determines the result of the guess
            round.print(i); //instance method in the Board class that prints the succession of guesses and their results
            if (won) { //update the score from the round if the game has been won
                score += round.getScore(i);
                System.out.println(BOLDINVERT + "You got it! Your score is now " + score + " :)" + RESET);
                return;
            }
        }
        //otherwise, the game was lost, so output the correct answer and update the score
        String answer = round.getAnswer();
        score += round.getScore(round.getNumGuesses());
        System.out.println(BOLDINVERT + "You lose! The correct answer was" + RESET + " " + answer + " " + BOLDINVERT + "! Your score is now " + score + "." + RESET);
    }

    /**
     * Gets the user's guess and loops while an invalid entry has been made
     * @param correctLength - to verify whether or not the user has entered a guess of the correct length
     * @return the user's valid guess
     */
    private static String getGuess(int correctLength) {
        String guess_in = s.nextLine();
        String validity = getGuessValidity(guess_in, correctLength); //method to check the guess' validity and determine the type of error (or lack thereof)
        while (!validity.equals("good")) { //while the guess is not good
            switch (validity) { //based on the error feedback, output the corresponding message
                case "COLOURS":
                    for (int i = 0; i < 8; i++) {
                        System.out.print(colourConversion.get(COLOURS[i]) + COLOURS[i] + RESET + " ");
                    }
                    System.out.println();
                    break;
                case "wrongcase":
                    System.out.print("Invalid input. Please ensure your input is in uppercase throughout the game (it is a good idea to press your \"Caps Lock\" key!). ");
                    break;
                case "badlength":
                    System.out.print("Invalid input. Please ensure your input consists of exactly " + correctLength + " characters. ");
                    break;
                case "notacolour":
                    System.out.print("Invalid colour choice (if you need a reminder of the colours of the game, input COLOURS). ");
                    break;
            }
            System.out.print("Enter your guess: ");
            guess_in = s.nextLine();
            validity = getGuessValidity(guess_in, correctLength);
        }
        return guess_in;
    }

    /**
     * Takes in the user's unchecked guess and determines which kind of error has occurred, or returns "good" if no error has been detected
     * @param guess - the user's unchecked input
     * @param correctLength - the accepted length of the colour code
     * @return a String that contains feedback regarding the user's guess
     */
    private static String getGuessValidity(String guess, int correctLength) { //return customized message for getGuess method to output
        if (guess.equals("COLOURS")) { //if the user requests to see the colour reminder
            return "COLOURS";
        }
        if (!guess.toUpperCase().equals(guess)) { //if the guess is not entirely in uppercase
            return "wrongcase";
        }
        if (guess.length()!=correctLength) { //if the guess is not the correct number of characters long
            return "badlength";
        }
        //if none of the previous checks have been set off, go through each character to ensure a valid colour is being guessed at each place
        for (int i=0; i<correctLength; i++) {
            if (colourConversion.get(guess.substring(i,i+1))==null) { //if the guess is not a key in the HashMap colourConversion, then the entry was not a colour
                return "notacolour";
            }
        }
        return "good"; //if none of the previous checks have failed, the guess must have been valid
    }

    /**
     * Called at the end of each round and game to ask the user to enter YES or NO (error otherwise) to a question regarding whether or not they will continue.
     * @return true if the round/game should continue, false otherwise
     */
    private static boolean getContinuation() {
        String continuation_in = s.nextLine();
        while (true) { //continue looping until the input is either YES or NO, the only valid choices
            if (continuation_in.equals("YES")) {
                return true;
            }
            else if (continuation_in.equals("NO")) {
                return false;
            }
            else { //YES or NO were not entered
                System.out.print("Invalid input. Enter either YES or NO: ");
                continuation_in = s.nextLine();
            }
        }
    }

    /**
     * Prints a fun "goodbye" message when the user decides to end Mastermind
     */
    private static void goodbye() {
        System.out.println();
        String dotsMatchName = "";
        for (int i=0; i<userName.length(); i+=2) { //because the user is being addressed by name, the amount of the dots must adjust itself to match the length of their name
            dotsMatchName += " •";
        }
        System.out.println(BOLDINVERT +" • • • •" + dotsMatchName + " • • • • • • • • • • • • • • • • • • " + RESET);
        System.out.print(BOLDINVERT + " • ");
        if (userName.length()%2==1) {
            System.out.print(" "); //names with an odd number of characters need an extra space for alignment purposes
        }
        System.out.println("Bye " + userName +"! Thank you for playing Mastermind! • " + RESET);
        System.out.println(BOLDINVERT +" • • • •" + dotsMatchName + " • • • • • • • • • • • • • • • • • • " + RESET);
    }
}