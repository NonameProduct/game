package com.example.till.game.test;

import com.example.till.game.VectorCalculations2D;

import junit.framework.TestCase;

/**
 * Created by till on 29.12.15.
 */
public class VectorCalculations2DTest extends TestCase {
    public void testMatrixMultiplicationAndInversion() {
        double[] matrix1 = {2.3, 4.5, 6.534, 21.54};
        double[] inverse = VectorCalculations2D.invert(matrix1);
        double[] product = VectorCalculations2D.multiplyMatrixMatrix(matrix1, inverse);
        roundComponentsTo10Digits(product);
        assertEquals(inverse.length, 4);
        assertEquals(product.length, 4);
        assertTrue(VectorCalculations2D.matrixEquals(product, new double[]{1, 0, 0, 1}));
    }

    public void testMatrixTranspose() {
        double[] matrix = {2, 4, 5, 3};
        double[] transposed = VectorCalculations2D.transpose(matrix);
        assertTrue(VectorCalculations2D.matrixEquals(new double[]{2, 5, 4, 3}, transposed));
    }

    public void testDeterminante() {
        double[] matrix = {1, 2, 3, 4};
        double determinanteCalc = VectorCalculations2D.determinante(matrix);
        double expected = 4 - 6;
        assertEquals(expected, determinanteCalc);
    }

    public void testMatrixAdditionAndSubstraction() {
        double[] v1 = {1, 2};
        double[] v2 = {3, 4};
        double[] sum = VectorCalculations2D.add(v1, v2);
        double[] diff = VectorCalculations2D.substract(v1, v2);
        double scalarProd = VectorCalculations2D.scalarProduct(v1, v2);
        assertTrue(VectorCalculations2D.vectorEquals(sum, new double[]{4, 6}));
        assertTrue(VectorCalculations2D.vectorEquals(diff, new double[]{-2, -2}));
        assertEquals(11.0, scalarProd);
    }

    public void testNorm() {
        double[] v = {3, 4};
        double norm = VectorCalculations2D.normL2(v);
        assertEquals(5, norm);

    }

    private void roundComponentsTo10Digits(double[] d) {
        for (int i = 0; i < d.length; i++) {
            d[i] = Math.round(10000000000l*d[i])/10000000000l;
        }

    }


}
