package org.example;



import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorOperators;

public class MatrixMultiplicationSIMD {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    public static double[][] matrixMultiplication(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = B[0].length;
        int common = B.length;

        double[][] C = new double[rows][cols];

        double[][] B_transposed = transposeMatrix(B);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                C[i][j] = vectorizedDotProduct(A[i], B_transposed[j], common);
            }
        }

        return C;
    }

    private static double vectorizedDotProduct(double[] rowA, double[] colB, int length) {
        DoubleVector sumVector = DoubleVector.zero(SPECIES);
        int i = 0;

        while (i + SPECIES.length() <= length) {
            DoubleVector vectorA = DoubleVector.fromArray(SPECIES, rowA, i);
            DoubleVector vectorB = DoubleVector.fromArray(SPECIES, colB, i);
            sumVector = sumVector.add(vectorA.mul(vectorB));
            i += SPECIES.length();
        }

        double sum = sumVector.reduceLanes(VectorOperators.ADD);

        while (i < length) {
            sum += rowA[i] * colB[i];
            i++;
        }

        return sum;
    }

    private static double[][] transposeMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }

        return transposed;
    }
}