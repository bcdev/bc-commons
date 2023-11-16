package com.bc.util.watch;

import com.bc.util.io.UnitTestDir;
import com.bc.util.io.UnitTestFile;
import junit.framework.TestCase;

import java.io.File;


public class DirectoryWatchTest extends TestCase {

    public void testDirectoriesCanBeAdded() {
        UnitTestDir dir = new UnitTestDir("somewhere/over/the/rainbow");

        assertEquals(0, watch.getNumDirectories());

        watch.add(dir);
        assertEquals(1, watch.getNumDirectories());

        watch.add(null);
        assertEquals(1, watch.getNumDirectories());
    }

    public void testSameDirectoryCannotBeAddedTwice() {
        UnitTestDir dir = new UnitTestDir("C:\\Test\\Directory");

        assertEquals(0, watch.getNumDirectories());

        watch.add(dir);
        assertEquals(1, watch.getNumDirectories());

        watch.add(dir);
        assertEquals(1, watch.getNumDirectories());
    }

    public void testAddingOtherThanDirectoryFails() {
        UnitTestDir dir = new UnitTestDir("C:\\Test\\Directory");
        dir.setIsDirectory(false);

        try {
            watch.add(dir);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testRemoveDirectory() {
        UnitTestDir dir = new UnitTestDir("/usr/local/popocal");

        assertEquals(0, watch.getNumDirectories());

        watch.add(dir);
        assertEquals(1, watch.getNumDirectories());

        watch.remove(dir);
        assertEquals(0, watch.getNumDirectories());
    }

    public void testRemoveDirectoryAlsoRemovesAllFilesContained() {
        UnitTestDir dir = new UnitTestDir("F:/drive_c");
        UnitTestFile file_1 = createFileInDir(dir, "testi.txt");
        UnitTestFile file_2 = createFileInDir(dir, "testomat.txt");

        dir.addFile(file_1);
        dir.addFile(file_2);

        watch.add(dir);
        triggerWatchTilFilesAreStable();

        File[] content = watch.getDirectoriesContent();
        assertEquals(2, content.length);

        watch.remove(dir);
        content = watch.getDirectoriesContent();
        assertEquals(0, content.length);
    }

    public void testRemoveIllegalDirectories() {
        UnitTestDir dir_1 = new UnitTestDir("/usr/muser");
        UnitTestDir dir_2 = new UnitTestDir("F:\\ile");

        assertEquals(0, watch.getNumDirectories());

        watch.add(dir_1);
        assertEquals(1, watch.getNumDirectories());

        watch.remove(null);
        assertEquals(1, watch.getNumDirectories());

        watch.remove(dir_2);
        assertEquals(1, watch.getNumDirectories());
    }

    public void testThatFilesAreWatched() {
        File[] list;

        list = watch.getWatchedFiles();
        assertEquals(0, list.length);

        watch.checkDirectories();

        UnitTestDir dir = new UnitTestDir("here");
        UnitTestFile file_1 = new UnitTestFile("testi.txt");
        UnitTestFile file_2 = new UnitTestFile("nocheiner.txt");
        dir.addFile(file_1);

        watch.add(dir);

        watch.checkDirectories();
        list = watch.getWatchedFiles();
        assertEquals(1, list.length);

        dir.addFile(file_2);
        watch.checkDirectories();
        list = watch.getWatchedFiles();
        assertEquals(2, list.length);

        assertEquals(file_1.getPath(), list[0].getPath());
        assertEquals(file_2.getPath(), list[1].getPath());
    }

    public void testListenersCanBeAddedAndRemoved() {
        UnitTestDirListener listener_1 = new UnitTestDirListener();
        UnitTestDirListener listener_2 = new UnitTestDirListener();

        assertEquals(0, watch.getNumListeners());

        watch.addListener(listener_1);
        assertEquals(1, watch.getNumListeners());

        watch.addListener(listener_2);
        assertEquals(2, watch.getNumListeners());

        watch.removeListener(listener_2);
        assertEquals(1, watch.getNumListeners());

        watch.removeListener(listener_1);
        assertEquals(0, watch.getNumListeners());
    }

    public void testNullCannotBeUsedAsListener() {
        UnitTestDirListener listener = new UnitTestDirListener();
        assertEquals(0, watch.getNumListeners());

        watch.addListener(null);
        assertEquals(0, watch.getNumListeners());

        watch.addListener(listener);
        assertEquals(1, watch.getNumListeners());

        watch.removeListener(null);
        assertEquals(1, watch.getNumListeners());
    }

    public void testSameListenerCannotAttachOrRemoveTwice() {
        UnitTestDirListener listener_1 = new UnitTestDirListener();
        UnitTestDirListener listener_2 = new UnitTestDirListener();

        watch.addListener(listener_1);
        watch.addListener(listener_2);
        assertEquals(2, watch.getNumListeners());

        watch.addListener(listener_2);
        assertEquals(2, watch.getNumListeners());

        watch.removeListener(listener_2);
        assertEquals(1, watch.getNumListeners());

        watch.removeListener(listener_2);
        assertEquals(1, watch.getNumListeners());
    }

    public void testWatchedFileThatDoNotChangeAnymoreNotifyListeners() {
        UnitTestDir dir = new UnitTestDir("here");
        UnitTestFile file_1 = new UnitTestFile("testi.txt");
        File[] list;
        UnitTestDirListener listener = new UnitTestDirListener();

        dir.addFile(file_1);
        watch.add(dir);
        watch.addListener(listener);

        watch.checkDirectories();
        list = watch.getWatchedFiles();
        assertEquals(1, list.length);

        watch.checkDirectories();
        watch.checkDirectories();

        assertEquals(1, listener.getNumAddedFiles());
    }

    /**
     * @noinspection MagicNumber
     */
    public void testNewFilesWithChangingLengthDoNotTriggerNotifies() {
        UnitTestDir dir = new UnitTestDir("here");
        UnitTestFile file = new UnitTestFile("testi.txt");
        File[] list;
        UnitTestDirListener listener = new UnitTestDirListener();

        dir.addFile(file);
        watch.add(dir);
        watch.addListener(listener);

        watch.checkDirectories();
        list = watch.getWatchedFiles();
        assertEquals(1, list.length);

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        file.setLength(file.length() + 108);
        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        file.setLength(file.length() + 108);
        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        // notify when file is stable after third iteration after the last change
        watch.checkDirectories();
        assertEquals(1, listener.getNumAddedFiles());
    }

    /**
     * @noinspection MagicNumber
     */
    public void testNewFilesWithChangingMofdificationDateDoNotTriggerNotifies() {
        UnitTestDir dir = new UnitTestDir("here");
        UnitTestFile file = new UnitTestFile("testi.txt");
        File[] list;
        UnitTestDirListener listener = new UnitTestDirListener();

        dir.addFile(file);
        watch.add(dir);
        watch.addListener(listener);

        watch.checkDirectories();
        list = watch.getWatchedFiles();
        assertEquals(1, list.length);

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        file.setLastModified(file.lastModified() + 12);
        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        file.setLastModified(file.lastModified() + 34);
        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        watch.checkDirectories();
        assertEquals(0, listener.getNumAddedFiles());

        // notify when file is stable after third iteration after the last change
        watch.checkDirectories();
        assertEquals(1, listener.getNumAddedFiles());
    }

    public void testStableFilesAreWatchedInDirectoriesContent() {
        UnitTestDir dir_1 = new UnitTestDir("here");
        UnitTestFile file_1 = new UnitTestFile("testi.txt");
        UnitTestDir dir_2 = new UnitTestDir("there");
        UnitTestFile file_2 = new UnitTestFile("testi.tom");

        dir_1.addFile(file_1);
        dir_2.addFile(file_2);

        watch.add(dir_1);
        watch.add(dir_2);
        triggerWatchTilFilesAreStable();

        File[] content = watch.getDirectoriesContent();
        assertNotNull(content);
        assertEquals(2, content.length);

        assertEquals(file_1.getPath(), content[0].getPath());
        assertEquals(file_2.getPath(), content[1].getPath());
    }

    public void testListenersAreNotifiedOnlyOnceOnAddedFiles() {
        UnitTestDirListener listener = new UnitTestDirListener();
        UnitTestDir dir = new UnitTestDir("C:(nasenmann");
        UnitTestFile file = new UnitTestFile("whatafile.txt");

        dir.addFile(file);

        watch.addListener(listener);
        watch.add(dir);

        triggerWatchTilFilesAreStable();
        watch.checkDirectories();
        watch.checkDirectories();

        assertEquals(1, listener.getNumAddedFiles());
    }


    public void testListenersAreNotifiedOnRemovedFiles() {
        UnitTestDirListener listener = new UnitTestDirListener();
        UnitTestDir dir_1 = new UnitTestDir("here");
        UnitTestFile file_1 = new UnitTestFile("testi.txt");
        UnitTestDir dir_2 = new UnitTestDir("there");
        UnitTestFile file_2 = new UnitTestFile("testi.tom");

        dir_1.addFile(file_1);
        dir_2.addFile(file_2);

        watch.add(dir_1);
        watch.add(dir_2);
        watch.addListener(listener);
        triggerWatchTilFilesAreStable();

        file_2.setExists(false);
        watch.checkDirectories();
        assertEquals(1, listener.getNumRemovedFiles());

        watch.checkDirectories();
        assertEquals(0, listener.getNumRemovedFiles());
    }

    /**
     * @noinspection MagicNumber
     */
    public void testTimerIsWorking() {
        UnitTestDirListener listener = new UnitTestDirListener();
        UnitTestDir dir = new UnitTestDir("here/it/is");
        UnitTestFile file_1 = new UnitTestFile("testomat.txt");
        UnitTestFile file_2 = new UnitTestFile("junitstuff.txt");
        dir.addFile(file_1);

        watch.add(dir);
        watch.addListener(listener);
        watch.start(50);

        dir.addFile(file_2);

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) {
        }

        assertEquals(2, listener.getNumAddedFiles());
        watch.stop();
    }

    /**
     * @noinspection MagicNumber
     */
    public void testWatchDoesNotFailWhenStoppedTwice() {
        watch.start(50);
        watch.stop();
        watch.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private DirectoryWatch watch;

    protected void setUp() {
        watch = new DirectoryWatch();
    }

    private void triggerWatchTilFilesAreStable() {
        watch.checkDirectories();
        watch.checkDirectories();
        watch.checkDirectories();
    }

    private UnitTestFile createFileInDir(UnitTestDir dir, String fileName) {
        final UnitTestFile file = new UnitTestFile(dir, fileName);
        dir.addFile(file);
        return file;
    }
}
