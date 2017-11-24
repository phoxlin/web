package com.core.server.tools;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class RandomValidateCode {
    public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";
    private Random random = new Random();
    private String randString = "0123456789";
    private int width = 78;
    private int height = 22;
    private int lineSize = 20;
    private int stringNum = 4;

    public RandomValidateCode() {
    }

    private Font getFont() {
        return new Font("Fixedsys", 1, 18);
    }

    private Color getRandColor(int fc, int bc) {
        if(fc > 255) {
            fc = 255;
        }

        if(bc > 255) {
            bc = 255;
        }

        int r = fc + this.random.nextInt(bc - fc - 16);
        int g = fc + this.random.nextInt(bc - fc - 14);
        int b = fc + this.random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    public void getRandcode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedImage image = new BufferedImage(this.width, this.height, 4);
        Graphics g = image.getGraphics();
        g.setColor(new Color(249, 249, 249));
        g.fillRect(0, 0, this.width, this.height);
        g.drawRect(0, 0, this.width - 1, this.height - 1);
        g.setFont(new Font("Times New Roman", 0, 18));
        g.setColor(this.getRandColor(110, 133));

        for(int randomString = 0; randomString <= this.lineSize; ++randomString) {
            this.drowLine(g);
        }

        String var9 = "";

        for(int e = 1; e <= this.stringNum; ++e) {
            var9 = this.drowString(g, var9, e);
        }

        SystemUtils.setSessionAttr("USER_LOGIN", "RANDOMVALIDATECODEKEY", var9, request, response);
        g.dispose();

        try {
            ServletOutputStream var10 = response.getOutputStream();
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(response.getOutputStream());
            encoder.encode(image);
            var10.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

    }

    private String drowString(Graphics g, String randomString, int i) throws InterruptedException {
        g.setFont(this.getFont());
        g.setColor(new Color(this.random.nextInt(101), this.random.nextInt(111), this.random.nextInt(121)));
        String rand = this.getRandomString(this.random.nextInt(this.randString.length()));
        randomString = randomString + rand;
        g.translate(this.random.nextInt(3), this.random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    private void drowLine(Graphics g) {
        int x = this.random.nextInt(this.width);
        int y = this.random.nextInt(this.height);
        int xl = this.random.nextInt(13);
        int yl = this.random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    public String getRandomString(int num) throws InterruptedException {
        return String.valueOf(this.randString.charAt(num));
    }
}
