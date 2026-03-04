package jpeg;

import Jama.Matrix;
import core.Helper;

/**
 * Class for color model transformations.
 * Implements conversion between RGB and YCbCr color spaces
 * using SDTV (ITU-R BT.601) formulas.
 */
public class ColorTransform {

    /**
     * Converts RGB arrays to YCbCr matrices using SDTV (BT.601) formulas.
     */
    public static Matrix[] convertOriginalRGBtoYcBcR(int[][] red, int[][] green, int[][] blue) { // Ex2
        int height = red.length;
        int width = red[0].length;

        // Create arrays to store Y, Cb, Cr values
        double[][] y = new double[height][width];
        double[][] cb = new double[height][width];
        double[][] cr = new double[height][width];

        // Go through every pixel and apply the formula
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int r = red[j][i];
                int g = green[j][i];
                int b = blue[j][i];

                y[j][i]  =  0.257 * r + 0.504 * g + 0.098 * b + 16;
                cb[j][i] = -0.148 * r - 0.291 * g + 0.439 * b + 128;
                cr[j][i] =  0.439 * r - 0.368 * g - 0.071 * b + 128;
            }
        }

        // Wrap arrays into Jama Matrix objects and return
        Matrix convertedY = new Matrix(y);
        Matrix convertedCb = new Matrix(cb);
        Matrix convertedCr = new Matrix(cr);

        return new Matrix[]{convertedY, convertedCb, convertedCr};
    }

    /**
     * Converts YCbCr matrices back to RGB arrays using inverse SDTV (BT.601) formulas.
     * Values are clamped to 0-255 range.
     */
    public static int[][][] convertModifiedYcBcRtoRGB(Matrix Y, Matrix Cb, Matrix Cr) { // Ex2
        int height = Y.getRowDimension();
        int width = Y.getColumnDimension();

        // Create arrays to store R, G, B values
        int[][] convertedRed = new int[height][width];
        int[][] convertedGreen = new int[height][width];
        int[][] convertedBlue = new int[height][width];

        // Go through every pixel and apply the inverse formula
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double yVal = Y.get(j, i);
                double cbVal = Cb.get(j, i);
                double crVal = Cr.get(j, i);

                double r = 1.164 * (yVal - 16) + 1.596 * (crVal - 128);
                double g = 1.164 * (yVal - 16) - 0.813 * (crVal - 128) - 0.391 * (cbVal - 128);
                double b = 1.164 * (yVal - 16) + 2.018 * (cbVal - 128);

                // Clamp to 0-255 and round to int
                convertedRed[j][i] = Helper.checkValue(r);
                convertedGreen[j][i] = Helper.checkValue(g);
                convertedBlue[j][i] = Helper.checkValue(b);
            }
        }

        return new int[][][]{convertedRed, convertedGreen, convertedBlue};
    }
}
