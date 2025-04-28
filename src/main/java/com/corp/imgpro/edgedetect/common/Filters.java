package com.corp.imgpro.edgedetect.common;

import uk.ac.manchester.tornado.api.types.matrix.Matrix2DInt;

public class Filters {

    private Filters() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

    public static double[][] getSobelMatrix() {
        double[][] sobelMatrix = new double[3][3];

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

    public static Matrix2DInt getTornadoSobelMatrix() {
        Matrix2DInt tornadoSobelMatrix = new Matrix2DInt(3, 3);

        tornadoSobelMatrix.set(0, 0, 1);
        tornadoSobelMatrix.set(0, 1, 0);
        tornadoSobelMatrix.set(0, 2, -1);
        tornadoSobelMatrix.set(1, 0, 2);
        tornadoSobelMatrix.set(1, 1, 0);
        tornadoSobelMatrix.set(1, 2, -2);
        tornadoSobelMatrix.set(2, 0, 1);
        tornadoSobelMatrix.set(2, 1, 0);
        tornadoSobelMatrix.set(2, 2, -1);

        return tornadoSobelMatrix;
    }

}
