package com.bc.util.io;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


public class UnitTestDir extends File {

    public UnitTestDir(File parent, String child) {
        super(parent, child);
        init();
    }

    public UnitTestDir(String pathname) {
        super(pathname);
        init();
    }

    public UnitTestDir(String parent, String child) {
        super(parent, child);
        init();
    }

    public UnitTestDir(URI pathname) {
        super(pathname);
        init();
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isIt) {
        isFile = isIt;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isIt) {
        isDirectory = isIt;
    }

    public File[] listFiles() {
        File[] result = new File[fileList.size()];
        return (File[]) fileList.toArray(result);
    }

    public void addFile(File file) {
        fileList.add(file);
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private boolean isFile;
    private boolean isDirectory;
    private boolean exists;
    private ArrayList fileList;

    private void init() {
        isFile = false;
        isDirectory = true;
        exists = true;
        fileList = new ArrayList();
    }
}
