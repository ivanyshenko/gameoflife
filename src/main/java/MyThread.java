import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

public class MyThread implements Runnable {

    private int[][] sums;
    private int[][] cells;
    private int id;
    private int N;
    private int M;
    private CyclicBarrier barrier;
    private CyclicBarrier masterBarrier;

    public MyThread(int[][] sums, int[][] cells, int id, int n, int m, CyclicBarrier barrier, CyclicBarrier masterBarrier) {
        this.sums = sums;
        this.cells = cells;
        this.id = id;
        this.N = n;
        this.M = m;
        this.barrier = barrier;
        this.masterBarrier = masterBarrier;
    }

    @Override
    public void run() {
        for (int k = 0; k < M; k++) {
            for (int e = id; e < N*N; e+= GameOfLifeImpl.NUM_THREADS) {
                int i = e/ N;
                int j = e% N;
                sums[i][j] = GameOfLifeImpl.sumOfCells(i, j, cells, N);
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            for (int e = id; e < N*N; e+= GameOfLifeImpl.NUM_THREADS) {
                int i = e/ N;
                int j = e% N;
                cells[i][j]= GameOfLifeImpl.update(cells[i][j], sums[i][j]);
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        try {
            masterBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
