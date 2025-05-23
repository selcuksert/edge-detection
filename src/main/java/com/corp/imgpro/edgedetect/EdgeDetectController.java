package com.corp.imgpro.edgedetect;

import com.corp.imgpro.edgedetect.method.SobelStandard;
import com.corp.imgpro.edgedetect.method.SobelTornado;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

/**
 * Controller class for the edge detection application's user interface.
 * This class manages the interaction between the UI components and the edge detection
 * processing logic, handling image selection, processing, and result visualization.
 * It supports two edge detection methods: standard Sobel and TornadoVM-accelerated Sobel.
 *
 * <p>The controller provides functionality to:</p>
 * <ul>
 *     <li>Select and load input images</li>
 *     <li>Process images using both standard and TornadoVM-based Sobel edge detection</li>
 *     <li>Display the original and processed images</li>
 *     <li>Show performance comparison between the two processing methods</li>
 * </ul>
 */
public class EdgeDetectController {
    /**
     * Logger instance for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(EdgeDetectController.class);

    /**
     * Separator for visual organization of the top section
     */
    @FXML
    public Separator topSeperator;

    /**
     * Separator for visual organization of the bottom section
     */
    @FXML
    public Separator bottomSeperator;

    /**
     * Text display for the selected image information
     */
    @FXML
    private Text selectedImageTxt;

    /**
     * Text display for the standard edge detection result information
     */
    @FXML
    private Text stdEdgeDetectImageTxt;

    /**
     * Text display for the TornadoVM edge detection result information
     */
    @FXML
    private Text tornadoEdgeDetectImageTxt;

    /**
     * Button for initiating image selection
     */
    @FXML
    private Button selectImageBtn;

    /**
     * ImageView for displaying the selected input image
     */
    @FXML
    private ImageView selectedImageView;

    /**
     * ImageView for displaying the standard Sobel edge detection result
     */
    @FXML
    private ImageView stdEdgeDetectImageView;

    /**
     * ImageView for displaying the TornadoVM Sobel edge detection result
     */
    @FXML
    private ImageView tornadoEdgeDetectImageView;

    /**
     * Bar chart for displaying performance comparison between methods
     */
    @FXML
    private BarChart<String, Number> perfBarChart;

    private final XYChart.Series<String, Number> seriesStandard = new XYChart.Series<>();

    private final XYChart.Series<String, Number> seriesTornado = new XYChart.Series<>();


    /**
     * Handles the image processing workflow when triggered by user interaction.
     * This method performs the following steps:
     * <ol>
     *     <li>Updates UI state to indicate processing</li>
     *     <li>Opens file chooser for image selection</li>
     *     <li>Processes the selected image using both standard and TornadoVM Sobel methods</li>
     *     <li>Measures and displays performance metrics</li>
     *     <li>Updates the UI with processed images and performance data</li>
     *     <li>Adjusts image display sizes to fit the window</li>
     * </ol>
     */
    @FXML
    protected void process() {
        try {
            seriesStandard.setName("Standard");
            seriesTornado.setName("TornadoVM");
            selectImageBtn.setDisable(true);
            selectImageBtn.setText("Processing... Please wait...");
            selectedImageTxt.setVisible(false);
            stdEdgeDetectImageTxt.setVisible(false);
            tornadoEdgeDetectImageTxt.setVisible(false);
            perfBarChart.setVisible(false);
            topSeperator.setVisible(false);
            bottomSeperator.setVisible(false);
            FileChooser fileChooser = new FileChooser();
            Scene scene = selectImageBtn.getScene();
            Window window = scene.getWindow();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            File selectedFile = fileChooser.showOpenDialog(window);
            assert selectedFile != null;
            URL imagePath = selectedFile.toURI().toURL();
            DecimalFormat df = new DecimalFormat("#");
            selectedImageView.setImage(new Image(imagePath.toURI().toString()));
            File imageFile = new File(imagePath.toURI());
            BufferedImage image = ImageIO.read(imageFile);
            double selectedImageWidth = selectedImageView.getImage().getWidth();
            double selectedImageHeight = selectedImageView.getImage().getHeight();
            String imageDim = String.format("%s x %s", df.format(selectedImageWidth),
                    df.format(selectedImageHeight));

            Instant start = Instant.now();
            BufferedImage convertedImage = SobelStandard.convert(image);
            Instant finish = Instant.now();
            long standardTimeElapsed = Duration.between(start, finish).toMillis();
            logger.info("[Standard] Execution time (msecs): {}", standardTimeElapsed);
            stdEdgeDetectImageView.setImage(SwingFXUtils.toFXImage(convertedImage, null));
            seriesStandard.getData().add(createData(imageDim, standardTimeElapsed));

            Instant startTornado = Instant.now();
            convertedImage = SobelTornado.convert(image);
            Instant finishTornado = Instant.now();
            long tornadoTimeElapsed = Duration.between(startTornado, finishTornado).toMillis();
            logger.info("[TornadoVM] Execution time (msecs): {}", tornadoTimeElapsed);
            tornadoEdgeDetectImageView.setImage(SwingFXUtils.toFXImage(convertedImage, null));
            selectedImageTxt.setVisible(true);
            stdEdgeDetectImageTxt.setVisible(true);
            tornadoEdgeDetectImageTxt.setVisible(true);
            seriesTornado.getData().add(createData(imageDim, tornadoTimeElapsed));

            perfBarChart.getData().clear();
            perfBarChart.getData().add(seriesStandard);
            perfBarChart.getData().add(seriesTornado);

            // To keep the 3 x 2 grid
            int gridWidth = 3;
            int gridHeight = 2;

            if (selectedImageHeight > sceneHeight / gridHeight) {
                selectedImageView.setFitHeight(sceneHeight / gridHeight);
                stdEdgeDetectImageView.setFitHeight(sceneHeight / gridHeight);
                tornadoEdgeDetectImageView.setFitHeight(sceneHeight / gridHeight);
            }

            if (selectedImageWidth > sceneWidth / gridWidth) {
                selectedImageView.setFitWidth(sceneWidth / gridWidth);
                stdEdgeDetectImageView.setFitWidth(sceneWidth / gridWidth);
                tornadoEdgeDetectImageView.setFitWidth(sceneWidth / gridWidth);
            }

            topSeperator.setVisible(true);
            bottomSeperator.setVisible(true);
        } catch (IOException | URISyntaxException e) {
            logger.error("ERROR:", e);
        } finally {
            selectImageBtn.setDisable(false);
            selectImageBtn.setText("Select Image");
            perfBarChart.setVisible(true);
        }
    }

    /**
     * Creates a data point for the performance bar chart with a labeled value.
     * This helper method generates a styled chart data point that includes:
     * <ul>
     *     <li>The dimensions of the processed image</li>
     *     <li>The execution time value</li>
     *     <li>A visual label showing the execution time above the bar</li>
     * </ul>
     *
     * @param imageDim the dimensions of the processed image
     * @param execTime the execution time in milliseconds
     * @return a configured XYChart.Data object ready for display
     */
    private XYChart.Data<String, Number> createData(String imageDim, long execTime) {
        XYChart.Data<String, Number> data = new XYChart.Data<>(imageDim, execTime);

        String text = String.valueOf(execTime);

        StackPane node = new StackPane();
        Label label = new Label(text);
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        StackPane.setMargin(group, new Insets(-15, 0, 0, 0));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }
}