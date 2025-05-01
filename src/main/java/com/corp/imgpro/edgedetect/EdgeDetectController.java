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

public class EdgeDetectController {
    private static final Logger logger = LoggerFactory.getLogger(EdgeDetectController.class);

    @FXML
    public Separator topSeperator;

    @FXML
    public Separator bottomSeperator;

    @FXML
    private Text selectedImageTxt;

    @FXML
    private Text stdEdgeDetectImageTxt;

    @FXML
    private Text tornadoEdgeDetectImageTxt;

    @FXML
    private Button selectImageBtn;

    @FXML
    private ImageView selectedImageView;

    @FXML
    private ImageView stdEdgeDetectImageView;

    @FXML
    private ImageView tornadoEdgeDetectImageView;

    @FXML
    private BarChart<String, Number> perfBarChart;

    @FXML
    protected void process() {
        try {
            selectImageBtn.setDisable(true);
            selectImageBtn.setText("Processing... Please wait...");
            selectedImageTxt.setVisible(false);
            stdEdgeDetectImageTxt.setVisible(false);
            tornadoEdgeDetectImageTxt.setVisible(false);
            perfBarChart.setVisible(false);
            topSeperator.setVisible(false);
            bottomSeperator.setVisible(false);
            FileChooser fileChooser = new FileChooser();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
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
            series.setName(String.format("%s x %s", df.format(selectedImageWidth),
                    df.format(selectedImageHeight)));

            Instant start = Instant.now();
            BufferedImage convertedImage = SobelStandard.convert(image);
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            logger.debug("[Standard] Execution time (msecs): {}", timeElapsed);
            stdEdgeDetectImageView.setImage(SwingFXUtils.toFXImage(convertedImage, null));
            series.getData().add(createData("Standard", timeElapsed));

            Instant startTornado = Instant.now();
            convertedImage = SobelTornado.convert(image);
            Instant finishTornado = Instant.now();
            long tornadoTimeElapsed = Duration.between(startTornado, finishTornado).toMillis();
            logger.info("[TornadoVM] Execution time (msecs): {}", tornadoTimeElapsed);
            tornadoEdgeDetectImageView.setImage(SwingFXUtils.toFXImage(convertedImage, null));
            selectedImageTxt.setVisible(true);
            stdEdgeDetectImageTxt.setVisible(true);
            tornadoEdgeDetectImageTxt.setVisible(true);
            series.getData().add(createData("TornadoVM", tornadoTimeElapsed));
            perfBarChart.getData().add(series);

            if (selectedImageHeight > sceneHeight / 2) {
                selectedImageView.setFitHeight(sceneHeight / 2);
                stdEdgeDetectImageView.setFitHeight(sceneHeight / 2);
                tornadoEdgeDetectImageView.setFitHeight(sceneHeight / 2);
            }

            if (selectedImageWidth > sceneWidth / 3) {
                selectedImageView.setFitWidth(sceneWidth / 3);
                stdEdgeDetectImageView.setFitWidth(sceneWidth / 3);
                tornadoEdgeDetectImageView.setFitWidth(sceneWidth / 3);
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

    private XYChart.Data<String, Number> createData(String method, long execTime) {
        XYChart.Data<String, Number> data = new XYChart.Data<>(method, execTime);

        String text = String.valueOf(execTime);

        StackPane node = new StackPane();
        Label label = new Label(text);
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.TOP_CENTER);
        StackPane.setMargin(group, new Insets(-20, 0, 0, 0));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }
}