package com.corp.imgpro.edgedetect.common;

import uk.ac.manchester.tornado.api.types.matrix.Matrix2DInt;

/**
 * Utility class providing Sobel filter matrices for edge detection operations.
 * Contains both standard array-based and Tornado-specific matrix implementations
 * of the Sobel operators for x and y directions.
 */
public class Filters {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws IllegalStateException if an attempt is made to instantiate this class
     */
    private Filters() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

    /**
     * Returns the Sobel operator matrix for horizontal edge detection.
     *
     * @return A 3x3 integer array representing the Sobel X operator
     */
    public static int[][] getSobelXMatrix() {
        int[][] sobelMatrix = new int[3][3];

        sobelMatrix[0][0] = -1;
        sobelMatrix[0][1] = 0;
        sobelMatrix[0][2] = 1;
        sobelMatrix[1][0] = -2;
        sobelMatrix[1][1] = 0;
        sobelMatrix[1][2] = 2;
        sobelMatrix[2][0] = -1;
        sobelMatrix[2][1] = 0;
        sobelMatrix[2][2] = 1;

        return sobelMatrix;
    }

    /**
     * Returns the Sobel operator matrix for vertical edge detection.
     *
     * @return A 3x3 integer array representing the Sobel Y operator
     */
    public static int[][] getSobelYMatrix() {
        int[][] transposeSobelMatrix = new int[3][3];

        transposeSobelMatrix[0][0] = -1;
        transposeSobelMatrix[0][1] = -2;
        transposeSobelMatrix[0][2] = -1;
        transposeSobelMatrix[1][0] = 0;
        transposeSobelMatrix[1][1] = 0;
        transposeSobelMatrix[1][2] = 0;
        transposeSobelMatrix[2][0] = 1;
        transposeSobelMatrix[2][1] = 2;
        transposeSobelMatrix[2][2] = 1;

        return transposeSobelMatrix;
    }

    /**
     * Returns the Tornado-specific Sobel X operator matrix for horizontal-edge detection.
     *
     * @return A Matrix2DInt representing the Sobel X operator
     */
    public static Matrix2DInt getTornadoSobelXMatrix() {
        Matrix2DInt tornadoSobelMatrix = new Matrix2DInt(3, 3);

        tornadoSobelMatrix.set(0, 0, -1);
        tornadoSobelMatrix.set(0, 1, 0);
        tornadoSobelMatrix.set(0, 2, 1);
        tornadoSobelMatrix.set(1, 0, -2);
        tornadoSobelMatrix.set(1, 1, 0);
        tornadoSobelMatrix.set(1, 2, 2);
        tornadoSobelMatrix.set(2, 0, -1);
        tornadoSobelMatrix.set(2, 1, 0);
        tornadoSobelMatrix.set(2, 2, 1);

        return tornadoSobelMatrix;
    }

    /**
     * Returns the Tornado-specific Sobel Y operator matrix for vertical-edge detection.
     *
     * @return A Matrix2DInt representing the Sobel Y operator
     */
    public static Matrix2DInt getTornadoSobelYMatrix() {
        Matrix2DInt transposeTornadoSobelMatrix = new Matrix2DInt(3, 3);

        transposeTornadoSobelMatrix.set(0, 0, -1);
        transposeTornadoSobelMatrix.set(0, 1, -2);
        transposeTornadoSobelMatrix.set(0, 2, -1);
        transposeTornadoSobelMatrix.set(1, 0, 0);
        transposeTornadoSobelMatrix.set(1, 1, 0);
        transposeTornadoSobelMatrix.set(1, 2, 0);
        transposeTornadoSobelMatrix.set(2, 0, 1);
        transposeTornadoSobelMatrix.set(2, 1, 2);
        transposeTornadoSobelMatrix.set(2, 2, 1);

        return transposeTornadoSobelMatrix;
    }
}