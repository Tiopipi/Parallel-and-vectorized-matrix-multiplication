
package benchmark;

import org.example.MatrixMultiplication;
import org.example.MatrixMultiplicationSIMD;
import org.example.ParallelMatrixMultiplication;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@BenchmarkMode({Mode.AverageTime, })
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class DenseMatrixBenchmark {

    @Param({"784", "1024", "1764", "2024"})
    public static int size;

    private double[][] A;
    private double[][] B;
    private List<Long> memoryUsages;
    private final int threads = 11;


    @Setup(Level.Trial)
    public void setupMatrices() {
        A = generateMatrix(size);
        B = generateMatrix(size);
        memoryUsages = new ArrayList<>();
    }

    @Setup(Level.Invocation)
    public void setupMemoryTracking() {
        System.gc();
    }

    @Benchmark
    public void originalMatrixMultiplication() {
        MatrixMultiplication.matrixMultiplication(A, B);
    }

    @Benchmark
    public void parallelMatrixMultiplication() throws InterruptedException {
        ParallelMatrixMultiplication.matrixMultiplication(A, B, threads);
    }

    @Benchmark
    public void vectorizedMatrixMultiplication() {
        MatrixMultiplicationSIMD.matrixMultiplication(A, B);
    }


    @TearDown(Level.Invocation)
    public void tearDownMemoryTracking() {
        long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
        memoryUsages.add(memoryUsed);
    }

    @TearDown(Level.Trial)
    public void calculateAverageMemoryUsage() {
        long totalMemoryUsed = memoryUsages.stream().mapToLong(Long::longValue).sum();
        long averageMemoryUsed = totalMemoryUsed / memoryUsages.size();
        System.out.println("\nAverage memory used: " + averageMemoryUsed + " KB");
    }

    private static double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
}
