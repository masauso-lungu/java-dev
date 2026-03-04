package graphics;

import core.FileBindings;
import core.Helper;
import enums.SamplingType;
import enums.TransformType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jpeg.Process;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    @FXML
    Button buttonInverseQuantize;
    @FXML
    Button buttonInverseToRGB;
    @FXML
    Button buttonInverseSample;
    @FXML
    Button buttonInverseTransform;
    @FXML
    Button buttonQuantize;
    @FXML
    Button buttonSample;
    @FXML
    Button buttonToYCbCr;
    @FXML
    Button buttonTransform;

    @FXML
    TextField qualityMSE;
    @FXML
    TextField qualityPSNR;

    @FXML
    Slider quantizeQuality;
    @FXML
    TextField quantizeQualityField;

    @FXML
    CheckBox shadesOfGrey;
    @FXML
    CheckBox showSteps;

    @FXML
    Spinner<Integer> transformBlock;
    @FXML
    ComboBox<TransformType> transformType;
    @FXML
    ComboBox<SamplingType> sampling;

    // Process instance for image operations // Ex2
    private Process process;

    /**
     * Inicializace okna, nastavení výchozích hodnot. Naplnění prvků v rozhraní.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nastavení všech hodnot do combo boxů
        sampling.getItems().setAll(SamplingType.values());
        transformType.getItems().setAll(TransformType.values());

        // Nastavení výchozích hodnot
        sampling.getSelectionModel().select(SamplingType.S_4_4_4);
        transformType.getSelectionModel().select(TransformType.DCT);
        quantizeQuality.setValue(50);

        // Vytvoření listu možností, které budou uvnitř spinneru
        ObservableList<Integer> blocks = FXCollections.observableArrayList(2, 4, 8, 16, 32, 64, 128, 256, 512);
        SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocks);
        spinnerValues.setValue(8);
        transformBlock.setValueFactory(spinnerValues);

        // Nastavení formátu čísel v textových polích, aby bylo možné zadávat pouze čísla. Plus metoda, která je na konci souboru.
        quantizeQualityField.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        // Propojení slideru s textovým polem
        quantizeQualityField.textProperty().bindBidirectional(quantizeQuality.valueProperty(), NumberFormat.getIntegerInstance());

        // Load the default image // Ex2
        process = new Process(FileBindings.defaultImage);
    }

    public void close() {
        Stage stage = ((Stage) buttonSample.getScene().getWindow());
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void closeWindows() {
        Dialogs.closeAllWindows();
    }

    public void showOriginal() {
        File f = new File(FileBindings.defaultImage);

        try {
            Dialogs.showImageInWindow(ImageIO.read(f), "Original", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    public void showOriginal() {
        if (process != null) {
            Dialogs.showImageInWindow(process.getOriginalImage(), "Original", true);
        }
    }
    */
    public void changeImage() { // Ex2
        File f = Dialogs.openFile();
        if (f != null) {
            process = new Process(f.getAbsolutePath());
            Dialogs.closeImageWindows();
        }
    }

    public void reset() { // Ex2
        process = new Process(FileBindings.defaultImage);
        Dialogs.closeImageWindows();
    }

    public void showRGBModified() { // Ex2
        if (process != null && process.getModifiedR() != null) {
            BufferedImage img = Process.getImageFromRGB(process.getModifiedR(), process.getModifiedG(), process.getModifiedB());
            Dialogs.showImageInWindow(img, "Modified RGB");
        }
    }

    public void convertToRGB() { // Ex2
        if (process != null && process.getModifiedY() != null) {
            process.convertToRGB();

            if (showSteps.isSelected()) {
                showRGBModified();
            }
        }
    }

    public void convertToYCbCr() { // Ex2
        if (process != null) {
            process.convertToYCbCr();

            if (showSteps.isSelected()) {
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getOriginalY()), "Y (Original)");
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getOriginalCb()), "Cb (Original)");
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getOriginalCr()), "Cr (Original)");
            }
        }
    }

    public void sample() {

    }

    public void inverseSample() {

    }

    public void transform() {

    }

    public void inverseTransform() {

    }

    public void quantize() {

    }

    public void inverseQuantize() {

    }

    public void countQuality() {

    }

    // --- Display individual color channels --- // Ex2

    public void showBlueModified() { // Ex2
        if (process != null && process.getModifiedB() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getModifiedB(), Color.BLUE, shadesOfGrey.isSelected()),
                    "Blue (Modified)");
        }
    }

    public void showBlueOriginal() { // Ex2
        if (process != null && process.getOriginalB() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getOriginalB(), Color.BLUE, shadesOfGrey.isSelected()),
                    "Blue (Original)");
        }
    }

    public void showCbModified() { // Ex2
        if (process != null && process.getModifiedCb() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getModifiedCb()),
                    "Cb (Modified)");
        }
    }

    public void showCbOriginal() { // Ex2
        if (process != null && process.getOriginalCb() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getOriginalCb()),
                    "Cb (Original)");
        }
    }

    public void showCrModified() { // Ex2
        if (process != null && process.getModifiedCr() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getModifiedCr()),
                    "Cr (Modified)");
        }
    }

    public void showCrOriginal() { // Ex2
        if (process != null && process.getOriginalCr() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getOriginalCr()),
                    "Cr (Original)");
        }
    }

    public void showGreenModified() { // Ex2
        if (process != null && process.getModifiedG() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getModifiedG(), Color.GREEN, shadesOfGrey.isSelected()),
                    "Green (Modified)");
        }
    }

    public void showGreenOriginal() { // Ex2
        if (process != null && process.getOriginalG() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getOriginalG(), Color.GREEN, shadesOfGrey.isSelected()),
                    "Green (Original)");
        }
    }

    public void showRedModified() { // Ex2
        if (process != null && process.getModifiedR() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getModifiedR(), Color.RED, shadesOfGrey.isSelected()),
                    "Red (Modified)");
        }
    }

    public void showRedOriginal() { // Ex2
        if (process != null && process.getOriginalR() != null) {
            Dialogs.showImageInWindow(
                    Process.showOneColorImageFromRGB(process.getOriginalR(), Color.RED, shadesOfGrey.isSelected()),
                    "Red (Original)");
        }
    }

    public void showYModified() { // Ex2
        if (process != null && process.getModifiedY() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getModifiedY()),
                    "Y (Modified)");
        }
    }

    public void showYOriginal() { // Ex2
        if (process != null && process.getOriginalY() != null) {
            Dialogs.showImageInWindow(
                    Process.getGrayscaleImageFromMatrix(process.getOriginalY()),
                    "Y (Original)");
        }
    }
}
