package AIGame;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Console {

    private Board board;
    private Scanner sc = new Scanner(System.in);

    /**
     * Construct Console.
     */
    private Console(Board.State[][] myboard ,int boardWidth,Board.State playerSymbol,boolean playerFirst) {

        board = new Board(playerSymbol,playerFirst,boardWidth,myboard);


    }

    /**
     * Begin the game.
     */
    private void play () {

        System.out.println("Starting a new game.");

        while (true) {
            printGameStatus();

            playMove();

            if (board.isGameOver()) {
                printWinner();
                break;
            }
        }
    }

    /**
     * Handle the move to be played, either by the player or the AI.
     */
    private void playMove () {

        if (board.getTurn() == board.getPlayerSymbol()) {
            getPlayerMove();
        } else {

            if(board.remainMoves()>Parameters.TH2){
                board.moveByWeight();
            }
            else{
                AlphaBetaAdvanced.run(board.getTurn(), board, Double.POSITIVE_INFINITY);
            }
        }
    }


    /**
     * Print out the board and the player who's turn it is.
     */
    private void printGameStatus () {
        //board.print();
        System.out.println("\n" + board + "\n");
        System.out.println(board.getTurn().name() + "'s turn.");
    }

    /**
     * For reading in and interpreting the move that the user types into the console.
     */
    private void getPlayerMove () {
        System.out.print("Index of move: ");


        int inrow = sc.nextInt() - 1;
        int incol = sc.nextInt() - 1;

        if (inrow < 0 || incol<0 || inrow > Board.BOARD_WIDTH|| incol> Board.BOARD_WIDTH) {
            System.out.println("\nInvalid move.");
            System.out.println("\nThe index of the move must be between 0 and "
                    + (Board.BOARD_WIDTH * Board.BOARD_WIDTH - 1) + ", inclusive.");
        } else if (!board.move(inrow*Board.BOARD_WIDTH+incol)) {
            System.out.println("\nInvalid move.");
            System.out.println("\nThe selected index must be blank.");
        }
    }

    /**
     * Print out the winner of the game.
     */
    private void printWinner () {
        Board.State winner = board.getWinner();

        System.out.println("\n" + board + "\n");

        if (winner == Board.State.Blank) {
            System.out.println("It is a Draw.");
        } else {
            if(winner==board.getPlayerSymbol()) System.out.println("You win!");
            else System.out.println("AI wins!");

        }
    }


    public static void main(String[] args) {
        System.out.println("Enter a file or randomly generate:");
        Scanner scanner = new Scanner(System.in);
        FileReader fr = null;
        int boardWidth = 5;
        Board.State playerSymbol=Board.State.R;
        boolean playerFirst=true;
        Board.State[][] myboard= null;//new Board.State[boardWidth][boardWidth];

        String inputFilename = scanner.next();

        try {
            fr = new FileReader(inputFilename);

        BufferedReader br = new BufferedReader(fr);
        int linecount =0;
        while (br.ready()) {
            if(linecount==0){
                boardWidth=Integer.parseInt(br.readLine().trim());
                myboard = new Board.State[boardWidth][boardWidth];
            }
            else {
                String[] symbol = br.readLine().split(" ");
                for(int i=0;i<boardWidth;i++){
                    myboard[linecount-1][i]=(symbol[i].equalsIgnoreCase("B"))?Board.State.B:Board.State.R;

                }
                //System.out.println(br.readLine());
            }
            linecount++;
        }
        fr.close();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Generate a random board...");
        }
        if(myboard==null){
            while(boardWidth%2!=0) {
                System.out.println("Please enter board size (must be even):");
                boardWidth = scanner.nextInt();
            }
        }

        System.out.println("Please choose B or R(default=R):");
        //scanner = new Scanner(System.in);
        char symbol=scanner.next().charAt(0);
        if(symbol=='B')playerSymbol=Board.State.B;



        System.out.println("Do you want to play first? (1=yes,0=no,default=1)");
        //scanner = new Scanner(System.in);
        playerFirst= (scanner.nextInt()==1)?true:false ;

        Console aiGame = new Console(myboard,boardWidth,playerSymbol,playerFirst);
        aiGame.play();
    }

}
