package com.corp.imgpro.edgedetect.method;

import com.corp.imgpro.edgedetect.common.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.enums.ProfilerMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;
import uk.ac.manchester.tornado.api.math.TornadoMath;
import uk.ac.manchester.tornado.api.types.matrix.Matrix2DInt;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Implementation of the Sobel edge detection algorithm using TornadoVM for GPU acceleration.
 * This class provides functionality to detect edges in images by computing intensity gradients
 * using Sobel operators and processing the computation on available GPU devices.
 *
 * <p>The class uses TornadoVM's TaskGraph API to parallelize the edge detection computation
 * and optimize performance through GPU execution. The implementation includes the following main steps:</p>
 * <ol>
 *     <li>Converting input image to matrix representation</li>
 *     <li>Computing edge colors using Sobel operators</li>
 *     <li>Normalizing the results</li>
 *     <li>Converting the processed matrix back to an image</li>
 * </ol>
 *
 * <p>This class is designed as a utility class and cannot be instantiated.</p>
 *
 * @see uk.ac.manchester.tornado.api.TaskGraph
 * @see uk.ac.manchester.tornado.api.TornadoExecutionPlan
 * @see java.awt.image.BufferedImage
 * @see uk.ac.manchester.tornado.api.types.matrix.Matrix2DInt
 */

public class SobelTornado {
    private static final Logger logger = LoggerFactory.getLogger(SobelTornado.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws IllegalStateException always, as this is a utility class
     */
    private SobelTornado() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

    /**
     * Converts an input image to an edge-detected version using the Sobel operator with TornadoVM acceleration.
     * The method processes the image in the following steps:
     * <ol>
     *     <li>Converts the input image to a matrix</li>
     *     <li>Computes edge colors using Sobel operators</li>
     *     <li>Normalizes the results using the maximum gradient</li>
     *     <li>Converts the processed matrix back to an image</li>
     * </ol>
     *
     * @param image the input BufferedImage to process
     * @return a new BufferedImage containing the edge-detected version of the input image
     */
    public static BufferedImage convert(BufferedImage image) {
        Matrix2DInt inputImageMatrix = convertImageToMatrix(image);
        Matrix2DInt edgeImageMatrix = computeEdgeColors(inputImageMatrix);
        Matrix2DInt outputImageMatrix = new Matrix2DInt(edgeImageMatrix.getNumRows(), edgeImageMatrix.getNumColumns());

        int maxGradient = findMaxGradient(edgeImageMatrix);

        //@formatter:off
        TaskGraph taskGraph = new TaskGraph("graph")
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, maxGradient, edgeImageMatrix, outputImageMatrix)
                .task("norm", SobelTornado::norm, maxGradient, edgeImageMatrix, outputImageMatrix)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, outputImageMatrix);
        //@formatter:on

        try (TornadoExecutionPlan tornadoExecutionPlan = new TornadoExecutionPlan(taskGraph.snapshot())) {
            TornadoDevice device = TornadoExecutionPlan.getDevice(0, 1);
            tornadoExecutionPlan.withProfiler(ProfilerMode.SILENT).withDevice(device).execute();
        } catch (TornadoExecutionPlanException e) {
            logger.error("Tornado execution error:", e);
        }

        return convertMatrixToImage(outputImageMatrix, image.getType());
    }

    /**
     * Converts a BufferedImage to a Matrix2DInt representation.
     *
     * @param image the input BufferedImage to convert
     * @return a Matrix2DInt containing the image data
     */
    private static Matrix2DInt convertImageToMatrix(BufferedImage image) {
        Matrix2DInt imageMatrix = new Matrix2DInt(image.getWidth(), image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                imageMatrix.set(x, y, image.getRGB(x, y));
            }
        }

        return imageMatrix;
    }

    /**
     * Converts a Matrix2DInt back to a BufferedImage.
     *
     * @param imageMatrix the matrix to convert
     * @param imageType   the type of the output image
     * @return a new BufferedImage representing the matrix data
     */
    private static BufferedImage convertMatrixToImage(Matrix2DInt imageMatrix, int imageType) {
        BufferedImage image = new BufferedImage(imageMatrix.getNumRows(), imageMatrix.getNumColumns(), imageType);
        for (int x = 0; x < imageMatrix.getNumRows(); x++) {
            for (int y = 0; y < imageMatrix.getNumColumns(); y++) {
                image.setRGB(x, y, imageMatrix.get(x, y));
            }
        }

        return image;
    }

    /**
     * Computes edge colors using Sobel operators.
     * This method applies both horizontal and vertical Sobel operators to detect edges.
     *
     * @param input the input image matrix
     * @return a new matrix containing the computed edge values
     */
    private static Matrix2DInt computeEdgeColors(Matrix2DInt input) {
        Matrix2DInt output = new Matrix2DInt(input.getNumRows(), input.getNumColumns());

        Matrix2DInt valMatrix = new Matrix2DInt(3, 3);
        Matrix2DInt sobelXMatrix = Filters.getTornadoSobelXMatrix();
        Matrix2DInt sobelYMatrix = Filters.getTornadoSobelYMatrix();
        Matrix2DInt.transpose(sobelYMatrix);

        for (int i = 1; i < input.getNumRows() - 1; i++) {
            for (int j = 1; j < input.getNumColumns() - 1; j++) {
                valMatrix.set(0, 0, convertRGBToGrayScale(input.get(i - 1, j - 1)));
                valMatrix.set(0, 1, convertRGBToGrayScale(input.get(i - 1, j)));
                valMatrix.set(0, 2, convertRGBToGrayScale(input.get(i - 1, j + 1)));

                valMatrix.set(0, 0, convertRGBToGrayScale(input.get(i, j - 1)));
                valMatrix.set(0, 1, convertRGBToGrayScale(input.get(i, j)));
                valMatrix.set(0, 2, convertRGBToGrayScale(input.get(i, j + 1)));

                valMatrix.set(2, 0, convertRGBToGrayScale(input.get(i + 1, j - 1)));
                valMatrix.set(2, 1, convertRGBToGrayScale(input.get(i + 1, j)));
                valMatrix.set(2, 2, convertRGBToGrayScale(input.get(i + 1, j + 1)));

                int gx = mask(sobelXMatrix, valMatrix);
                int gy = mask(sobelYMatrix, valMatrix);

                float gval = TornadoMath.sqrt((float) (gx * gx) + (gy * gy));
                int g = (int) gval;
                output.set(i, j, g);
            }
        }

        return output;
    }

    /**
     * Applies a mask operation between two matrices.
     *
     * @param sobelMatrix the Sobel operator matrix
     * @param valMatrix the values matrix
     * @return the result of the mask operation
     */
    private static int mask(Matrix2DInt sobelMatrix, Matrix2DInt valMatrix) {
        int gval = 0;
        for (int k = 0; k < sobelMatrix.getNumRows(); k++) {
            for (int l = 0; l < sobelMatrix.getNumColumns(); l++) {
                gval += sobelMatrix.get(k, l) * valMatrix.get(k, l);
            }
        }
        return gval;
    }

    /**
     * Normalizes the edge values and converts them to RGB colors.
     * This method is annotated for parallel execution with TornadoVM.
     *
     * @param maxGradient the maximum gradient value for normalization
     * @param input the input matrix containing edge values
     * @param output the output matrix for normalized RGB values
     */
    private static void norm(int maxGradient, Matrix2DInt input, Matrix2DInt output) {
        double scale = 255.0 / maxGradient;

        for (@Parallel int i = 1; i < input.getNumRows() - 1; i++) {
            for (@Parallel int j = 1; j < input.getNumColumns() - 1; j++) {
                int edgeColor = input.get(i, j);
                edgeColor = (int) (edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                output.set(i, j, edgeColor);
            }
        }
    }

    /**
     * Finds the maximum gradient value in the edge matrix.
     *
     * @param imageMatrix the matrix containing edge values
     * @return the maximum gradient value found
     */
    private static int findMaxGradient(Matrix2DInt imageMatrix) {
        int max = -1;
        for (int i = 1; i < imageMatrix.getNumRows() - 1; i++) {
            for (int j = 1; j < imageMatrix.getNumColumns() - 1; j++) {
                if (max < imageMatrix.get(i, j)) {
                    max = imageMatrix.get(i, j);
                }
            }
        }
        return max;
    }

    /**
     * Converts an RGB color value to its grayscale equivalent using the luminance formula.
     * The formula used is: 0.2126R + 0.7152G + 0.0722B
     *
     * @param rgbValue the RGB color value to convert
     * @return the grayscale value
     */
    private static int convertRGBToGrayScale(int rgbValue) {
        Color c = new Color(rgbValue);
        // Convert gradient value to color
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        // Linear luminance representation of RGB
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }
}
