package com.bc.util.watch;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class DirectoryWatch {

    public DirectoryWatch() {
        directoryList = new ArrayList();
        watchedFiles = new ArrayList();
        listeners = new ArrayList();
        directoriesContent = new ArrayList();
        addedFileList = new ArrayList();
        removedFileList = new ArrayList();
    }

    synchronized public int getNumDirectories() {
        return directoryList.size();
    }

    synchronized public void add(File dir) {
        if (dir != null) {
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("The argument: " + dir.getPath() + " is not a directory");
            }
            if (getDirectoryIndex(dir) < 0) {
                directoryList.add(dir);
            }
        }
    }

    synchronized public void remove(File dir) {
        final int index = getDirectoryIndex(dir);
        if (index >= 0) {
            directoryList.remove(index);

            ArrayList toDelete = new ArrayList();
            for (Iterator iterator = directoriesContent.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();                
                if (dir.equals(file.getParentFile())) {
                    toDelete.add(file);
                }
            }

            for (Iterator iterator = toDelete.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                directoriesContent.remove(file);
            }
        }
    }

    synchronized public int getNumListeners() {
        return listeners.size();
    }

    synchronized public void addListener(DirectoryWatchListener listener) {
        if ((listener != null) && (!listeners.contains(listener))) {
            listeners.add(listener);
        }
    }

    synchronized public void removeListener(DirectoryWatchListener listener) {
        listeners.remove(listener);
    }

    synchronized public File[] getDirectoriesContent() {
        File[] result = new File[directoriesContent.size()];
        return (File[]) directoriesContent.toArray(result);
    }

    public void start(long rate) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new DirectoryWatchTask(), 0, rate);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private final ArrayList directoryList;
    private final ArrayList watchedFiles;
    private final ArrayList directoriesContent;
    private final ArrayList listeners;
    private final ArrayList addedFileList;
    private final ArrayList removedFileList;

    private Timer timer;

    synchronized File[] getWatchedFiles() {
        File[] result = new File[watchedFiles.size()];
        int idx = 0;
        for (Iterator iterator = watchedFiles.iterator(); iterator.hasNext();) {
            FileContainer container = (FileContainer) iterator.next();
            result[idx] = container.getFile();
            ++idx;
        }
        return result;
    }

    synchronized void checkDirectories() {
        addNewFilesToWatch();
        checkForFileChanges();
        checkForRemovedFiles();
        notifyListeners();
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PROTECTED
    ///////////////////////////////////////////////////////////////////////////

    private int getDirectoryIndex(File dir) {
        return directoryList.indexOf(dir);
    }

    private void checkForRemovedFiles() {
        removedFileList.clear();
        for (Iterator iterator = directoriesContent.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (!file.exists()) {
                removedFileList.add(file);
            }
        }

        for (Iterator iterator = removedFileList.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            directoriesContent.remove(file);
        }
    }

    private void checkForFileChanges() {
        final ArrayList stableFiles = new ArrayList();

        addedFileList.clear();
        for (Iterator iterator = watchedFiles.iterator(); iterator.hasNext();) {
            final FileContainer container = (FileContainer) iterator.next();
            final File file = container.getFile();
            final long length = file.length();
            final long lastModified = file.lastModified();

            if ((container.getLastModified() == lastModified)
                    && (container.getSize() == length)) {
                container.setStableCount(1 + container.getStableCount());
                if (container.getStableCount() > 2) {
                    stableFiles.add(file);
                }
            } else {
                container.setLastModified(lastModified);
                container.setSize(length);
                container.setStableCount(0);
            }
        }

        FileContainer comparer = new FileContainer();
        for (Iterator iterator = stableFiles.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();

            addedFileList.add(file);
            directoriesContent.add(file);
            comparer.setFile(file);
            watchedFiles.remove(comparer);
        }
    }

    private void notifyListeners() {
        if (addedFileList.size() > 0) {
            File[] added = new File[addedFileList.size()];
            added = (File[]) addedFileList.toArray(added);

            for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                DirectoryWatchListener listener = (DirectoryWatchListener) iterator.next();
                listener.filesAdded(added);
            }
            addedFileList.clear();
        }

        if (removedFileList.size() > 0) {
            File[] removed = new File[removedFileList.size()];
            removed = (File[]) removedFileList.toArray(removed);

            for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                DirectoryWatchListener listener = (DirectoryWatchListener) iterator.next();
                listener.filesRemoved(removed);
            }

            removedFileList.clear();
        }
    }

    private void addNewFilesToWatch() {
        final FileContainer comparer = new FileContainer();

        for (Iterator iterator = directoryList.iterator(); iterator.hasNext();) {
            File dir = (File) iterator.next();
            File[] fileArray = dir.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                File file = fileArray[i];

                if (!directoriesContent.contains(file)) {
                    comparer.setFile(file);
                    if (!watchedFiles.contains(comparer)) {
                        final FileContainer container = new FileContainer();
                        container.setFile(file);
                        container.setLastModified(file.lastModified());
                        container.setSize(file.length());
                        container.setStableCount(0);
                        watchedFiles.add(container);
                    }
                }
            }
        }
    }

    private class FileContainer {
        private File file;
        private long lastModified;
        private long size;
        private int stableCount;

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public int getStableCount() {
            return stableCount;
        }

        public void setStableCount(int stableCount) {
            this.stableCount = stableCount;
        }

        /**
         * @noinspection InstanceofInterfaces
         */
        public boolean equals(Object obj) {
            boolean result = false;

            if (obj instanceof FileContainer) {
                result = this.file.equals(((FileContainer) obj).getFile());
            }

            return result;
        }
    }

    private class DirectoryWatchTask extends TimerTask {
        /**
         * The action to be performed by this timer task.
         */
        public void run() {
            checkDirectories();
        }
    }
}
