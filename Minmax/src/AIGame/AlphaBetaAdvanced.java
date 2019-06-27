package AIGame;


public class AlphaBetaAdvanced {
    static boolean DEBUG=false;

    private static double maxPly;

    /**
     * AlphaBetaAdvanced cannot be instantiated.
     */
    private AlphaBetaAdvanced() {}

    /**
     * Execute the algorithm.
     * @param player        the player that the AI will identify as
     * @param board         the game board to play on
     * @param maxPly        the maximum depth
     */
    public static void run (Board.State player, Board board, double maxPly) {

        if (maxPly < 1) {
            throw new IllegalArgumentException("Maximum depth must be greater than 0.");
        }

        AlphaBetaAdvanced.maxPly = maxPly;
        int bestmove = alphaBetaPruning(player, board, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        Move mv=board.convertMove(bestmove);
        System.out.println("AI plays="+(mv.getRow()+1)+" "+(mv.getCol()+1));
    }

    /**
     * The meat of the algorithm.
     * @param player        the player that the AI will identify as
     * @param board         the board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int alphaBetaPruning (Board.State player, Board board, double alpha, double beta, int currentPly) {
        if (currentPly++ == maxPly || board.isGameOver()) {
            return score(player, board, currentPly);
        }
        if(DEBUG)System.out.println("\n" + board + "\n");

        if (board.getTurn() == player) {
            if(DEBUG)System.out.println("get max");
            return getMax(player, board, alpha, beta, currentPly);
        } else {
            if(DEBUG)System.out.println("get min");
            return getMin(player, board, alpha, beta, currentPly);
        }
    }

    /**
     * Play the move with the highest score.
     * @param player        the player that the AI will identify as
     * @param board         the board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMax (Board.State player, Board board, double alpha, double beta, int currentPly) {
        int indexOfBestMove = -1;

        for (Integer theMove : board.getAvailableMoves()) {
            //System.out.println("[max]"+theMove);

            Board modifiedBoard = board.getDeepCopy();
            modifiedBoard.move(theMove);
            if(DEBUG)System.out.println("##[max]after move\n" + modifiedBoard + "\n");
            int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);
            if(DEBUG)System.out.println("score="+score+" alpha="+alpha+" beta="+beta);
            if (score > alpha) {
                alpha = score;
                indexOfBestMove = theMove;
                if(DEBUG)System.out.println("[max]best move:"+theMove);
            }

            if (alpha >= beta) {
                break;
            }
        }

        if (indexOfBestMove != -1) {

            board.move(indexOfBestMove);
        }
        return indexOfBestMove;//(int)alpha;
    }

    /**
     * Play the move with the lowest score.
     * @param player        the player that the AI will identify as
     * @param board         the board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMin (Board.State player, Board board, double alpha, double beta, int currentPly) {
        int indexOfBestMove = -1;

        for (Integer theMove : board.getAvailableMoves()) {
            //System.out.println("[min]"+theMove);
            Board modifiedBoard = board.getDeepCopy();
            modifiedBoard.move(theMove);
            if(DEBUG)System.out.println("##[min]after move\n" + modifiedBoard + "\n");
            int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);
            if(DEBUG)System.out.println("score="+score+" alpha="+alpha+" beta="+beta);
            if (score < beta) {
                beta = score;
                indexOfBestMove = theMove;
                if(DEBUG)System.out.println("[min]best move:"+theMove);
            }

            if (alpha >= beta) {
                break;
            }
        }

        if (indexOfBestMove != -1) {
            board.move(indexOfBestMove);
        }
        return indexOfBestMove;//(int)beta;
    }

    /**
     * Get the score of the board. Takes depth into account.
     * @param player        the play that the AI will identify as
     * @param board         the board to play on
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int score (Board.State player, Board board, int currentPly) {

        if (player == Board.State.Blank) {
            throw new IllegalArgumentException("Player must be X or O.");
        }

        Board.State opponent = (player == Board.State.R) ? Board.State.B : Board.State.R;

        if (board.isGameOver() && board.getWinner() == player) {
            return Parameters.score_k - currentPly;
        } else if (board.isGameOver() && board.getWinner() == opponent) {
            return -Parameters.score_k + currentPly;
        } else {
            return 0;
        }
    }

}
