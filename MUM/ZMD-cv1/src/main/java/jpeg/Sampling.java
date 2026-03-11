package jpeg;

import Jama.Matrix;
import enums.SamplingType;

/**
 * Chroma subsampling and upsampling for Cb/Cr channels. // Ex3
 */
public class Sampling {

    /**
     * Downsamples a matrix by keeping only selected columns (and rows for 4:2:0).
     * 4:4:4 = no change, 4:2:2 = keep every 2nd col, 4:1:1 = keep every 4th col,
     * 4:2:0 = keep every 2nd col AND every 2nd row.
     */
    public static Matrix sampleDown(Matrix inputMatrix, SamplingType samplingType) { // Ex3
        switch (samplingType) {
            case S_4_4_4:
                return inputMatrix;

            case S_4_2_2:
                return downSampleColumns(inputMatrix);

            case S_4_1_1:
                // Keep every 4th column = downsample columns twice
                return downSampleColumns(downSampleColumns(inputMatrix));

            case S_4_2_0:
                // Downsample columns, then transpose, downsample columns again (= rows), then transpose back
                Matrix colDown = downSampleColumns(inputMatrix);
                Matrix transposed = colDown.transpose();
                Matrix rowDown = downSampleColumns(transposed);
                return rowDown.transpose();

            default:
                return inputMatrix;
        }
    }

    /**
     * Upsamples a matrix by duplicating columns (and rows for 4:2:0).
     * Restores the matrix to its original dimensions.
     */
    public static Matrix sampleUp(Matrix inputMatrix, SamplingType samplingType) { // Ex3
        switch (samplingType) {
            case S_4_4_4:
                return inputMatrix;

            case S_4_2_2:
                return upSampleColumns(inputMatrix);

            case S_4_1_1:
                // Upsample columns twice (2x -> 4x)
                return upSampleColumns(upSampleColumns(inputMatrix));

            case S_4_2_0:
                // Upsample rows first (transpose, upsample cols, transpose back), then upsample columns
                Matrix transposed = inputMatrix.transpose();
                Matrix rowUp = upSampleColumns(transposed);
                Matrix backToNormal = rowUp.transpose();
                return upSampleColumns(backToNormal);

            default:
                return inputMatrix;
        }
    }

    /**
     * Keeps every other column (1st, 3rd, 5th...), skips even-indexed columns.
     */
    private static Matrix downSampleColumns(Matrix mat) { // Ex3
        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();
        int newCols = (cols + 1) / 2; // ceiling division

        double[][] result = new double[rows][newCols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < newCols; x++) {
                result[y][x] = mat.get(y, x * 2);
            }
        }

        return new Matrix(result);
    }

    /**
     * Duplicates each column once (each value appears twice).
     */
    private static Matrix upSampleColumns(Matrix mat) { // Ex3
        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();
        int newCols = cols * 2;

        double[][] result = new double[rows][newCols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                double val = mat.get(y, x);
                result[y][x * 2] = val;
                result[y][x * 2 + 1] = val;
            }
        }

        return new Matrix(result);
    }
}
