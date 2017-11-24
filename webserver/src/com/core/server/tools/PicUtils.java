package com.core.server.tools;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PicUtils {
    public static void compressPic(File input, File output) throws Exception {
        FileOutputStream out = null;

        try {
            BufferedImage ex = ImageIO.read(input);
            if(ex.getWidth((ImageObserver)null) == -1) {
                throw new Exception("图片没有准备好:" + input.getAbsolutePath());
            }

            int newWidth = (int)((double)ex.getWidth((ImageObserver)null));
            int newHeight = (int)((double)ex.getHeight((ImageObserver)null));
            if(newWidth > 2000 || newHeight > 2000) {
                double tag = 0.1D;
                if(newWidth > newHeight) {
                    tag = (double)newWidth / 2000.0D;
                } else {
                    tag = (double)newHeight / 2000.0D;
                }

                newWidth = (int)((double)newWidth / tag);
                newHeight = (int)((double)newHeight / tag);
            }

            BufferedImage tag1 = new BufferedImage(newWidth, newHeight, 1);
            tag1.getGraphics().drawImage(ex.getScaledInstance(newWidth, newHeight, 4), 0, 0, (ImageObserver)null);
            out = new FileOutputStream(output);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag1);
        } catch (IOException var11) {
            throw var11;
        } finally {
            if(out != null) {
                out.close();
            }

        }

    }

    public static void compressPic(File input, File output, int width, int height) throws Exception {
        Object out = null;

        try {
            BufferedImage ex = ImageIO.read(input);
            if(ex.getWidth((ImageObserver)null) == -1) {
                throw new Exception("图片没有准备好:" + input.getAbsolutePath());
            }

            int newWidth = (int)((double)ex.getWidth((ImageObserver)null));
            int newHeight = (int)((double)ex.getHeight((ImageObserver)null));
            double tag;
            if(newWidth > width) {
                tag = 0.1D;
                tag = (double)newWidth / (double)width;
                newWidth = (int)((double)newWidth / tag);
            }

            if(newHeight > height) {
                tag = 0.1D;
                tag = (double)newHeight / (double)height;
                newHeight = (int)((double)newHeight / tag);
            }

            if(!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }

            BufferedImage tag1 = new BufferedImage(newWidth, newHeight, 1);
            tag1.getGraphics().drawImage(ex.getScaledInstance(newWidth, newHeight, 4), 0, 0, (ImageObserver)null);
            ImageIO.write(tag1, Utils.getExt(output.getName()), output);
        } catch (IOException var13) {
            throw var13;
        } finally {
            if(out != null) {
                ((FileOutputStream)out).close();
            }

        }

    }
}
