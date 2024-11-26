package org.example;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class ParallelMatrixMultiplication {

    private static class RowMultiplierTask implements Runnable {
        private final double[][] A;
        private final double[][] B;
        private final double[][] C;
        private final int row;

        public RowMultiplierTask(double[][] A, double[][] B, double[][] C, int row) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.row = row;
        }

        @Override
        public void run() {
            int n = B[0].length;
            int common = B.length;
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < common; k++) {
                    C[row][j] += A[row][k] * B[k][j];
                }
            }
        }
    }

    public static double[][] matrixMultiplication(double[][] A, double[][] B, int threadCount) throws InterruptedException {
        int rows = A.length;
        int cols = B[0].length;
        double[][] C = new double[rows][cols];

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            futures.add(executor.submit(new RowMultiplierTask(A, B, C, i)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return C;
    }
}