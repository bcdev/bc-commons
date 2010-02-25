package com.bc.util.io;

import java.io.File;
import java.net.URI;

public class UnitTestFile extends File {

    public UnitTestFile(File parent, String child) {
        super(parent, child);
        init();
    }

    public UnitTestFile(String pathname) {
        super(pathname);
        init();
    }

    public UnitTestFile(String parent, String child) {
        super(parent, child);
        init();
    }

    public UnitTestFile(URI pathname) {
        super(pathname);
        init();
    }

    public long lastModified() {
        return lastModified;
    }


    public boolean setLastModified(long time) {
        lastModified = time;
        return true;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isIt) {
        isFile = isIt;
    }

    public long length() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean doesIt) {
        exists = doesIt;
    }

    public void setMkdirsReturns(boolean mkdirReturns) {
        this.mkdirReturns = mkdirReturns;
    }

    public boolean mkdirs() {
        if (mkdirReturns) {
            super.mkdirs();
        }
        return mkdirReturns;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final double multiplier = 10000.0;
    
    private boolean isFile;
    private boolean isDirectory;
    private boolean exists;
    private long lastModified;
    private long length;
    private boolean mkdirReturns;

    private void init() {
        lastModified = (long) (multiplier * Math.random());
        length = (long) (multiplier * Math.random());
        isFile = true;
        exists = true;
        mkdirReturns = true;
    }
}
