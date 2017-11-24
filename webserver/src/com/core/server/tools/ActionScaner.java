package com.core.server.tools;

import com.core.Task;
import com.core.server.Action;
import com.core.server.log.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class ActionScaner {
    private Set<String> names = new HashSet();
    private Set<Class<Action>> actions = new HashSet();
    private Set<Class<Task>> tasks = new HashSet();
    private boolean stop = false;

    public boolean scan() {
        this.names.clear();
        File baseClassPath = new File(Utils.getRootClassPath());
        this.getAllFiles(baseClassPath, "", "class");
        this.scanActions();
        return this.stop;
    }

    private void scanActions() {
        this.actions.clear();
        this.tasks.clear();
        String scanPaths = Resources.getProperty("ScanPaths", "");
        Logger.info("当前系统为:" + (Resources.DEVELOPMENT ? "开发环境" : "生产环境"));
        Logger.info("ScanPaths:" + scanPaths);
        Iterator it = this.names.iterator();

        while(it.hasNext()) {
            String name = (String)it.next();

            try {
                if(!name.startsWith("com.core")) {
                    boolean ok = false;
                    if(scanPaths != null && scanPaths.length() > 0) {
                        String[] obj = scanPaths.split(";");
                        int e = 0;

                        for(int l = obj.length; e < l; ++e) {
                            String path = obj[e];
                            if(path != null && path.length() > 0 && name.startsWith(path)) {
                                ok = true;
                                break;
                            }
                        }
                    }

                    if(ok) {
                        Class cls = null;

                        try {
                            cls = Class.forName(name);
                        } catch (Exception e) {
                            ;
                        }

                        try {
                            if(cls != null && cls.asSubclass(Action.class) != null) {
                                this.actions.add(cls);
                            }
                        } catch (Exception e) {
                            if(cls != null && cls.asSubclass(Task.class) != null) {
                                this.tasks.add(cls);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ;
            }
        }

    }

    private void getAllFiles(File basePath, String prefixName, String ext) {
        if(basePath.exists() && basePath.isDirectory()) {
            File[] fs = basePath.listFiles();
            if(fs != null && fs.length > 0) {
                int i = 0;

                for(int l = fs.length; i < l; ++i) {
                    File f = fs[i];
                    String name;
                    String fext;
                    if(f.isDirectory()) {
                        name = f.getName();
                        fext = prefixName.length() > 0?prefixName + "." + name:name;
                        this.getAllFiles(f, fext, ext);
                    } else {
                        name = f.getName();
                        fext = Utils.getExt(name);
                        if(fext.equals(ext)) {
                            String jarFile = Utils.getFileNameWithoutExt(name);
                            String e = prefixName.length() > 0?prefixName + "." + jarFile:jarFile;
                            this.names.add(e);
                        } else if(fext.equalsIgnoreCase("jar")) {
                            JarFile var26 = null;

                            try {
                                var26 = new JarFile(f);
                                Enumeration var27 = var26.entries();

                                while(var27.hasMoreElements()) {
                                    JarEntry jar = (JarEntry)var27.nextElement();
                                    if(!jar.isDirectory()) {
                                        String jarName = jar.getName();
                                        String jarExt = Utils.getExt(jarName);
                                        if(ext.equalsIgnoreCase(jarExt)) {
                                            String pathWithoutExt = Utils.getFileNameWithoutExt(jarName);
                                            pathWithoutExt = pathWithoutExt.replace("\\", "/");
                                            pathWithoutExt = pathWithoutExt.replace("/", ".");
                                            this.names.add(pathWithoutExt);
                                        }
                                    }
                                }

                                this.stop = true;
                            } catch (IOException var24) {
                                var24.printStackTrace();
                            } finally {
                                try {
                                    var26.close();
                                } catch (IOException var23) {
                                    ;
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    public Set<Class<Action>> getActions() {
        return this.actions;
    }

    public Set<Class<Task>> getTasks() {
        return this.tasks;
    }
}
