import java.util.Scanner;

import static java.lang.Math.ceil;
import static oracle.jrockit.jfr.events.Bits.intValue;

public class AIGame {
    private char[] symbol = {'B', 'R'};
    private boolean playerSymbol = true; //true:R false:B
    //private boolean playerFirst = true;
    private int boardSize = 4;
    private char[][] gameBoard;
    private boolean playerToMove = true;//true: player's turn
    private boolean isEnd = false;

    private double weighted=0.4;
    /*private double W_RM_SELF = 1;
    private double W_RM_OPP = -1.2;
    private double W_HELP_SELF = 0.5;
    private double W_HELP_OPP = -0.6;*/

    public AIGame(boolean playerSymbol, boolean playerFirst, int n,char[][] inboard) {
        this.playerSymbol = playerSymbol;
        //this.playerFirst = playerFirst;
        playerToMove=playerFirst;
        this.boardSize = n;

        if(inboard==null) {
            int[] randomInitial = Utilities.randomCommon(0, boardSize * boardSize, boardSize * boardSize / 2);

            gameBoard = new char[boardSize][boardSize];
            for (int row = 0; row < boardSize; row++)
                for (int col = 0; col < boardSize; col++)
                    gameBoard[row][col] = symbol[0];


            for (int r : randomInitial) {
                int row = intValue(ceil(r / boardSize));
                int col = r % boardSize;
                gameBoard[row][col] = symbol[1];
            }
        }
        else{
            gameBoard=inboard;
        }

    }

    public void go() {
        System.out.println("Hello!");
        showBoard();
        while (!isEnd) {


            //player's move
            if (playerToMove) {

                int inrow, incol;
                System.out.println("It's your turn! Please remove " + symbol[playerSymbol ? 1 : 0]);
                System.out.println("input col row(separate by space) ex: 2 3");

                try {
                    Scanner scanner = new Scanner(System.in);

                    inrow = scanner.nextInt() - 1;
                    incol = scanner.nextInt() - 1;

                    //check symbol
                    if (gameBoard[inrow][incol] != symbol[playerSymbol ? 1 : 0]) {
                        System.out.println("Please choose " + symbol[playerSymbol ? 1 : 0] + " to remove.");
                        continue;
                    }

                    gameBoard[inrow][incol] = ' ';
                    System.out.println("After your move");

                    //side effect
                    checkSideEffect(inrow, incol);

                } catch (Exception e) {
                    System.out.println("Wrong input format, please input again");
                }
            } else {// AI's move
                System.out.println("AI's move....");
                int[] nextMove = AIStrategy();
                //side effect
                System.out.println("AI remove " + (nextMove[0] + 1) + "," + (nextMove[1] + 1));
                gameBoard[nextMove[0]][nextMove[1]] = ' ';
                showBoard();

                checkSideEffect(nextMove[0], nextMove[1]);

            }
        }


    }


    private void showBoard() {
        for (int row = 0; row < boardSize; row++) {
            for(int n=0;n<boardSize;n++) {
                System.out.print("----");
            }
            System.out.println();
            System.out.print("|");
            for (int col = 0; col < boardSize; col++) {
                System.out.print(gameBoard[row][col] + " | ");
            }
            System.out.println();

        }
        for(int n=0;n<boardSize;n++) {
            System.out.print("----");
        }
        System.out.println();

    }

    private double getScore(int row, int col) {

        int selfCount =0;
        int oppCount=0;
        int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};

        for (int[] rc : rcList) {
            if (checkRange(rc[0],rc[1])) {
                if (gameBoard[rc[0]][rc[1]] == symbol[playerSymbol ? 0 : 1]) selfCount++;
                else if (gameBoard[rc[0]][rc[1]] == symbol[playerSymbol ? 1 : 0]) oppCount++;
            }
        }
        double score = selfCount*weighted+(weighted-1)*oppCount;
        return score;
    }

    private int[] AIStrategy() {
        int[] nextMove = new int[2];
        // level=easy, always remove the first one
        /*for (int row = 0; row < boardSize; row++)
            for (int col = 0; col < boardSize; col++)
                if (gameBoard[row][col] == symbol[playerSymbol ? 0 : 1]) {
                    nextMove = new int[]{row, col};
                    return nextMove;
                }

          isEnd = true;
        */
        double highScore = -4;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] == symbol[playerSymbol ? 0 : 1]) {
                    double currentScore = getScore(row, col);
                    if (currentScore > highScore) {
                        highScore = currentScore;
                        nextMove[0] = row;
                        nextMove[1] = col;
                    }
                }
            }
        }

        return nextMove;
    }


    private void checkSideEffect(int row, int col) {
        int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};
        for (int[] rc : rcList) {
            removeLonely(rc[0], rc[1]);
        }

        System.out.println("After side effect");
        showBoard();
        checkEnd();

        playerToMove = !playerToMove;

    }
    private void checkEnd(){
        int count[] = {0, 0};
        for (int row = 0; row < boardSize; row++)
            for (int col = 0; col < boardSize; col++)
                for (int s = 0; s < 2; s++)
                    if (gameBoard[row][col] == symbol[s]) count[s]++;
        if (count[0] == 0 ||count[1]==0){
            isEnd = true;
        }

        if(count[0]==0 && count[1]==0) System.out.println("It's a draw!!");
        else if (count[playerSymbol ? 1 : 0] == 0) System.out.println("You win!!");
        else if (count[playerSymbol ? 0 : 1] == 0) System.out.println("AI win!!");

    }

    private boolean checkRange(int r, int c) {
        if (r >= 0 && r < boardSize && c >= 0 && c < boardSize) return true;
        else return false;
    }

    private void removeLonely(int row, int col) {
        if (checkRange(row, col)) {
            int count = 0;
            int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};
            for (int[] rc : rcList)
                if (checkRange(rc[0], rc[1])) {
                    if (gameBoard[rc[0]][rc[1]] == ' ') count++;
                }

            if (count >= 2) gameBoard[row][col] = ' ';
        }
    }


}
