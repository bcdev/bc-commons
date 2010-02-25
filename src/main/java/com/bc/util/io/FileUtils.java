/*
 * $Id: FileUtils.java,v 1.4 2008-09-04 10:47:43 tom Exp $
 *
 * Copyright (C) 2002,2003  by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package com.bc.util.io;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static final String[] PATH_SEPARATORS = {"\\", "/"};

    /**
     * Retrieves the filename with (eventual) extension from a complete path
     *
     * @param path the complete path
     * @return the filename
     */
    public static String getFileNameFromPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("argument path is null");
        }

        int lastChar = -1;
        for (int i = 0; i < PATH_SEPARATORS.length; i++) {
            lastChar = path.lastIndexOf(PATH_SEPARATORS[i]);
            if (lastChar >= 0) {
                break;
            }
        }

        String fileName;
        if (lastChar >= 0) {
            fileName = path.substring(lastChar + 1, path.length());
        } else {
            fileName = path;
        }
        return fileName;
    }

    public static String getFileNameWithoutExtension(String fileName) {
        final int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }

    /**
     * Retrieves the size of a file object. Three cases can be distinguished:
     * <ul>
     * <li>The file object denotes a file: returns the result of File.length()</li>
     * <li>The file object denotes a directory: returns the iteratively accumulated size of all files contained</li>
     * <li>The file object does not exist or is null: returns 0</li>
     * </ul>
     *
     * @param file the file to check
     * @return the size
     */
    public static long getSizeInBytes(File file) {
        if ((file == null) || (!file.exists())) {
            return 0;
        }

        if (file.isFile()) {
            return file.length();
        }

        long size = 0;

        final File[] content = file.listFiles();
        if (content == null) {
            return 0;
        }
        for (int i = 0; i < content.length; i++) {
            size += getSizeInBytes(content[i]);
        }

        return size;
    }

    /**
     * Returns a system dependent slashified path.
     *
     * @param path the path to shlashify
     * @return a system dependent slashified path.
     */
    public static String slashify(final String path) {
        final char sep = File.separatorChar;
        if ('/' == sep) {
            return path.replace('\\', sep);
        } else {
            return path.replace('/', sep);
        }
    }

    public static void copy(File source, File target) throws IOException {
        final byte[] buffer = new byte[ONE_KB * ONE_KB];
        int bytesRead;

        FileImageInputStream sourceStream = null;
        FileImageOutputStream targetStream = null;

        try {
            final File targetDir = target.getParentFile();
            if (!targetDir.isDirectory()) {
                if (!targetDir.mkdirs()) {
                    throw new IOException("failed to create target directory: " + targetDir.getAbsolutePath());
                }
            }
            target.createNewFile();

            sourceStream = new FileImageInputStream(source);
            targetStream = new FileImageOutputStream(target);
            while ((bytesRead = sourceStream.read(buffer)) >= 0) {
                targetStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (sourceStream != null) {
                sourceStream.close();
            }
            if (targetStream != null) {
                targetStream.flush();
                targetStream.close();
            }
        }
    }

    public static void move(File source, File target) throws IOException {
        copy(source, target);

        if (source.length() == target.length()) {
            source.delete();
        }
    }

    synchronized public static File createFileInTempDir(String tempDirName, String fileName) throws IOException {
        final File tempDir = new File(tempDirName);
        if (!tempDir.isDirectory()) {
            throw new IOException("The temp directory '" + tempDirName + "' does not exist");
        }

        final String name;
        final String extension;
        if (fileName.contains(".")) {
            final int i = fileName.lastIndexOf(".");
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        } else {
            name = fileName;
            extension = "";
        }

        File tempFile;
        int number = 0;
        do {
            tempFile = new File(tempDirName, name + (number > 0 ? number : "") + extension);
            number++;
        } while (tempFile.exists());
        tempFile.createNewFile();

        return tempFile;
    }

    public static void deleteFileTree(File treeRoot) {
        File[] files = treeRoot.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    deleteFileTree(file);
                }
                file.delete();
            }
        }
        treeRoot.delete();
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private static final int ONE_KB = 1024;
}
