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

public class SobelTornado {
    private static final Logger logger = LoggerFactory.getLogger(SobelTornado.class);

    private SobelTornado() {
        throw new IllegalStateException("%s is a utility class and cannot be instantiated!".formatted(this.getClass().getName()));
    }

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

    private static Matrix2DInt convertImageToMatrix(BufferedImage image) {
        Matrix2DInt imageMatrix = new Matrix2DInt(image.getWidth(), image.getHeight());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                imageMatrix.set(x, y, image.getRGB(x, y));
            }
        }

        return imageMatrix;
    }

    private static BufferedImage convertMatrixToImage(Matrix2DInt imageMatrix, int imageType) {
        BufferedImage image = new BufferedImage(imageMatrix.getNumRows(), imageMatrix.getNumColumns(), imageType);
        for (int x = 0; x < imageMatrix.getNumRows(); x++) {
            for (int y = 0; y < imageMatrix.getNumColumns(); y++) {
                image.setRGB(x, y, imageMatrix.get(x, y));
            }
        }

        return image;
    }

    private static Matrix2DInt computeEdgeColors(Matrix2DInt input) {
        Matrix2DInt output = new Matrix2DInt(input.getNumRows(), input.getNumColumns());

        Matrix2DInt valMatrix = new Matrix2DInt(3, 3);
        Matrix2DInt sobelMatrix = Filters.getTornadoSobelMatrix();

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

                int gx = mask(sobelMatrix, valMatrix);
                int gy = mask(sobelMatrix, valMatrix);

                float gval = TornadoMath.sqrt((float) (gx * gx) + (gy * gy));
                int g = (int) gval;
                output.set(i, j, g);
            }
        }

        return output;
    }

    private static int mask(Matrix2DInt sobelMatrix, Matrix2DInt valMatrix) {
        int gval = 0;
        for (int k = 0; k < sobelMatrix.getNumRows(); k++) {
            for (int l = 0; l < sobelMatrix.getNumColumns(); l++) {
                gval += sobelMatrix.get(k, l) * valMatrix.get(k, l);
            }
        }
        Matrix2DInt.transpose(sobelMatrix);
        return gval;
    }

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
