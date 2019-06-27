
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        System.out.println("Enter a file or randomly generate:");
        Scanner scanner = new Scanner(System.in);
        FileReader fr = null;
        int boardWidth = 5;
        boolean playerSymbol=true;
        boolean playerFirst;
        char[][] myboard= null;//new Board.State[boardWidth][boardWidth];

        String inputFilename = scanner.next();

        try {
            fr = new FileReader(inputFilename);

            BufferedReader br = new BufferedReader(fr);
            int linecount =0;
            while (br.ready()) {
                if(linecount==0){
                    boardWidth=Integer.parseInt(br.readLine().trim());
                    myboard = new char[boardWidth][boardWidth];
                }
                else {
                    String[] symbol = br.readLine().split(" ");
                    for(int i=0;i<boardWidth;i++){
                        myboard[linecount-1][i]=(symbol[i].equalsIgnoreCase("B"))?'B':'R';

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
        scanner = new Scanner(System.in);
        char symbol=scanner.next().charAt(0);
        if(symbol=='B')playerSymbol=false;


        System.out.println("Do you want to play first? (1=yes,0=no,default=1)");
        scanner = new Scanner(System.in);
        playerFirst= (scanner.nextInt()==1)?true:false ;
        AIGame MYGAME = new AIGame(playerSymbol,playerFirst,boardWidth,myboard);
        MYGAME.go();
    }
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min+  1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min))+   min;
            boolean flag = true;
            for (int j = 0; j < n; j++  ) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }
}
