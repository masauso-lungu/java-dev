package jpeg;

import Jama.Matrix;

/**
 * Objective image quality evaluation methods. // Ex4
 */
public class Quality {

    /**
     * Calculates MSE (Mean Squared Error) between original and modified image data.
     * MSE = (1/MN) * Σ[x(m,n) - x'(m,n)]²
     */
    public static double countMSE(double[][] original, double[][] modified) { // Ex4
        int height = original.length;
        int width = original[0].length;

        double sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double diff = original[y][x] - modified[y][x];
                sum += diff * diff;
            }
        }

        return sum / (height * width);
    }

    /**
     * Calculates MAE (Mean Absolute Error) between original and modified image data.
     * MAE = (1/MN) * Σ|x(m,n) - x'(m,n)|
     */
    public static double countMAE(double[][] original, double[][] modified) { // Ex4
        int height = original.length;
        int width = original[0].length;

        double sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += Math.abs(original[y][x] - modified[y][x]);
            }
        }

        return sum / (height * width);
    }

    /**
     * Calculates SAE (Sum of Absolute Errors) between original and modified image data.
     * SAE = Σ|x(m,n) - x'(m,n)|
     */
    public static double countSAE(double[][] original, double[][] modified) { // Ex4
        int height = original.length;
        int width = original[0].length;

        double sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += Math.abs(original[y][x] - modified[y][x]);
            }
        }

        return sum;
    }

    /**
     * Calculates PSNR (Peak Signal-to-Noise Ratio) from MSE.
     * PSNR = 10 * log10(255² / MSE) [dB]
     */
    public static double countPSNR(double MSE) { // Ex4
        return 10 * Math.log10((255.0 * 255.0) / MSE);
    }

    /**
     * Calculates PSNR for an RGB image from MSE values of each channel.
     * Uses the average of the three MSE values.
     */
    public static double countPSNRforRGB(double mseRed, double mseGreen, double mseBlue) { // Ex4
        double averageMSE = (mseRed + mseGreen + mseBlue) / 3.0;
        return countPSNR(averageMSE);
    }

    /**
     * Converts an int[][] array to double[][] (needed for RGB components).
     */
    public static double[][] convertIntToDouble(int[][] intArray) { // Ex4
        double[][] doubleArray = new double[intArray.length][intArray[0].length];
        for (int i = 0; i < intArray.length; i++) {
            for (int j = 0; j < intArray[0].length; j++) {
                doubleArray[i][j] = (double) intArray[i][j];
            }
        }
        return doubleArray;
    }

    /**
     * Calculates SSIM (Structural Similarity Index) over the entire image.
     * SSIM(x,y) = (2*μx*μy + C1)(2*σxy + C2) / (μx² + μy² + C1)(σx² + σy² + C2)
     * Constants: L=255, K1=0.01, K2=0.03
     */
    public static double countSSIM(Matrix original, Matrix modified) { // Ex4
        double[] x = flattenMatrix(original);
        double[] y = flattenMatrix(modified);
        return computeSSIM(x, y);
    }

    /**
     * Calculates MSSIM (Mean SSIM) as the average SSIM over 8x8 blocks.
     */
    public static double countMSSIM(Matrix original, Matrix modified) { // Ex4
        int rows = original.getRowDimension();
        int cols = original.getColumnDimension();

        int blockSize = 8;
        double ssimSum = 0;
        int blockCount = 0;

        for (int y = 0; y + blockSize <= rows; y += blockSize) {
            for (int x = 0; x + blockSize <= cols; x += blockSize) {
                // Extract 8x8 block from each matrix
                Matrix origBlock = original.getMatrix(y, y + blockSize - 1, x, x + blockSize - 1);
                Matrix modBlock = modified.getMatrix(y, y + blockSize - 1, x, x + blockSize - 1);

                double[] origFlat = flattenMatrix(origBlock);
                double[] modFlat = flattenMatrix(modBlock);

                ssimSum += computeSSIM(origFlat, modFlat);
                blockCount++;
            }
        }

        return ssimSum / blockCount;
    }

    /**
     * Computes SSIM between two 1D arrays of pixel values.
     */
    private static double computeSSIM(double[] x, double[] y) { // Ex4
        int N = x.length;
        double L = 255.0;
        double K1 = 0.01;
        double K2 = 0.03;
        double C1 = (K1 * L) * (K1 * L); // 6.5025
        double C2 = (K2 * L) * (K2 * L); // 58.5225

        // Mean values (formula 5.6)
        double muX = 0, muY = 0;
        for (int i = 0; i < N; i++) {
            muX += x[i];
            muY += y[i];
        }
        muX /= N;
        muY /= N;

        // Standard deviations and covariance (formulas 5.7, 5.8)
        double sigmaXsq = 0, sigmaYsq = 0, sigmaXY = 0;
        for (int i = 0; i < N; i++) {
            double dx = x[i] - muX;
            double dy = y[i] - muY;
            sigmaXsq += dx * dx;
            sigmaYsq += dy * dy;
            sigmaXY += dx * dy;
        }
        sigmaXsq /= (N - 1);
        sigmaYsq /= (N - 1);
        sigmaXY /= (N - 1);

        // SSIM formula (5.5) — using variances directly (σ² not σ)
        double numerator = (2 * muX * muY + C1) * (2 * sigmaXY + C2);
        double denominator = (muX * muX + muY * muY + C1) * (sigmaXsq + sigmaYsq + C2);

        return numerator / denominator;
    }

    /**
     * Flattens a Matrix into a 1D array (row by row).
     */
    private static double[] flattenMatrix(Matrix mat) { // Ex4
        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();
        double[] flat = new double[rows * cols];
        double[][] arr = mat.getArray();
        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                flat[index++] = arr[y][x];
            }
        }
        return flat;
    }
}
