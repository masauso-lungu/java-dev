package jpeg;

import Jama.Matrix;
import enums.TransformType;

/**
 * Implementation of DCT and WHT transformations for image processing. // Ex5
 */
public class Transform {

    /**
     * Returns a transformation matrix for the given type and block size.
     * DCT: cosine-based coefficients.
     * WHT: recursive Hadamard matrix normalized by 1/sqrt(N).
     */
    public static Matrix getTransformMatrix(TransformType type, int blockSize) { // Ex5
        switch (type) {
            case DCT:
                return createDCTMatrix(blockSize);
            case WHT:
                return createWHTMatrix(blockSize);
            default:
                return Matrix.identity(blockSize, blockSize);
        }
    }

    /**
     * Forward transform of the input matrix using blocks of the given size.
     * For each NxN block: Θ = A * X * Aᵀ
     */
    public static Matrix transform(Matrix input, TransformType type, int blockSize) { // Ex5
        Matrix A = getTransformMatrix(type, blockSize);
        Matrix AT = A.transpose();
        int rows = input.getRowDimension();
        int cols = input.getColumnDimension();

        Matrix result = new Matrix(rows, cols);

        for (int y = 0; y + blockSize <= rows; y += blockSize) {
            for (int x = 0; x + blockSize <= cols; x += blockSize) {
                // Extract block
                Matrix block = input.getMatrix(y, y + blockSize - 1, x, x + blockSize - 1);
                // Forward: Θ = A * X * Aᵀ
                Matrix transformed = A.times(block).times(AT);
                // Place back
                result.setMatrix(y, y + blockSize - 1, x, x + blockSize - 1, transformed);
            }
        }

        return result;
    }

    /**
     * Inverse transform of the input matrix using blocks of the given size.
     * For each NxN block: X = Aᵀ * Θ * A
     */
    public static Matrix inverseTransform(Matrix input, TransformType type, int blockSize) { // Ex5
        Matrix A = getTransformMatrix(type, blockSize);
        Matrix AT = A.transpose();
        int rows = input.getRowDimension();
        int cols = input.getColumnDimension();

        Matrix result = new Matrix(rows, cols);

        for (int y = 0; y + blockSize <= rows; y += blockSize) {
            for (int x = 0; x + blockSize <= cols; x += blockSize) {
                // Extract block
                Matrix block = input.getMatrix(y, y + blockSize - 1, x, x + blockSize - 1);
                // Inverse: X = Aᵀ * Θ * A
                Matrix inverted = AT.times(block).times(A);
                // Place back
                result.setMatrix(y, y + blockSize - 1, x, x + blockSize - 1, inverted);
            }
        }

        return result;
    }

    /**
     * Creates a DCT (Discrete Cosine Transform) matrix of given size.
     * C[i][j] = sqrt(1/N) * cos((2j+1)*i*π / 2N)    for i = 0
     * C[i][j] = sqrt(2/N) * cos((2j+1)*i*π / 2N)    for i > 0
     */
    private static Matrix createDCTMatrix(int N) { // Ex5
        double[][] matrix = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double cosValue = Math.cos((2 * j + 1) * i * Math.PI / (2.0 * N));
                if (i == 0) {
                    matrix[i][j] = Math.sqrt(1.0 / N) * cosValue;
                } else {
                    matrix[i][j] = Math.sqrt(2.0 / N) * cosValue;
                }
            }
        }

        return new Matrix(matrix);
    }

    /**
     * Creates a WHT (Walsh-Hadamard Transform) matrix of given size.
     * Built recursively: H(2N) = [H(N), H(N); H(N), -H(N)]
     * Then normalized by 1/sqrt(blockSize).
     */
    private static Matrix createWHTMatrix(int N) { // Ex5
        double[][] hadamard = buildHadamard(N);
        Matrix mat = new Matrix(hadamard);
        // Normalize by 1/sqrt(N)
        return mat.times(1.0 / Math.sqrt(N));
    }

    /**
     * Recursively builds the unnormalized Hadamard matrix.
     * H(1) = [1]
     * H(2N) = [H(N), H(N); H(N), -H(N)]
     */
    private static double[][] buildHadamard(int N) { // Ex5
        if (N == 1) {
            return new double[][]{{1.0}};
        }

        double[][] halfH = buildHadamard(N / 2);
        int half = N / 2;
        double[][] H = new double[N][N];

        for (int i = 0; i < half; i++) {
            for (int j = 0; j < half; j++) {
                H[i][j] = halfH[i][j];           // top-left: H(N/2)
                H[i][j + half] = halfH[i][j];     // top-right: H(N/2)
                H[i + half][j] = halfH[i][j];     // bottom-left: H(N/2)
                H[i + half][j + half] = -halfH[i][j]; // bottom-right: -H(N/2)
            }
        }

        return H;
    }
}
