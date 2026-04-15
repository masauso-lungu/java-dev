package jpeg;

import Jama.Matrix;

/**
 * JPEG quantization and inverse quantization. // Ex6
 */
public class Quantization {

    // Standard 8x8 luminance quantization matrix // Ex6
    private static final double[][] quantizationMatrix8Y = {
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    // Standard 8x8 chrominance quantization matrix // Ex6
    private static final double[][] quantizationMatrix8C = {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}};

    /**
     * Generates a quantization matrix for the given block size and quality.
     * Quality 100 = all 1s (no quantization loss).
     * Resizes from 8x8 base using nearest-neighbor mapping.
     */
    public static Matrix getQuantizationMatrix(int blockSize, double quality, boolean matrixY) { // Ex6
        // Quality 100: return matrix of all 1s
        if (quality == 100) {
            return new Matrix(blockSize, blockSize, 1);
        }

        // Calculate alpha scaling factor
        double alpha;
        if (quality < 50) {
            alpha = 50.0 / quality;
        } else {
            alpha = (100.0 - quality) / 50.0;
        }

        // Select base 8x8 matrix
        double[][] base = matrixY ? quantizationMatrix8Y : quantizationMatrix8C;

        // Resize and apply alpha
        double[][] result = new double[blockSize][blockSize];
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                // Map to 8x8 source position
                int srcI = Math.min((int) Math.floor(i * 8.0 / blockSize), 7);
                int srcJ = Math.min((int) Math.floor(j * 8.0 / blockSize), 7);
                double val = base[srcI][srcJ] * alpha;
                // Clamp to [1, 255]
                result[i][j] = Math.max(1, Math.min(255, val));
            }
        }

        return new Matrix(result);
    }

    /**
     * Quantizes the input matrix by dividing by the quantization matrix.
     * Uses special rounding: |val| <= 0.2 → 2 decimal places, otherwise → 1 decimal place.
     */
    public static Matrix quantize(Matrix input, int blockSize, double quality, boolean matrixY) { // Ex6
        Matrix Q = getQuantizationMatrix(blockSize, quality, matrixY);
        double[][] qArr = Q.getArray();
        double[][] inputArr = input.getArray();
        int rows = inputArr.length;
        int cols = inputArr[0].length;
        double[][] result = new double[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                double val = inputArr[y][x] / qArr[y % blockSize][x % blockSize];
                result[y][x] = specialRound(val);
            }
        }

        return new Matrix(result);
    }

    /**
     * Inverse quantization — multiplies by the quantization matrix.
     */
    public static Matrix inverseQuantize(Matrix input, int blockSize, double quality, boolean matrixY) { // Ex6
        Matrix Q = getQuantizationMatrix(blockSize, quality, matrixY);
        double[][] qArr = Q.getArray();
        double[][] inputArr = input.getArray();
        int rows = inputArr.length;
        int cols = inputArr[0].length;
        double[][] result = new double[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                result[y][x] = inputArr[y][x] * qArr[y % blockSize][x % blockSize];
            }
        }

        return new Matrix(result);
    }

    /**
     * Special rounding rule for quantization:
     * Values between -0.2 and 0.2 are rounded to zero.
     * For x < -0.2 or x > 0.2, round to 1 decimal place (e.g. -7.889 → -7.9).
     */
    private static double specialRound(double value) { // Ex6
        if (Math.abs(value) <= 0.2) {
            return 0;
            //return Math.round(value * 100.0) / 100.0;
        } else {
            return Math.round(value * 10.0) / 10.0;
        }
    }
}
