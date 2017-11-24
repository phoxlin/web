package com.core.server.tools;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.util.HashMap;

public class ZxingUtils {
    public static void createQr(String str, OutputStream os) throws Exception {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(2));
        BitMatrix bitMatrix = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 400, 400, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, "jpg", os);
    }
}
