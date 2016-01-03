package com.example.till.game.test;

import static com.example.till.game.VectorCalculations2D.*;

import junit.framework.TestCase;

/**
 * Created by till on 29.12.15.
 */
public class VectorCalculations2DTest extends GameTestCase {
    public void testMatrixMultiplicationAndInversion() {
        double[] matrix1 = {2.3, 4.5, 6.534, 21.54};
        double[] inverse = invert(matrix1);
        double[] product = multiplyMatrixMatrix(matrix1, inverse);
        roundComponentsTo10Digits(product);
        assertEquals(inverse.length, 4);
        assertEquals(product.length, 4);
        assertTrue(matrixEquals(product, new double[]{1, 0, 0, 1}));
    }

    public void testMatrixTranspose() {
        double[] matrix = {2, 4, 5, 3};
        double[] transposed = transpose(matrix);
        assertTrue(matrixEquals(new double[]{2, 5, 4, 3}, transposed));
    }

    public void testDeterminante() {
        double[] matrix = {1, 2, 3, 4};
        double determinanteCalc = determinante(matrix);
        double expected = 4 - 6;
        assertEquals(expected, determinanteCalc);
    }

    public void testMatrixAdditionAndSubstraction() {
        double[] v1 = {1, 2};
        double[] v2 = {3, 4};
        double[] sum = add(v1, v2);
        double[] diff = substract(v1, v2);
        double scalarProd = scalarProduct(v1, v2);
        assertTrue(vectorEquals(sum, new double[]{4, 6}));
        assertTrue(vectorEquals(diff, new double[]{-2, -2}));
        assertEquals(11.0, scalarProd);
    }

    public void testNorm() {
        double[] v = {3, 4};
        double norm = normL2(v);
        assertEquals(5.0, norm);

    }

    private void roundComponentsTo10Digits(double[] d) {
        for (int i = 0; i < d.length; i++) {
            d[i] = Math.round(10000000000l*d[i])/10000000000l;
        }
    }

    public void testGetAngleFromRotationMatrix() {
        double[] angles = {3.1, -2.5,  1.2};
        for (double d:angles) {
            double calculatedAngle = getRotationAngle(calculateRotationMatrix(d));
            assertTrue(Math.abs(d - calculatedAngle)<0.00000000001);

        }
    }


}
