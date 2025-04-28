package com.corp.imgpro.edgedetect.method;

import com.corp.imgpro.edgedetect.common.Filters;
import org.apache.commons.math3.linear.MatrixUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SobelStandard {

    private SobelStandard() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

    public static BufferedImage convert(BufferedImage image) {
        var edgeColorMatrix = computeEdgeColors(image);
        var maxGradient = findMaxGradient(edgeColorMatrix);

        return norm(maxGradient, edgeColorMatrix, image);
    }

    private static int[][] computeEdgeColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] valMatrix = new int[3][3];

        double[][] sobelMatrix = Filters.getSobelMatrix();
        double[][] transposeSobelMatrix = MatrixUtils.createRealMatrix(sobelMatrix).transpose().getData();
        int[][] edgeColorMatrix = new int[width][height];

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                valMatrix[0][0] = convertRGBToGrayScale(image.getRGB(i - 1, j - 1));
                valMatrix[0][1] = convertRGBToGrayScale(image.getRGB(i - 1, j));
                valMatrix[0][2] = convertRGBToGrayScale(image.getRGB(i - 1, j + 1));

                valMatrix[1][0] = convertRGBToGrayScale(image.getRGB(i, j - 1));
                valMatrix[1][1] = convertRGBToGrayScale(image.getRGB(i, j));
                valMatrix[1][2] = convertRGBToGrayScale(image.getRGB(i, j + 1));

                valMatrix[2][0] = convertRGBToGrayScale(image.getRGB(i + 1, j - 1));
                valMatrix[2][1] = convertRGBToGrayScale(image.getRGB(i + 1, j));
                valMatrix[2][2] = convertRGBToGrayScale(image.getRGB(i + 1, j + 1));

                int gx = mask(sobelMatrix, valMatrix);
                int gy = mask(transposeSobelMatrix, valMatrix);

                double gval = Math.sqrt((double) (gx * gx) + (gy * gy));
                int g = (int) gval;

                edgeColorMatrix[i][j] = g;
            }
        }

        return edgeColorMatrix;
    }

    private static int convertRGBToGrayScale(int rgbValue) {
        Color c = new Color(rgbValue);
        // Convert gradient value to color
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        // Linear luminance representation of RGB
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    private static int mask(double[][] sobelMatrix, int[][] valMatrix) {
        int gval = 0;
        int width = sobelMatrix.length;
        int height = sobelMatrix[0].length;

        for (int k = 0; k < width; k++) {
            for (int l = 0; l < height; l++) {
                gval += (int) (sobelMatrix[k][l] * valMatrix[k][l]);
            }
        }
        return gval;
    }

    private static int findMaxGradient(int[][] edgeColorMatrix) {
        int max = -1;
        int width = edgeColorMatrix.length;
        int height = edgeColorMatrix[0].length;

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (max < edgeColorMatrix[i][j]) {
                    max = edgeColorMatrix[i][j];
                }
            }
        }
        return max;
    }

    private static BufferedImage norm(int maxGradient, int[][] edgeColorMatrix, BufferedImage image) {
        double scale = 255.0 / maxGradient;
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage normalizedImage = new BufferedImage(width, height, image.getType());

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                int edgeColor = edgeColorMatrix[i][j];
                edgeColor = (int) (edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                normalizedImage.setRGB(i, j, edgeColor);
            }
        }

        return normalizedImage;
    }
}
