import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

public class GameOfLifeImpl implements GameOfLife {

    public static int NUM_THREADS = 16;

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

            CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
            CyclicBarrier masterBarrier = new CyclicBarrier(NUM_THREADS+1);
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < NUM_THREADS; i++) {
                threads.add(new Thread(new MyThread(sums, cells, i, N, M, barrier, masterBarrier)));
            }
            long t = System.currentTimeMillis();
            for (Thread thread : threads) {
                thread.start();
            }
            masterBarrier.await();
//            runGameOfLife(N, M, cells, sums);
            System.out.println(System.currentTimeMillis() - t);

            //подготовка к тому формату который ожидается в unit test
            for (int i = 0; i < N; i++) {
                result.add(
                        Arrays.toString(cells[i])
                                .replace(", ", "")
                                .replace("[","")
                                .replace("]", ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void runGameOfLife(int n, int m, int[][] cells, int[][] sums) {
        for(int k = 0; k < m; k++) {

            IntStream.range(0, n * n).parallel().forEach(e ->{
                final int i = e/ n;
                final int j = e% n;
                sums[i][j] = sumOfCells(i, j, cells, n);
            } );
            IntStream.range(0, n * n).parallel().forEach(e ->{
                int i = e/ n;
                int j = e% n;
                cells[i][j]= update(cells[i][j], sums[i][j]);
            } );
        }
    }

    public static int update(int cell, int sum) {
        if (cell == 0){
            if(sum == 3) return 1;
        } else if (sum == 2 || sum == 3) return 1;
        return 0;
    }

    public static int sumOfCells(int i, int j, int[][] cells, int N){
        return cells[(i-1 + N)%N][(j-1 + N)%N] +
                cells[(i-1 + N)%N][(j + N)%N]     +
                cells[(i-1 + N)%N][(j+1 + N)%N]   +
                cells[(i + N)%N][(j-1 + N)%N]     +
                cells[(i + N)%N][(j+1 + N)%N]     +
                cells[(i+1 + N)%N][(j-1 + N)%N]   +
                cells[(i+1 + N)%N][(j + N)%N]     +
                cells[(i+1 + N)%N][(j+1 + N)%N];
    }
}
