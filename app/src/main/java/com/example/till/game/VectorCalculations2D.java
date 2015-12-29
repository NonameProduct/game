package com.example.till.game;

/**
 * Created by till on 29.12.15.
 * 2D matrices are represented as row vectors (x11, x12, x21, x22)
 */
public class VectorCalculations2D {


    public static double[] add(double[] v1, double[] v2) {
        return add(v1[0], v1[1], v2[0], v2[1]);
    }

    public static double[] add(double x1, double x2, double y1, double y2) {
        return new double[]{x1+y1, x2+y2};
    }

    public static double[] substract(double[] v1, double[] v2) {
        return substract(v1[0], v1[1], v2[0], v2[1]);
    }

    public static double[] substract(double x1, double x2, double y1, double y2) {
        return add(x1, x2, -y1, -y2);
    }

    public static double scalarProduct(double[] v1, double[] v2) {
        return scalarProduct(v1[0], v1[1], v2[0], v2[1]);
    }

    public static double scalarProduct(double x1, double x2, double y1, double y2) {
        return x1*y1 + x2*y2;
    }

    public static double[] transpose(double x11, double x12, double x21, double x22) {
        return new double[]{x11, x21, x12, x22};
    }

    public static double[] invert(double x11, double x12, double x21, double x22) {
        double factor = 1.0 / (x11 * x22 - x12 * x21);
        return new double[]{factor * x22, factor * -x12, factor * -x21, factor * x11};
    }

    public static double[] invert(double[] matrix1) {
        return invert(matrix1[0], matrix1[1], matrix1[2], matrix1[3]);
    }

    public static double[] multiplyMatrixMatrix(double[] m1, double[] m2) {
        double x11 = m1[0] * m2[0] + m1[1] * m2[2];
        double x12 = m1[0] * m2[1] + m1[1] * m2[3];
        double x21 = m1[2] * m2[0] + m1[3] * m2[2];
        double x22 = m1[2] * m2[1] + m1[3] * m2[3];
        return new double[]{x11, x12, x21, x22};
    }

    public static boolean matrixEquals(double[] matrix1, double[] matrix2) {
        for (int i = 0; i < matrix1.length; i++) {
            if (matrix1[i] != matrix2[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean vectorEquals(double[] v1, double[] v2) {
        return v1[0] == v2[0] && v1[1] == v2[1];
    }

    public static double determinante(double[] matrix) {
        return determinante(matrix[0], matrix[1], matrix[2], matrix[3]);
    }

    public static double determinante(double x11, double x12, double x21, double x22) {
        return x11*x22-x12*x21;
    }

    public static double[] transpose(double[] m) {
        return transpose(m[0], m[1], m[2], m[3]);
    }

    public static double[] scale(double[] m, double factor) {
        double[] result = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            result[i] = m[i]*factor;
        }
        return result;
    }

    public static double[] multiplyMatrixVector(double[] m, double[] v) {
        return new double[]{m[0] * v[0] + m[1] * v[1], m[2] * v[0] + m[3] * v[1]};
    }

    public static double normL2(double[] v) {
        double sumOfSquares = 0;
        for (double d : v) {
            sumOfSquares += d*d;
        }
        return Math.sqrt(sumOfSquares);
    }
}
