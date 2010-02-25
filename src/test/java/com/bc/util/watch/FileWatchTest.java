package com.bc.util.watch;

import junit.framework.TestCase;

import java.io.File;

import com.bc.util.io.UnitTestFile;


public class FileWatchTest extends TestCase {
    private FileWatch fileWatch;

    protected void setUp() {
        fileWatch = new FileWatch();
    }

    public void testAddFile() {
        UnitTestFile file = new UnitTestFile("somewhere");

        assertEquals(0, fileWatch.getNumFiles());

        fileWatch.add(file);
        assertEquals(1, fileWatch.getNumFiles());

        fileWatch.add(null);
        assertEquals(1, fileWatch.getNumFiles());
    }

    public void testThatSameFileCannotBeAddedTwice() {
        UnitTestFile file = new UnitTestFile("somewhere");

        assertEquals(0, fileWatch.getNumFiles());

        fileWatch.add(file);
        assertEquals(1, fileWatch.getNumFiles());

        fileWatch.add(file);
        assertEquals(1, fileWatch.getNumFiles());
    }

    public void testAddingNonExistingFileOrDirectoryFails() {
        UnitTestFile file = new UnitTestFile("this/file.ile");
        file.setIsFile(false);

        try {
            fileWatch.add(file);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testRemoveFile() {
        UnitTestFile file = new UnitTestFile("/not/in/here/but/there");

        fileWatch.add(file);
        assertEquals(1, fileWatch.getNumFiles());

        fileWatch.remove(file);
        assertEquals(0, fileWatch.getNumFiles());
    }

    public void testRemoveIllegalFiles() {
        UnitTestFile file = new UnitTestFile("C:\\nasenmann.org");
        UnitTestFile file_2 = new UnitTestFile("/usr/loca/blasgt");

        fileWatch.add(file);
        assertEquals(1, fileWatch.getNumFiles());

        fileWatch.remove(null);
        assertEquals(1, fileWatch.getNumFiles());

        fileWatch.remove(file_2);
        assertEquals(1, fileWatch.getNumFiles());
    }

    public void testNoChangesOnCheck() {
        UnitTestFile file = new UnitTestFile("C:\\nasenmann.org");

        fileWatch.add(file);

        File[] changedFiles = fileWatch.getChangedFiles();
        assertEquals(0, changedFiles.length);
    }

    public void testChangesAreDetectedAndReset() {
        UnitTestFile file = new UnitTestFile("C:\\nasenmann.org");
        UnitTestFile file_2 = new UnitTestFile("/usr/loca/blasgt");

        fileWatch.add(file);
        fileWatch.add(file_2);

        File[] changedFiles = fileWatch.getChangedFiles();
        assertEquals(0, changedFiles.length);

        file.setLastModified(file.lastModified() + 3);

        changedFiles = fileWatch.getChangedFiles();
        assertEquals(1, changedFiles.length);

        changedFiles = fileWatch.getChangedFiles();
        assertEquals(0, changedFiles.length);

        file.setLastModified(file.lastModified() + 3);
        file_2.setLastModified(file_2.lastModified() + 3);

        changedFiles = fileWatch.getChangedFiles();
        assertEquals(2, changedFiles.length);

        changedFiles = fileWatch.getChangedFiles();
        assertEquals(0, changedFiles.length);
    }

    public void testThatListenerCanBeAttachedAndRemoved() {
        UnitTestFileListener listener_1 = new UnitTestFileListener();
        UnitTestFileListener listener_2 = new UnitTestFileListener();

        assertEquals(0, fileWatch.getNumListeners());

        fileWatch.addListener(listener_1);
        assertEquals(1, fileWatch.getNumListeners());

        fileWatch.addListener(listener_2);
        assertEquals(2, fileWatch.getNumListeners());

        fileWatch.removeListener(listener_1);
        assertEquals(1, fileWatch.getNumListeners());

        fileWatch.removeListener(listener_2);
        assertEquals(0, fileWatch.getNumListeners());
    }

    public void testNullCannotBeUsedAsListener() {
        UnitTestFileListener listener = new UnitTestFileListener();
        assertEquals(0, fileWatch.getNumListeners());

        fileWatch.addListener(null);
        assertEquals(0, fileWatch.getNumListeners());

        fileWatch.addListener(listener);
        assertEquals(1, fileWatch.getNumListeners());

        fileWatch.removeListener(null);
        assertEquals(1, fileWatch.getNumListeners());        
    }

    public void testThatSameListenerCannotAttachTwice() {
        UnitTestFileListener listener = new UnitTestFileListener();

        assertEquals(0, fileWatch.getNumListeners());

        fileWatch.addListener(listener);
        assertEquals(1, fileWatch.getNumListeners());

        fileWatch.addListener(listener);
        assertEquals(1, fileWatch.getNumListeners());
    }

    /** @noinspection MagicNumber*/
    public void testThatListenerIsNotifiedWhenChangesOccured() {
        UnitTestFile file = new UnitTestFile("C:\\hier\\isses.org");
        UnitTestFileListener listener_1 = new UnitTestFileListener();
        UnitTestFileListener listener_2 = new UnitTestFileListener();

        assertEquals(0, listener_1.getNumChangedFiles());
        assertEquals(0, listener_2.getNumChangedFiles());

        fileWatch.addListener(listener_1);
        fileWatch.addListener(listener_2);
        fileWatch.add(file);

        fileWatch.checkForModifiedFiles();
        assertEquals(0, listener_1.getNumChangedFiles());
        assertEquals(0, listener_2.getNumChangedFiles());

        file.setLastModified(file.lastModified() + 23);
        fileWatch.checkForModifiedFiles();
        assertEquals(1, listener_1.getNumChangedFiles());
        assertEquals(1, listener_2.getNumChangedFiles());
    }

    public void testThatListenersAreNotifiedOnMultipleFilesChanged() {
        UnitTestFile file_1 = new UnitTestFile("C:\\hier\\isses.org");
        UnitTestFile file_2 = new UnitTestFile("C:\\da\\auch.hup");
        UnitTestFile file_3 = new UnitTestFile("/usr/home/test.file");

        fileWatch.add(file_1);
        fileWatch.add(file_2);
        fileWatch.add(file_3);

        assertEquals(3, fileWatch.getNumFiles());

        UnitTestFileListener listener = new UnitTestFileListener();
        fileWatch.addListener(listener);
        assertEquals(0, listener.getNumChangedFiles());

        file_2.setLastModified(file_2.lastModified() + 4);
        file_3.setLastModified(file_3.lastModified() + 4);

        fileWatch.checkForModifiedFiles();
        assertEquals(2, listener.getNumChangedFiles());

        fileWatch.checkForModifiedFiles();
        assertEquals(0, listener.getNumChangedFiles());

        file_1.setLastModified(file_1.lastModified() + 4);
        fileWatch.checkForModifiedFiles();
        assertEquals(1, listener.getNumChangedFiles());
    }

    /** @noinspection MagicNumber*/
    public void testTimerWorks() {
        UnitTestFile file = new UnitTestFile("C:\\hier\\a.file");

        fileWatch.add(file);
        UnitTestFileListener listener = new UnitTestFileListener();
        fileWatch.addListener(listener);

        fileWatch.start(50);
        file.setLastModified(file.lastModified() + 25);

        try {
            Thread.sleep(60);
        } catch (InterruptedException ignore) {
        }

        assertEquals(1, listener.getNumChangedFiles());
        fileWatch.stop();
    }

    /** @noinspection MagicNumber*/
    public void testTimerStopDoesNotFailWhenClickedTwice() {
        fileWatch.start(50);
        fileWatch.stop();
        fileWatch.stop();
    }
}
