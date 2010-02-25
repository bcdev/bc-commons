/*
 * $Id: TestUtil.java,v 1.7 2008-06-19 15:37:24 sabine Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.util;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bc.util.io.FileUtils;

public class TestUtil {

    public static final File TEST_PATH = new File("testData");

    public static boolean isIOTestsSuppressed() {
        return TRUE.equals(System.getProperty("noiotests"));
    }

    public static boolean isHdfTestsSuppressed() {
        return TRUE.equals(System.getProperty("noHdfTests"));
    }

    public static boolean checkForSupressTimeExtensiveTests() {
        return TRUE.equals(System.getProperty("noExtensiveTests"));
    }

    public static boolean canFileBasedTestBePerformed(String methodName, File testFile) {
        if (TestUtil.isIOTestsSuppressed()) {
            System.out.println(methodName + "() suppressed");
            return false;
        }
        if (testFile == null || !testFile.exists()) {
            System.out.println(methodName + ": test data not available!");
            return false;
        }
        return true;
    }

    public static boolean isWindowsSystem() {
        final String osName = System.getProperty("os.name");
        return osName != null && osName.startsWith("Windows");
    }

    public static void deleteFileTree(File treeRoot) {
        FileUtils.deleteFileTree(treeRoot);
        if (treeRoot.isDirectory()) {
            Assert.fail("test directory could not be removed - check your tests");
        }
    }

    public static File writeTextFileInTestDir(String filename, String content) throws IOException {
        final File testFile = new File(TestUtil.TEST_PATH, filename);
        testFile.createNewFile();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(testFile);
            fileOutputStream.write(content.getBytes());
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }

        return testFile;
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private static final String TRUE = "true";
}
