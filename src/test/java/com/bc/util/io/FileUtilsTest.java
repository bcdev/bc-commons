/*
 * $Id: FileUtilsTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
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

import com.bc.util.TestUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class FileUtilsTest extends TestCase {

    public void testGetFileNameFromPath() {
        try {
            FileUtils.getFileNameFromPath(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        final String path_1 = "/usr/local/nasenmann.pl";
        final String file_1 = "nasenmann.pl";
        assertEquals(file_1, FileUtils.getFileNameFromPath(path_1));

        final String path_2 = "d:\\directory\\file.txt";
        final String file_2 = "file.txt";
        assertEquals(file_2, FileUtils.getFileNameFromPath(path_2));

        final String fileName = "HTMLspecial.ent";
        assertEquals(fileName, FileUtils.getFileNameFromPath(fileName));

        final String emptyName = "";
        assertEquals(emptyName, FileUtils.getFileNameFromPath(emptyName));
    }

    public void testGetFileNameWIthoutExtension() {
        assertEquals("Whats", FileUtils.getFileNameWithoutExtension("Whats.hip"));
        assertEquals("whereist.it", FileUtils.getFileNameWithoutExtension("whereist.it.zip"));
        assertEquals("withoutdot", FileUtils.getFileNameWithoutExtension("withoutdot"));
    }

    public void testGetSizeInBytesInvalidArguments() {
        assertEquals(0, FileUtils.getSizeInBytes(null));

        UnitTestFile file = new UnitTestFile("somewhere");
        file.setExists(false);

        assertEquals(0, FileUtils.getSizeInBytes(file));
    }

    public void testGetSizeInBytesForFiles() {
        UnitTestFile file = new UnitTestFile("somewhere.else");

        file.setLength(12);
        assertEquals(12, FileUtils.getSizeInBytes(file));

        file.setLength(108234);
        assertEquals(108234, FileUtils.getSizeInBytes(file));
    }

    public void testGetSizeInBytesForDirectory() {
        UnitTestFile file_1 = new UnitTestFile("100.bytes");
        file_1.setLength(100);
        UnitTestFile file_2 = new UnitTestFile("200.bytes");
        file_2.setLength(200);
        UnitTestDir dir = new UnitTestDir("testdir");

        dir.addFile(file_1);
        dir.addFile(file_2);

        assertEquals(100 + 200, FileUtils.getSizeInBytes(dir));
    }

    public void testGetSizeInBytesForNestedDirectories() {
        UnitTestFile file_1 = new UnitTestFile("100.bytes");
        file_1.setLength(100);
        UnitTestFile file_2 = new UnitTestFile("200.bytes");
        file_2.setLength(200);
        UnitTestFile file_3 = new UnitTestFile("200.bytes");
        file_3.setLength(300);
        UnitTestDir dir = new UnitTestDir("testdir");
        UnitTestDir subDir = new UnitTestDir("Subdir");

        subDir.addFile(file_1);
        subDir.addFile(file_2);
        dir.addFile(subDir);
        dir.addFile(file_3);

        assertEquals(100 + 200 + 300, FileUtils.getSizeInBytes(dir));
    }

    public void testSlashify() {
        final String sep = File.separator;
        final String pre;
        if ("/".equals(sep)) {
            pre = "\\";
        } else {
            pre = "/";
        }
        assertFalse(sep.equals(pre));

        final String a = "a";
        final String b = "b";
        final String c = "c";
        final String d = "d";

        final String toSlashify = a + pre + b + pre + c + pre + d;
        final String expected = a + sep + b + sep + c + sep + d;

        assertEquals(expected, FileUtils.slashify(toSlashify));
    }

    public void testCopyFile() throws IOException {
        final File source = prepareTestFile();
        final File target = new File(TestUtil.TEST_PATH, "target");

        FileUtils.copy(source, target);

        assertEquals(source.length(), target.length());
        assertTargetContainsTestData(target);
    }

    public void testCopyFileCreatesDirectoryWhenNotExisting() throws IOException {
        final File source = prepareTestFile();
        final File target = new File(TestUtil.TEST_PATH, "/who/is/this/target");

        FileUtils.copy(source, target);

        assertEquals(source.length(), target.length());
        assertTargetContainsTestData(target);
    }

    public void testCopyFileFailsWhenInputNotExisting() {
        final File source = new File("this_is_not_present.file");
        final File target = new File(TestUtil.TEST_PATH, "target");

        try {
            FileUtils.copy(source, target);
            fail("IOException expected");
        } catch (IOException expected) {
        }
    }

    public void testMoveFile() throws IOException {
        final File source = prepareTestFile();
        final File target = new File(TestUtil.TEST_PATH, "target");

        FileUtils.move(source, target);

        assertTargetContainsTestData(target);
        assertFalse(source.isFile());
    }

    public void testCreateFileInTempDir_WithNewNameIfFileAlreadyExists() throws IOException {
        final String name = "filename";
        final String ext = ".ext";
        final File expected1 = new File(TestUtil.TEST_PATH.getPath(), name + ext);
        final File expected2 = new File(TestUtil.TEST_PATH.getPath(), name + 1 + ext);

        final String fileName = name + ext;

        final File result1 = FileUtils.createFileInTempDir(TestUtil.TEST_PATH.getPath(), fileName);
        assertEquals(expected1, result1);
        assertEquals(true, result1.isFile());

        final File result2 = FileUtils.createFileInTempDir(TestUtil.TEST_PATH.getPath(), fileName);
        assertEquals(expected2, result2);
        assertEquals(true, result2.isFile());
    }

    public void testCreateFileInTempDir() throws IOException {
        final String fileName = "filename";
        final File expected = new File(TestUtil.TEST_PATH.getPath(), fileName);

        final File result = FileUtils.createFileInTempDir(TestUtil.TEST_PATH.getPath(), fileName);
        assertEquals(expected, result);
        assertTrue(expected.isFile());
    }

    public void testCreatFileInTempDirFailsWhenDirectoryDoesNotExist() throws IOException {
        try {
            FileUtils.createFileInTempDir("/not/on/this/machine", "a _file");
            fail("IOException expected");
        } catch (IOException expected) {
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private static final String testData = "1234567890123456789012345678901234567890";
    private static final String TEST_FILE_NAME = "test_input";

    protected void setUp() {
        if (!TestUtil.TEST_PATH.exists()) {
            TestUtil.TEST_PATH.mkdirs();
        }
    }

    protected void tearDown() {
        if (TestUtil.TEST_PATH.exists()) {
            TestUtil.deleteFileTree(TestUtil.TEST_PATH);
        }
    }

    private File prepareTestFile() throws IOException {
        final File source = new File(TestUtil.TEST_PATH, TEST_FILE_NAME);
        source.createNewFile();
        final FileOutputStream sourceStream = new FileOutputStream(source);
        sourceStream.write(testData.getBytes());
        sourceStream.close();
        return source;
    }

    private void assertTargetContainsTestData(File target) throws IOException {
        assertTrue(target.isFile());

        final FileInputStream targetStream = new FileInputStream(target);
        final byte[] buffer = new byte[(int) target.length()];
        targetStream.read(buffer);
        targetStream.close();

        assertEquals(testData, new String(buffer));
    }
}
