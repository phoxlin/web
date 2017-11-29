package com.core.server.tools;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class AudioUtils {
    public static void main(String[] args) {
        File source = new File("D:/FileStore/uploadfile/ybserver/7d/47/64/ff/7f/d15aec42b8378c4a480636be527dea/7d4764ff7fd15aec42b8378c4a480636be527dea.wav");
        trimAndroidAudio(source);
    }

    public static void trimAndroidAudio(File f) {
        File f2 = new File(f.getAbsolutePath() + ".2." + Utils.getExt(f.getName()));

        try {
            FileUtils.copyFile(f, f2);
            trimAndroidAudio(f2, f);
            f2.delete();
        } catch (IOException var3) {
            if(f2 != null && f2.exists()) {
                f2.delete();
            }
        }

    }

    public static void trimAndroidAudio(File f, File f2) {
        String ext = Utils.getExt(f.getName());
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec((String)null);
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat(ext);
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();

        try {
            encoder.encode(f, f2, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
