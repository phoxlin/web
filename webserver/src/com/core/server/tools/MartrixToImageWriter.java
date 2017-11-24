package com.core.server.tools;

import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class MartrixToImageWriter {
    private static final int BLACK = -16777216;
    private static final int WHITE = -1;

    private MatrixToImageWriter() {
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, 1);

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                image.setRGB(x, y, matrix.get(x, y)?-16777216:-1);
            }
        }

        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if(!ImageIO.write(image, format, file)) {
            throw new IOException("图片格式有误： " + format);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if(!ImageIO.write(image, format, stream)) {
            throw new IOException("图片格式有误：" + format);
        }
    }
}
