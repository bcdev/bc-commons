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

import java.io.*;

import static com.bc.util.io.FileUtils.directoryContainsFile;
import static com.bc.util.io.FileUtils.isSymbolicLink;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class FileUtilsTest extends TestCase {

    private final String systemTempDirPath = System.getProperty("java.io.tmpdir");
    private final File systemTempDir = new File(systemTempDirPath);

    public void testDeleteFileTree(boolean dereference) throws IOException {
        File testDir = null;
        File testDirToDelete = null;
        try {
            testDir = new File(systemTempDir, "testDir");
            testDir.mkdirs();
            File testFile = new File(testDir, "testFile");
            testFile.createNewFile();

            testDirToDelete = new File(systemTempDir, "testDirToDelete");
            testDirToDelete.mkdirs();
            File testLinkToTestDir = new File(testDirToDelete, "testLinkToTestDir");

            if( isLnCommandAvailable() ) {
                createSymLink(testDir, testLinkToTestDir);
            }

            if( dereference ) {
                FileUtils.deleteFileTree(testDirToDelete, true);
            }else{
                FileUtils.deleteFileTree(testDirToDelete, false);
            }

            assertFalse(testDirToDelete.exists());
            if( isLnCommandAvailable() ) {
                assertFalse(testLinkToTestDir.exists());
            }
            if( dereference ) {
                //if we dereference, we expect the directory linked to, and its content to be deleted along with the "testDirToDelete" directory.
                assertFalse(testDir.isDirectory());
                assertFalse(testFile.isFile());
            }else{
                assertTrue(testDir.isDirectory());
                assertTrue(testFile.isFile());
            }
        } finally {
            //cleanup (which unfortunately relies on the same code being tested to work :D)
            TestUtil.deleteFileTree(testDir, true);
            TestUtil.deleteFileTree(testDirToDelete, true);
        }
    }

    private void createSymLink(File filetoLinkTo, File linkFileDestination) throws IOException {
        if( linkFileDestination.exists() )
            throw new RuntimeException("link already exists");
        String[] linkCommand = new String[]{"ln", "-s", filetoLinkTo.getAbsolutePath(), linkFileDestination.getAbsolutePath()};
        Process exec = Runtime.getRuntime().exec(linkCommand);
        try {
            int result = exec.waitFor();
            printProcessErr(exec);
            assertEquals(0, result);
        } catch (InterruptedException e) {
        }
    }

    private Boolean lnCommandAvailable_cached = null;
    private boolean isLnCommandAvailable() {
        if( lnCommandAvailable_cached != null )
            return lnCommandAvailable_cached;
        String[] linkCommand = new String[]{"which", "ln"};
        try {
            Process exec = Runtime.getRuntime().exec(linkCommand);
            int result = exec.waitFor();
            lnCommandAvailable_cached = (result == 0);
        } catch (InterruptedException e) {
        } catch (IOException e) {
        }
        return lnCommandAvailable_cached;
    }


    public void testDeleteFileTree() throws IOException {
        testDeleteFileTree(false);
    }

    public void testDeleteFileTreeDereference() throws IOException {
        if( !isLnCommandAvailable() ) {
            System.out.println("testDeleteFileTreeDereference() suppressed (ln command not available)");
            return;
        }
        testDeleteFileTree(true);
    }

    public void testIsLink_file() throws IOException {
        if( !isLnCommandAvailable() ) {
            System.out.println("testIsLink_file() suppressed (ln command not available)");
            return;
        }
        File testDir = null;
        try {
            testDir = new File(systemTempDir, "testDir");
            testDir.mkdirs();
            File testFile = new File(testDir, "testFile");
            testFile.createNewFile();

            File linkFile = new File(testDir, "linkFile");

            createSymLink(testFile, linkFile);

            boolean result = isSymbolicLink(linkFile);
            assertFalse(isSymbolicLink(testDir));
            assertFalse(isSymbolicLink(testFile));
            assertTrue(result);
            assertTrue(!linkFile.isDirectory());
        } finally {
            TestUtil.deleteFileTree(testDir, true);
        }
    }

    public void testIsLink_directory() throws IOException {
        if( !isLnCommandAvailable() ) {
            System.out.println("testIsLink_directory() suppressed (ln command not available)");
            return;
        }
        File testDir = null;
        try {
            testDir = new File(systemTempDir, "testDir");
            testDir.mkdirs();
            File testFile = new File(testDir, "testFile");
            testFile.createNewFile();

            File linkFile = new File(testDir, "linkFile");

            createSymLink(testDir, linkFile);

            boolean result = isSymbolicLink(linkFile);
            assertFalse(isSymbolicLink(testDir));
            assertFalse(isSymbolicLink(testFile));
            assertTrue(result);
            assertTrue(linkFile.isDirectory());

            File nonLinkFileWithLinksInPath = new File(linkFile, testFile.getName());
            assertFalse(isSymbolicLink(nonLinkFileWithLinksInPath));
        } finally {
            TestUtil.deleteFileTree(testDir, true);
        }
    }

    public void testDirectoryContainsFile() throws Exception {
        if( !isLnCommandAvailable() ) {
            System.out.println("testDirectoryContainsFile() suppressed (ln command not available)");
            return;
        }
        assertTrue(directoryContainsFile(new File("/some/dir/somewhere"), new File("/some/dir/somewhere/and/inside/we/have/this/file")));
        assertFalse(directoryContainsFile(new File("/some/dir/somewhere"), new File("/some/dir/elsewhere/and/inside/we/have/this/file")));

        File testDir1 = null;
        File testDir2 = null;

        //now test again with links
        try {
            testDir1 = new File(systemTempDir, "testDir1");
            testDir1.mkdirs();
            File testFile = new File(testDir1, "testFile");
            testFile.createNewFile();

            testDir2 = new File(systemTempDir, "testDir2");
            testDir2.mkdirs();
            File linkFile1 = new File(testDir2, "linkFile1");
            File linkFile2 = new File(testDir2, "linkFile2");

            createSymLink(testDir1, linkFile1);
            createSymLink(testDir2, linkFile2);

            //testFile1 should be directly inside testDir1
            assertTrue(directoryContainsFile(testDir1, testFile));

            //linkFile1 should be a link, directly inside testDir2
            assertTrue(directoryContainsFile(testDir2, linkFile1));

            //linkFile1 is linked to a file that is not in testDir2
            assertFalse(directoryContainsFile(testDir2, linkFile1.getCanonicalFile()));

            //linkFile2 is linked to a file that is in testDir2
            assertTrue(directoryContainsFile(testDir2, linkFile2));
            assertTrue(directoryContainsFile(testDir2, linkFile2.getCanonicalFile()));
        } finally {
            TestUtil.deleteFileTree(testDir1, true);
            TestUtil.deleteFileTree(testDir2, true);
        }

    }

    private void printProcessErr(Process exec) throws IOException {
        InputStream in = exec.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while( true ) {
            String line = reader.readLine();
            if( line == null ) {
                break;
            }
            System.err.println(line);
        }
    }

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
