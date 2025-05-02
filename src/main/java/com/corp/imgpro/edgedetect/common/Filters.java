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
        int[][] sobelXMatrix = new int[3][3];

        sobelXMatrix[0][0] = -1;
        sobelXMatrix[0][1] = 0;
        sobelXMatrix[0][2] = 1;
        sobelXMatrix[1][0] = -2;
        sobelXMatrix[1][1] = 0;
        sobelXMatrix[1][2] = 2;
        sobelXMatrix[2][0] = -1;
        sobelXMatrix[2][1] = 0;
        sobelXMatrix[2][2] = 1;

        return sobelXMatrix;
    }

    /**
     * Returns the Sobel operator matrix for vertical edge detection.
     *
     * @return A 3x3 integer array representing the Sobel Y operator
     */
    public static int[][] getSobelYMatrix() {
        int[][] sobelYMatrix = new int[3][3];

        sobelYMatrix[0][0] = -1;
        sobelYMatrix[0][1] = -2;
        sobelYMatrix[0][2] = -1;
        sobelYMatrix[1][0] = 0;
        sobelYMatrix[1][1] = 0;
        sobelYMatrix[1][2] = 0;
        sobelYMatrix[2][0] = 1;
        sobelYMatrix[2][1] = 2;
        sobelYMatrix[2][2] = 1;

        return sobelYMatrix;
    }

    /**
     * Returns the Tornado-specific Sobel X operator matrix for horizontal-edge detection.
     *
     * @return A Matrix2DInt representing the Sobel X operator
     */
    public static Matrix2DInt getTornadoSobelXMatrix() {
        Matrix2DInt tornadoSobelXMatrix = new Matrix2DInt(3, 3);

        tornadoSobelXMatrix.set(0, 0, -1);
        tornadoSobelXMatrix.set(0, 1, 0);
        tornadoSobelXMatrix.set(0, 2, 1);
        tornadoSobelXMatrix.set(1, 0, -2);
        tornadoSobelXMatrix.set(1, 1, 0);
        tornadoSobelXMatrix.set(1, 2, 2);
        tornadoSobelXMatrix.set(2, 0, -1);
        tornadoSobelXMatrix.set(2, 1, 0);
        tornadoSobelXMatrix.set(2, 2, 1);

        return tornadoSobelXMatrix;
    }

    /**
     * Returns the Tornado-specific Sobel Y operator matrix for vertical-edge detection.
     *
     * @return A Matrix2DInt representing the Sobel Y operator
     */
    public static Matrix2DInt getTornadoSobelYMatrix() {
        Matrix2DInt tornadoSobelYMatrix = new Matrix2DInt(3, 3);

        tornadoSobelYMatrix.set(0, 0, -1);
        tornadoSobelYMatrix.set(0, 1, -2);
        tornadoSobelYMatrix.set(0, 2, -1);
        tornadoSobelYMatrix.set(1, 0, 0);
        tornadoSobelYMatrix.set(1, 1, 0);
        tornadoSobelYMatrix.set(1, 2, 0);
        tornadoSobelYMatrix.set(2, 0, 1);
        tornadoSobelYMatrix.set(2, 1, 2);
        tornadoSobelYMatrix.set(2, 2, 1);

        return tornadoSobelYMatrix;
    }
}