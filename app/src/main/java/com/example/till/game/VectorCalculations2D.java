package com.example.till.game;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by till on 29.12.15.
 * 2D matrices are represented as row vectors (x11, x12, x21, x22)
 */
public class VectorCalculations2D implements Serializable {


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

    public static double[] invert(double[] matrix) {
        checkDimensionsMatrix(matrix);
        return invert(matrix[0], matrix[1], matrix[2], matrix[3]);
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

    public static double determinante(double[] vector1, double[] vector2) {
        return determinante(vector1[0], vector2[0], vector1[1], vector2[1]);
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

    public static double[] transformLinear(double[] transformation, double[] vector) {
        checkDimensionsTransformation(transformation);
        checkDimensionsVector(vector);
        double[] translation = Arrays.copyOfRange(transformation, 0, 2);
        double[] rotation = Arrays.copyOfRange(transformation, 2, 6);
        return add(multiplyMatrixVector(rotation, vector), translation);
    }

    public static double[] transformLinear(double[] translation, double[] rotation, double[] vector) {
        checkDimensionsVector(translation);
        checkDimensionsMatrix(rotation);
        checkDimensionsVector(vector);
        return transformLinear(makeLinearTransformation(translation, rotation), vector);
    }

    public static double[] makeLinearTransformation(double[] translation, double[] rotation) {
        checkDimensionsVector(translation);
        checkDimensionsMatrix(rotation);
        return new double[]{translation[0], translation[1], rotation[0], rotation[1], rotation[2], rotation[3]};
    }

    public static double[] concatLinearTransformation(double[] transformation1, double[] transformation2) {
        checkDimensionsTransformation(transformation1);
        checkDimensionsTransformation(transformation2);
        double[] t1 = Arrays.copyOfRange(transformation1, 0, 2);
        double[] r1 = Arrays.copyOfRange(transformation1, 2, 6);
        double[] t2 = Arrays.copyOfRange(transformation2, 0, 2);
        double[] r2 = Arrays.copyOfRange(transformation2, 2, 6);
        return concatLinearTransformation(transformation1, t2, r2);
    }

    public static double[] concatLinearTransformation(double[] d1, double[] d2, double[] d3) {
        if (d1.length == 6) {
            checkDimensionsTransformation(d1);
            checkDimensionsVector(d2);
            checkDimensionsMatrix(d3);
            double[] t1 = Arrays.copyOfRange(d1, 0, 2);
            double[] r1 = Arrays.copyOfRange(d1, 2, 6);
            return concatLinearTransformation(t1, r1, d2, d3);
        }else if (d1.length == 2) {
            checkDimensionsTransformation(d3);
            checkDimensionsVector(d1);
            checkDimensionsMatrix(d2);
            double[] t2 = Arrays.copyOfRange(d3, 0, 2);
            double[] r2 = Arrays.copyOfRange(d3, 2, 6);
            return concatLinearTransformation(d1, d2, t2, r2);
        } else {
            throw new IllegalArgumentException("First parameter must be eiter a vector or a transformation.");
        }
    }

    public static double[] concatLinearTransformation(double[] translation1, double[] rotation1, double[] translation2, double[] rotation2) {
        checkDimensionsMatrix(rotation1);
        checkDimensionsMatrix(rotation2);
        checkDimensionsVector(translation1);
        checkDimensionsVector(translation2);
        double[] t = add(multiplyMatrixVector(rotation1, translation2), translation1);
        double[] r = multiplyMatrixMatrix(rotation1, rotation2);
        return makeLinearTransformation(t, r);
    }

    public static void checkDimensionsVector(double[] v) {
        if (v.length != 2) {
            throw new IllegalArgumentException("Input must be a two-dimensional vector. The input has dimension " + v.length + ".");
        }
    }

    public static void checkDimensionsMatrix(double[] m) {
        if (m.length != 4) {
            throw new IllegalArgumentException("Input must be a Matrix, represented as a four-dimensional vector. The input has dimension " + m.length + ".");
        }
    }

    public static void checkDimensionsTransformation(double[] t) {
        if (t.length != 6) {
            throw new IllegalArgumentException("The transformation must have six entries. The first two are the translation, the last for are the rotationMatrix. " +
                    "The input has dimension " + t.length + ".");
        }
    }

    public static double[] invertLinearTransformation(double[] transformation) {
        double[] translation = Arrays.copyOfRange(transformation, 0, 2);
        double[] rotation = Arrays.copyOfRange(transformation, 2, 6);
        return invertLinearTransformation(translation, rotation);
    }

    public static double[] invertLinearTransformation(double[] translation, double[] rotation) {
        checkDimensionsVector(translation);
        checkDimensionsMatrix(rotation);
        rotation = invert(rotation);
        translation = scale(multiplyMatrixVector(rotation, translation), -1);
        return new double[]{translation[0], translation[1], rotation[0], rotation[1], rotation[2], rotation[3]};
    }

    public static double[] calculateRotationMatrix(double angle) {
        return  new double[]{Math.cos(angle), -Math.sin(angle),Math.sin(angle), Math.cos(angle)};
    }

    public static double getRotationAngle(double[] matrix) {
        checkDimensionsMatrix(matrix);
        double cos = matrix[0];
        double sin = matrix[2];
        double acos = Math.acos(cos);
        double asin = Math.asin(sin);
        return Math.signum(asin) * acos;
    }

    public static double angleBetweenVectors(double[] v1, double[] v2) {
        checkDimensionsVector(v1);
        checkDimensionsVector(v2);
        double cosPhi = scalarProduct(v1, v2) / (normL2(v1) * normL2(v2));
        if (cosPhi > 1) {
            cosPhi = 1;
        }
        double cos = Math.signum(determinante(v1, v2)) * Math.acos(cosPhi);
        return cos;
    }
}
