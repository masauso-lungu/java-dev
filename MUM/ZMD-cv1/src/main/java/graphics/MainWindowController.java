package graphics;

import Jama.Matrix;
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
import jpeg.Quality;

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
    TextField qualityMAE; // Ex4
    @FXML
    TextField qualityPSNR;
    @FXML
    TextField qualitySAE; // Ex4

    @FXML
    ComboBox<String> qualityChannel; // Ex4

    @FXML
    TextField qualitySSIM; // Ex4
    @FXML
    TextField qualityMSSIM; // Ex4
    @FXML
    ComboBox<String> ssimChannel; // Ex4

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

        // Populate quality channel selector // Ex4
        qualityChannel.getItems().addAll("RGB", "Red", "Green", "Blue", "Y", "Cb", "Cr");
        qualityChannel.getSelectionModel().select("RGB");

        // Populate SSIM channel selector (Y, Cb, Cr only) // Ex4
        ssimChannel.getItems().addAll("Y", "Cb", "Cr");
        ssimChannel.getSelectionModel().select("Y");
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

    public void sample() { // Ex3
        if (process != null && process.getModifiedCb() != null) {
            process.downSample(sampling.getValue());

            if (showSteps.isSelected()) {
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getModifiedCb()), "Cb (Downsampled)");
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getModifiedCr()), "Cr (Downsampled)");
            }
        }
    }

    public void inverseSample() { // Ex3
        if (process != null && process.getModifiedCb() != null) {
            process.overSample(sampling.getValue());

            if (showSteps.isSelected()) {
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getModifiedCb()), "Cb (Upsampled)");
                Dialogs.showImageInWindow(Process.getGrayscaleImageFromMatrix(process.getModifiedCr()), "Cr (Upsampled)");
            }
        }
    }

    public void transform() { // Ex5
        if (process != null && process.getModifiedY() != null) {
            process.transform(transformType.getValue(), transformBlock.getValue());
        }
    }

    public void inverseTransform() { // Ex5
        if (process != null && process.getModifiedY() != null) {
            process.inverseTransform(transformType.getValue(), transformBlock.getValue());
        }
    }

    public void quantize() { // Ex6
        if (process != null && process.getModifiedY() != null) {
            process.quantize(transformBlock.getValue(), quantizeQuality.getValue());
        }
    }

    public void inverseQuantize() { // Ex6
        if (process != null && process.getModifiedY() != null) {
            process.inverseQuantize(transformBlock.getValue(), quantizeQuality.getValue());
        }
    }

    public void countQuality() { // Ex4
        if (process == null) return;

        String channel = qualityChannel.getValue();
        double[][] orig = null;
        double[][] mod = null;

        // Get the data for the selected channel
        switch (channel) {
            case "Red":
                if (process.getOriginalR() != null && process.getModifiedR() != null) {
                    orig = Quality.convertIntToDouble(process.getOriginalR());
                    mod = Quality.convertIntToDouble(process.getModifiedR());
                }
                break;
            case "Green":
                if (process.getOriginalG() != null && process.getModifiedG() != null) {
                    orig = Quality.convertIntToDouble(process.getOriginalG());
                    mod = Quality.convertIntToDouble(process.getModifiedG());
                }
                break;
            case "Blue":
                if (process.getOriginalB() != null && process.getModifiedB() != null) {
                    orig = Quality.convertIntToDouble(process.getOriginalB());
                    mod = Quality.convertIntToDouble(process.getModifiedB());
                }
                break;
            case "Y":
                if (process.getOriginalY() != null && process.getModifiedY() != null) {
                    orig = process.getOriginalY().getArray();
                    mod = process.getModifiedY().getArray();
                }
                break;
            case "Cb":
                if (process.getOriginalCb() != null && process.getModifiedCb() != null) {
                    orig = process.getOriginalCb().getArray();
                    mod = process.getModifiedCb().getArray();
                }
                break;
            case "Cr":
                if (process.getOriginalCr() != null && process.getModifiedCr() != null) {
                    orig = process.getOriginalCr().getArray();
                    mod = process.getModifiedCr().getArray();
                }
                break;
            case "RGB":
            default:
                if (process.getOriginalR() != null && process.getModifiedR() != null) {
                    // Average MSE of all 3 channels
                    double[][] origR = Quality.convertIntToDouble(process.getOriginalR());
                    double[][] origG = Quality.convertIntToDouble(process.getOriginalG());
                    double[][] origB = Quality.convertIntToDouble(process.getOriginalB());
                    double[][] modR = Quality.convertIntToDouble(process.getModifiedR());
                    double[][] modG = Quality.convertIntToDouble(process.getModifiedG());
                    double[][] modB = Quality.convertIntToDouble(process.getModifiedB());

                    double mseR = Quality.countMSE(origR, modR);
                    double mseG = Quality.countMSE(origG, modG);
                    double mseB = Quality.countMSE(origB, modB);
                    double mseAvg = (mseR + mseG + mseB) / 3.0;

                    double maeR = Quality.countMAE(origR, modR);
                    double maeG = Quality.countMAE(origG, modG);
                    double maeB = Quality.countMAE(origB, modB);
                    double maeAvg = (maeR + maeG + maeB) / 3.0;

                    double saeR = Quality.countSAE(origR, modR);
                    double saeG = Quality.countSAE(origG, modG);
                    double saeB = Quality.countSAE(origB, modB);
                    double saeAvg = (saeR + saeG + saeB) / 3.0;

                    qualityMSE.setText(String.format("%.4f", mseAvg));
                    qualityMAE.setText(String.format("%.4f", maeAvg));
                    qualityPSNR.setText(String.format("%.4f", Quality.countPSNR(mseAvg)));
                    qualitySAE.setText(String.format("%.4f", saeAvg));
                    return;
                }
                break;
        }

        // For single channel calculations
        if (orig != null && mod != null) {
            double mse = Quality.countMSE(orig, mod);
            double mae = Quality.countMAE(orig, mod);
            double sae = Quality.countSAE(orig, mod);
            double psnr = Quality.countPSNR(mse);

            qualityMSE.setText(String.format("%.4f", mse));
            qualityMAE.setText(String.format("%.4f", mae));
            qualityPSNR.setText(String.format("%.4f", psnr));
            qualitySAE.setText(String.format("%.4f", sae));
        }
    }

    /**
     * Calculates SSIM and MSSIM for the selected YCbCr channel.
     */
    public void countSSIM() { // Ex4
        if (process == null) return;

        String channel = ssimChannel.getValue();
        Matrix orig = null;
        Matrix mod = null;

        switch (channel) {
            case "Y":
                orig = process.getOriginalY();
                mod = process.getModifiedY();
                break;
            case "Cb":
                orig = process.getOriginalCb();
                mod = process.getModifiedCb();
                break;
            case "Cr":
                orig = process.getOriginalCr();
                mod = process.getModifiedCr();
                break;
        }

        if (orig != null && mod != null) {
            double ssim = Quality.countSSIM(orig, mod);
            double mssim = Quality.countMSSIM(orig, mod);
            qualitySSIM.setText(String.format("%.6f", ssim));
            qualityMSSIM.setText(String.format("%.6f", mssim));
        }
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
