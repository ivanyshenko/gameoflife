import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameOfLifeImpl implements GameOfLife {
    @Override
    public List<String> play(String inputFile) {
        List<String> result = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(inputFile));
            int N = scanner.nextInt();
            int M = scanner.nextInt();
            scanner.nextLine();
            int[][] cells = new int[N][N];
            int[][] sums = new int[N][N];
            for (int i = 0; i < N; i++) {
                char[] chars = scanner.nextLine().toCharArray();
                for (int j = 0; j < N; j++) {
                    cells[i][j] = chars[j] == '1' ? 1 : 0;
                }
            }


            for(int k = 0; k < M; k++) {
                for(int i=0; i<N; i++){
                    for(int j=0; j<N; j++){
                        sums[i][j] = sumOfCells(i, j, cells, N);
                    }
                }

                for(int i=0; i<N; i++)
                    for(int j=0; j<N; j++)
                        cells[i][j]= update(cells[i][j], sums[i][j]);
            }

            for (int i = 0; i < N; i++) {
                result.add(
                        Arrays.toString(cells[i])
                                .replace(", ", "")
                                .replace("[","")
                                .replace("]", ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int update(int cell, int sum) {
        if (cell == 0){
            if(sum == 3) return 1;
        } else if (sum == 2 || sum == 3) return 1;
        return 0;
    }

    private int sumOfCells(int i, int j, int[][] cells, int N){
        int sum = 0;
        for (int k = i - 1; k < i + 2; k++) {
            for (int l = j - 1; l < j + 2; l++) {
                sum += cells[(k + N)%N][(l + N)%N];
//                if (i == 3 && j == 3)
//                    System.out.println(k + " " + l + " " + cells[(k + N)%N][(l + N)%N]);
            }
        }
        return sum - cells[i][j];
    }

    public static void main(String[] args) {
        System.out.println(-1 % 5);
//        GameOfLifeImpl gameOfLife = new GameOfLifeImpl();
//        gameOfLife.play("C:\\Users\\alex\\IdeaProjects\\gameoflife1\\gameoflife\\resources\\input.txt");
    }
}
