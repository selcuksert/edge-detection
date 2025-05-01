package com.corp.imgpro.edgedetect.method;

import com.corp.imgpro.edgedetect.common.Filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A utility class implementing the standard Sobel edge detection algorithm.
 * This implementation processes images using CPU-based calculations without hardware acceleration.
 * The Sobel operator calculates the gradient of the image intensity at each point, giving the direction
 * of the largest possible increase from light to dark and the rate of change in that direction.
 * 
 * <p>The class uses both horizontal and vertical Sobel operators to detect edges in both directions
 * and combines them to create a complete edge detection result.</p>
 */
public class SobelStandard {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws IllegalStateException always, as this class should not be instantiated
     */
    private SobelStandard() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

    /**
     * Converts an input image to its edge-detected version using the Sobel operator.
     * The process involves computing edge colors, finding the maximum gradient,
     * and normalizing the result to create the final edge-detected image.
     *
     * @param image the input image to be processed
     * @return a new BufferedImage containing the edge-detected version of the input image
     */
    public static BufferedImage convert(BufferedImage image) {
        var edgeColorMatrix = computeEdgeColors(image);
        var maxGradient = findMaxGradient(edgeColorMatrix);

        return norm(maxGradient, edgeColorMatrix, image);
    }

    /**
     * Computes the edge colors for each pixel in the image using Sobel operators.
     * The method applies both horizontal and vertical Sobel operators to detect edges
     * in both directions and combines them using the magnitude formula: sqrt(gx² + gy²).
     *
     * @param image the input image to process
     * @return a 2D array containing the computed edge values for each pixel
     */
    private static int[][] computeEdgeColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] valMatrix = new int[3][3];

        int[][] sobelXMatrix = Filters.getSobelXMatrix();
        int[][] sobelYMatrix = Filters.getSobelYMatrix();
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

                int gx = mask(sobelXMatrix, valMatrix);
                int gy = mask(sobelYMatrix, valMatrix);

                double gval = Math.sqrt((double) (gx * gx) + (gy * gy));
                int g = (int) gval;

                edgeColorMatrix[i][j] = g;
            }
        }

        return edgeColorMatrix;
    }

    /**
     * Converts an RGB value to its grayscale equivalent using the linear luminance formula.
     * The formula used is: 0.2126R + 0.7152G + 0.0722B
     * This formula takes into account human perception of colors.
     *
     * @param rgbValue the RGB value to convert
     * @return the grayscale value between 0 and 255
     */
    private static int convertRGBToGrayScale(int rgbValue) {
        Color c = new Color(rgbValue);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    /**
     * Applies a convolution mask to a 3x3 matrix of values.
     * This method is used to apply the Sobel operator to a local neighborhood of pixels.
     *
     * @param sobelMatrix the Sobel operator matrix (either X or Y)
     * @param valMatrix the 3x3 matrix of pixel values to process
     * @return the result of applying the mask
     */
    private static int mask(int[][] sobelMatrix, int[][] valMatrix) {
        int gval = 0;
        int width = sobelMatrix.length;
        int height = sobelMatrix[0].length;

        for (int k = 0; k < width; k++) {
            for (int l = 0; l < height; l++) {
                gval += (sobelMatrix[k][l] * valMatrix[k][l]);
            }
        }
        return gval;
    }

    /**
     * Finds the maximum gradient value in the edge color matrix.
     * This value is used for normalizing the final edge-detected image.
     *
     * @param edgeColorMatrix the matrix containing edge values
     * @return the maximum gradient value found
     */
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

    /**
     * Normalizes the edge values and creates the final edge-detected image.
     * The method scales the edge values to the range 0-255 and creates a grayscale image
     * where edges are represented by lighter pixels.
     *
     * @param maxGradient the maximum gradient value used for normalization
     * @param edgeColorMatrix the matrix of edge values
     * @param image the original input image (used for dimensions and type)
     * @return the normalized edge-detected image
     */
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