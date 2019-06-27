package AIGame;
public class Move{

    private int row=-1;
    private int col=-1;
    public Move(int idx,int boardSize){
        //System.out.println("#"+idx);
        //System.out.println("#"+boardSize);

        this.row = idx/boardSize;
        this.col = idx%boardSize;
        //System.out.println(row);
        //System.out.println(col);
    }
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
    public static void main(String[] args) {

        Move mv=new Move(10,6);
        System.out.println("AI plays:"+(mv.row+1)+" "+(mv.col+1));
    }

}