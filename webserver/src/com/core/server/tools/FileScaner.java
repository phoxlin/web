package com.core.server.tools;

import java.io.File;

public final class FileScaner {
    private File baseDirectory;
    private String filename;
    private String ext;
    private File rsFile = null;

    public FileScaner(File baseDirectory, String filename, String ext) {
        this.baseDirectory = baseDirectory;
        this.filename = filename;
        this.ext = ext;
    }

    public File getFile() {
        this.scan(this.baseDirectory);
        return this.rsFile;
    }

    private void scan(File f) {
        if(f.isDirectory()) {
            File[] name = f.listFiles();
            File[] var6 = name;
            int tempExt = name.length;

            for(int realName = 0; realName < tempExt; ++realName) {
                File tempName = var6[realName];
                this.scan(tempName);
            }
        } else {
            String var7 = f.getName();
            String var8 = Utils.getFileNameWithoutExt(var7);
            String var9 = var8.split("-")[0];
            if(this.filename.equals(var9.trim())) {
                String var10 = Utils.getExt(var7);
                if(this.ext.equals(var10)) {
                    this.rsFile = f;
                    return;
                }
            }
        }

    }
}
