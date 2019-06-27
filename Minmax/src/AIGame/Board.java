package AIGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Board {

    public static int BOARD_WIDTH = 4;

    public enum State {Blank, R, B}
    private State[][] board;
    private double[][] scoreBoard;

    private State playersTurn;
    private State playerSymbol;
    private boolean playerFirst;
    private State winner;
    private HashSet<Integer> movesAvailableR;
    private HashSet<Integer> movesAvailableB;




    private boolean gameOver;

    /**
     * Construct the board.
     */

    Board( State playerSymbol,boolean playerFirst, int n,State[][] inboard) {
        BOARD_WIDTH=n;

        this.playerFirst = playerFirst;
        this.playerSymbol=playerSymbol;
        playersTurn = playerFirst?playerSymbol:(playerSymbol==State.B?State.R:State.B);

        movesAvailableR = new HashSet<>();
        movesAvailableB = new HashSet<>();

        reset(inboard);
    }
    Board( int n) {
        BOARD_WIDTH=n;
        board = new State[BOARD_WIDTH][BOARD_WIDTH];
        scoreBoard = new double[BOARD_WIDTH][BOARD_WIDTH];
        movesAvailableR = new HashSet<>();
        movesAvailableB = new HashSet<>();

    }


    /**
     * Set the cells to be blank and load the available moves (all the moves are
     * available at the start of the game).
     */
    private void initialize (State[][] inboard) {
        movesAvailableR.clear();
        movesAvailableB.clear();
        scoreBoard = new double[BOARD_WIDTH][BOARD_WIDTH];
        if (inboard==null) {

            board = new State[BOARD_WIDTH][BOARD_WIDTH];

            int[] randomInitial = Utilities.randomCommon(0, BOARD_WIDTH * BOARD_WIDTH, BOARD_WIDTH * BOARD_WIDTH / 2);
            for (int row = 0; row < BOARD_WIDTH; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    board[row][col] = State.B;

                }
            }

            for (int k = 0; k < BOARD_WIDTH * BOARD_WIDTH; k++) {
                movesAvailableB.add(k);
            }

            for (int r : randomInitial) {
                Move mv=convertMove(r);
                board[mv.getRow()][mv.getCol()] = State.R;
                movesAvailableB.remove(r);
                movesAvailableR.add(r);
            }
        }
        else {
            board=inboard;
            for (int row = 0; row < BOARD_WIDTH; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    if(board[row][col]==State.B){
                        movesAvailableB.add(convertMove(row,col));
                    }
                    else if(board[row][col]==State.R){
                        movesAvailableR.add(convertMove(row,col));
                    }

                }
            }

        }
        /*board[0][0] =State.B;
        board[0][2] =State.B;
        board[1][1] =State.B;
        board[1][2] =State.B;
        board[1][3] =State.B;
        board[2][2] =State.B;
        board[3][0] =State.B;
        board[3][3] =State.B;
        movesAvailableB.add(0);
        movesAvailableB.add(2);
        movesAvailableB.add(5);
        movesAvailableB.add(6);
        movesAvailableB.add(7);
        movesAvailableB.add(10);
        movesAvailableB.add(15);
        movesAvailableB.add(12);
        board[0][1] =State.R;
        board[0][3] =State.R;
        board[1][0] =State.R;
        board[2][0] =State.R;
        board[2][1] =State.R;
        board[2][3] =State.R;
        board[3][1] =State.R;
        board[3][2] =State.R;
        movesAvailableR.add(1);
        movesAvailableR.add(3);
        movesAvailableR.add(4);
        movesAvailableR.add(8);
        movesAvailableR.add(9);
        movesAvailableR.add(11);
        movesAvailableR.add(13);
        movesAvailableR.add(14);

         */


    }

    /**
     * Restart the game with a new blank board.
     */
    void reset (State[][] inboard) {
        //moveCount = 0;
        gameOver = false;
        //playersTurn = State.X;
        winner = State.Blank;
        initialize(inboard);
    }

    /**
     * Remove R or B on the specified index depending on whose turn it is.
     * @param index     the position on the board (example: index 4 is location (0, 1))
     * @return          true if the move has not already been played
     */
    public boolean move (int index)
    {
        Move mv=convertMove(index);
        return move(mv.getRow(),mv.getCol());
    }

    /**
     * Remove R or B on the specified location depending on who turn it is.
     * @param x         the x coordinate of the location
     * @param y         the y coordinate of the location
     * @return          true if the move has not already been played
     */
    private boolean move (int x, int y) {

        if (gameOver) {
            throw new IllegalStateException("The game is over. No moves can be played.");
        }

        if (board[x][y] == playersTurn) {
            board[x][y] = State.Blank;
            if(playersTurn==State.B){
                movesAvailableB.remove(convertMove(x,y));
            }
            else{
                movesAvailableR.remove(convertMove(x,y));
            }
        } else {
            return false;
        }

        List<Integer> lonelyList = checkSideEffect(x, y);

        for(int idx:lonelyList){
            Move mv=convertMove(idx);
            int rIdx = mv.getRow();//idx/BOARD_WIDTH;
            int cIdx = mv.getCol();//idx%BOARD_WIDTH;

            if(board[rIdx][cIdx]==State.B){
                movesAvailableB.remove(idx);
            }
            else if(board[rIdx][cIdx]==State.R){
                movesAvailableR.remove(idx);
            }

            board[rIdx][cIdx]=State.Blank;
        }



        // Check for a winner.
        checkWinner();


        playersTurn = (playersTurn == State.R) ? State.B : State.R;
        return true;
    }
    private List<Integer>  checkNeighbor(int row,int col){
        List<Integer> neighborList=new ArrayList<>();
        int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};

        for (int[] rc : rcList) {
            if (checkRange(rc[0],rc[1])) {
                neighborList.add(convertMove(rc[0],rc[1]));
            }
        }
        return neighborList;
    }
    public void moveByWeight(){
        int[] sortMoves=new int[Parameters.TH_n];
        generateScore();
        if(playersTurn==State.B) {
            sortMoves = sortIdx(movesAvailableB);


        }
        else if(playersTurn==State.R) {
            sortMoves = sortIdx(movesAvailableR);

        }

        Move mv=convertMove(sortMoves[0]);
        move(mv.getRow(),mv.getCol());

        System.out.println("AI plays:"+(mv.getRow()+1)+" "+(mv.getCol()+1));

    }

    public void printScoreBoard(){
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                System.out.print(scoreBoard[row][col]+" ");
            }
            System.out.println();
        }
    }
    private void generateScore(){

        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {

                if(board[row][col]==this.playersTurn){
                    double score=0;
                    ArrayList<Integer> neighborList=new ArrayList<Integer>();

                    int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};
                    for (int[] rc : rcList) {
                        if(checkLonely(rc[0], rc[1])){
                            //side effect, add score
                            if(board[rc[0]][rc[1]]==playersTurn){
                                score+=Parameters.w_rm_self;
                            }
                            else if(board[rc[0]][rc[1]]==(playersTurn==State.R?State.B:State.R)){
                                score+=Parameters.w_rm_other;
                            }
                            //merge side effect's neighbor to list
                            List<Integer> tmpList = checkNeighbor(rc[0], rc[1]);
                            for(int element: tmpList){
                                if(!neighborList.contains(element)){
                                    neighborList.add(element);
                                }
                            }

                        }
                        else if (checkRange(rc[0], rc[1])){
                            //no side effect, add to neighborList
                            neighborList.add(rc[0]*BOARD_WIDTH+rc[1]);
                        }
                    }

                    for(int idx:neighborList){
                        Move mv=convertMove(idx);
                        int rIdx = mv.getRow();//idx/BOARD_WIDTH;
                        int cIdx = mv.getCol();//idx%BOARD_WIDTH;

                        if(board[rIdx][cIdx]==playersTurn){
                            score+=Parameters.w_se_self;
                        }
                        else if(board[rIdx][cIdx]==(playersTurn==State.R?State.B:State.R)){
                            score+=Parameters.w_se_other;
                        }
                    }

                    scoreBoard[row][col]=score;

                }
                else{

                    scoreBoard[row][col]=0;
                }

            }
        }
    }

    private int[] sortIdx(HashSet<Integer> moves){
        //HashSet<Integer> moves=getAvailableMoves();
        int moveSize = moves.size();
        int count=0;
        double[] sortScore=new double[moveSize];
        int[] sortIdx = new int[moveSize];
        for(int idx:moves){
            sortIdx[count] =idx;
            Move mv=convertMove(idx);

            sortScore[count] = scoreBoard[mv.getRow()][mv.getCol()];
            count++;
        }
        Utilities.quicksort(sortScore,sortIdx);

        int[] reverseSortMoves =new int[moveSize];
        //HashSet<Integer> outputList = new HashSet<Integer>();
        int idx=moveSize;
        for(int i=0; i<Parameters.TH_n; i++){

            reverseSortMoves[i]=sortIdx[--idx];

            //System.out.println(i);
        }

        //printScoreBoard();
        /*for(int i:reverseSortMoves){
            System.out.print(i+" ");
        }
        System.out.println();
*/
        return reverseSortMoves;
    }
    private List<Integer> checkSideEffect(int row, int col) {
        List<Integer> lonelyList=new ArrayList<>();
        int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};
        for (int[] rc : rcList) {
            if(checkLonely(rc[0], rc[1]))lonelyList.add(convertMove(rc[0],rc[1]));
        }
        return lonelyList;
    }
    private boolean checkRange(int r, int c) {
        if (r >= 0 && r < BOARD_WIDTH && c >= 0 && c < BOARD_WIDTH) return true;
        else return false;
    }

    private boolean checkLonely(int row, int col) {

        if (checkRange(row, col)) {
            int count = 0;
            int rcList[][] = {{row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1}};
            for (int[] rc : rcList)
                if (checkRange(rc[0], rc[1])) {
                    if (board[rc[0]][rc[1]] == State.Blank) count++;
                }

            if (count >= 2) return true;//board[row][col] = State.Blank;
        }
        return false;
    }
    private void checkWinner() {
        int count[] = {0, 0};
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == State.B) count[0]++;
                else if (board[row][col] == State.R) count[1]++;
            }
        }

        //have draw mode
        if(count[1]==0 && count[0]==0){
            gameOver=true;
            winner=State.Blank;
        }else if(count[0]==0){
            gameOver=true;
            winner=State.B;
        }else if(count[1]==0){
            gameOver=true;
            winner=State.R;
        }



    }
    /**
     * Check to see if the game is over (if there is a winner or a draw).
     * @return          true if the game is over
     */
    public boolean isGameOver () {
        return gameOver;
    }


    /**
     * Get a copy of the array that represents the board.
     * @return          the board array
     */
    State[][] toArray () {
        return board.clone();
    }

    /**
     * Check to see who's turn it is.
     * @return          the player who's turn it is
     */
    public State getTurn () {
        return playersTurn;
    }
    public State getPlayerSymbol(){
        return playerSymbol;
    }

    /**
     * Check to see who won.
     * @return          the player who won (or Blank if the game is a draw)
     */
    public State getWinner () {
        if (!gameOver) {
            throw new IllegalStateException("The game is not over yet.");
        }
        return winner;
    }

    /**
     * Get the indexes of all the positions on the board that are empty.
     * @return          the empty cells
     */


    public int remainMoves(){
        if(playersTurn==State.B){return movesAvailableB.size();}
        else if(playersTurn==State.R){return movesAvailableR.size();}
        return 0;
    }
    public HashSet<Integer> getAvailableMoves(){
        if(playersTurn==State.B){
            if(movesAvailableB.size()>Parameters.TH1){
                //System.out.println("BBBBB");
                generateScore();
                //printScoreBoard();
               return Utilities.intArray2HashSet(sortIdx(movesAvailableB));

            }
            return movesAvailableB;
        }
        else {
            if(movesAvailableR.size()>Parameters.TH1){
                //System.out.println("RRRRR");
                generateScore();
                //printScoreBoard();
                return Utilities.intArray2HashSet(sortIdx(movesAvailableR));

            }
            return movesAvailableR;
        }
    }


    /**
     * Get a deep copy
     * @return      an identical copy of the board
     */
    public Board getDeepCopy () {
       // boolean ps = (this.playerSymbol==State.R)?true:false;
        Board board             = new Board(BOARD_WIDTH);

        for (int i = 0; i < board.board.length; i++) {
            board.board[i] = this.board[i].clone();
        }
        board.playerSymbol  = this.playerSymbol;
        board.playersTurn       = this.playersTurn;
        board.winner            = this.winner;
        board.movesAvailableR    = new HashSet<>();
        board.movesAvailableB    = new HashSet<>();
        board.movesAvailableR.addAll(this.movesAvailableR);
        board.movesAvailableB.addAll(this.movesAvailableB);
        board.gameOver          = this.gameOver;
        return board;
    }

    public void print(){
        for (int y = 0; y < BOARD_WIDTH; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {

                if (board[y][x] == State.Blank) {
                    System.out.print("-");
                } else {
                    System.out.print(board[y][x].name());
                }
                System.out.print(" ");

            }
            if (y != BOARD_WIDTH -1) {
                System.out.println();
            }
        }
    }
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < BOARD_WIDTH; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {

                if (board[y][x] == State.Blank) {
                    sb.append("-");
                } else {
                    sb.append(board[y][x].name());
                }
                sb.append(" ");

            }
            if (y != BOARD_WIDTH -1) {
                sb.append("\n");
            }
        }

        return new String(sb);
    }
    public int convertMove(int row,int col){
        return row*BOARD_WIDTH+col;
    }

    public Move convertMove(int move){
        Move mv=new Move(move,BOARD_WIDTH);
        return mv;
    }
}
