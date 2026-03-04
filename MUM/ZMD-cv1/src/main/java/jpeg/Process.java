package jpeg;

import Jama.Matrix;
import core.FileBindings;
import core.Helper;
import graphics.Dialogs;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class that handles all image processing operations.
 */
public class Process {

    private BufferedImage originalImage; // Ex2

    // Original RGB channels extracted from the image // Ex2
    private int[][] originalR, originalG, originalB;

    // Modified RGB channels (result of YCbCr -> RGB conversion) // Ex2
    private int[][] modifiedR, modifiedG, modifiedB;

    // Original YCbCr channels (result of RGB -> YCbCr conversion) // Ex2
    private Matrix originalY, originalCb, originalCr;

    // Modified YCbCr channels (can be changed by later steps like sampling) // Ex2
    private Matrix modifiedY, modifiedCb, modifiedCr;

    /**
     * Constructor - loads image and extracts RGB channels.
     */
    public Process(String imagePath) { // Ex2
        this.originalImage = Dialogs.loadImageFromPath(imagePath);
        setOriginalRGB();
    }

    /**
     * Extracts R, G, B values from each pixel of the original image.
     */
    private void setOriginalRGB() { // Ex2
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        originalR = new int[height][width];
        originalG = new int[height][width];
        originalB = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = originalImage.getRGB(x, y);

                // Extract each color from the pixel using bit shifting
                // pixel is stored as: 0xAARRGGBB
                originalR[y][x] = (pixel >> 16) & 0xFF;
                originalG[y][x] = (pixel >> 8) & 0xFF;
                originalB[y][x] = pixel & 0xFF;
            }
        }
    }

    /**
     * Creates a BufferedImage from R, G, B arrays.
     */
    public static BufferedImage getImageFromRGB(int[][] red, int[][] green, int[][] blue) { // Ex2
        int height = red.length;
        int width = red[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = Helper.checkValue(red[y][x]);
                int g = Helper.checkValue(green[y][x]);
                int b = Helper.checkValue(blue[y][x]);

                // Combine R, G, B into one pixel value: 0xRRGGBB
                int pixel = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, pixel);
            }
        }

        return image;
    }

    /**
     * Converts original RGB to YCbCr.
     */
    public void convertToYCbCr() { // Ex2
        Matrix[] result = ColorTransform.convertOriginalRGBtoYcBcR(originalR, originalG, originalB);

        originalY = result[0];
        originalCb = result[1];
        originalCr = result[2];

        // Copy to modified (these will be changed by later processing steps)
        modifiedY = new Matrix(result[0].getArrayCopy());
        modifiedCb = new Matrix(result[1].getArrayCopy());
        modifiedCr = new Matrix(result[2].getArrayCopy());
    }

    /**
     * Converts modified YCbCr back to RGB.
     */
    public void convertToRGB() { // Ex2
        int[][][] result = ColorTransform.convertModifiedYcBcRtoRGB(modifiedY, modifiedCb, modifiedCr);

        modifiedR = result[0];
        modifiedG = result[1];
        modifiedB = result[2];
    }

    /**
     * Creates an image showing just one color channel.
     * If grayscale is true, shows as gray. Otherwise shows in the given color.
     */
    public static BufferedImage showOneColorImageFromRGB(int[][] channel, Color color, boolean grayscale) { // Ex2
        int height = channel.length;
        int width = channel[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = Helper.checkValue(channel[y][x]);

                int pixel;
                if (grayscale) {
                    // Same value in all three channels = gray
                    pixel = (val << 16) | (val << 8) | val;
                } else if (color.equals(Color.RED)) {
                    pixel = (val << 16);
                } else if (color.equals(Color.GREEN)) {
                    pixel = (val << 8);
                } else {
                    pixel = val;
                }
                image.setRGB(x, y, pixel);
            }
        }

        return image;
    }

    /**
     * Creates a grayscale image from a Matrix (used for Y, Cb, Cr channels).
     */
    public static BufferedImage getGrayscaleImageFromMatrix(Matrix matrix) { // Ex2
        int height = matrix.getRowDimension();
        int width = matrix.getColumnDimension();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = Helper.checkValue(matrix.get(y, x));
                int pixel = (val << 16) | (val << 8) | val;
                image.setRGB(x, y, pixel);
            }
        }

        return image;
    }

    // --- Getters --- // Ex2

    public BufferedImage getOriginalImage() { return originalImage; }

    public int[][] getOriginalR() { return originalR; }
    public int[][] getOriginalG() { return originalG; }
    public int[][] getOriginalB() { return originalB; }

    public int[][] getModifiedR() { return modifiedR; }
    public int[][] getModifiedG() { return modifiedG; }
    public int[][] getModifiedB() { return modifiedB; }

    public Matrix getOriginalY() { return originalY; }
    public Matrix getOriginalCb() { return originalCb; }
    public Matrix getOriginalCr() { return originalCr; }

    public Matrix getModifiedY() { return modifiedY; }
    public Matrix getModifiedCb() { return modifiedCb; }
    public Matrix getModifiedCr() { return modifiedCr; }
}
