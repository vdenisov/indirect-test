package org.plukh.indirecttest.work;

public class ComputationalWork implements Work {
    private final double[][] A = new double[][] {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
    private final double[][] B = new double[][] {{0.1, 0.2, 0.3}, {0.4, 0.5, 0.6}, {0.7, 0.8, 0.9}};

    @Override
    public void init() {

    }

    @Override
    public void work() {
        final double[][] C = multiply(A, B);
    }

    @Override
    public void dispose() {

    }

    private double[][] multiply(double[][] A, double[][] B) {
        int mA = A.length;
        int nA = A[0].length;
        int mB = B.length;
        int nB = A[0].length;

        if (nA != mB) throw new IllegalArgumentException("Illegal matrix dimensions.");

        double[][] C = new double[mA][nB];

        for (int i = 0; i < mA; i++)
            for (int j = 0; j < nB; j++)
                for (int k = 0; k < nA; k++)
                    C[i][j] += (A[i][k] * B[k][j]);

        return C;
    }

}
