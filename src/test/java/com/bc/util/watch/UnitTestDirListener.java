package com.bc.util.watch;

import java.io.File;
import java.util.ArrayList;


class UnitTestDirListener implements DirectoryWatchListener {

    public void filesAdded(File[] files) {
        for (int i = 0; i < files.length; i++) {
            addedList.add(files[i]);
        }
    }

    public void filesRemoved(File[] files) {
        for (int i = 0; i < files.length; i++) {
            removedList.add(files[i]);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private ArrayList addedList;
    private ArrayList removedList;

    UnitTestDirListener() {
        addedList = new ArrayList();
        removedList = new ArrayList();
    }

    int getNumAddedFiles() {
        final int length = addedList.size();
        addedList.clear();
        return length;
    }

    int getNumRemovedFiles() {
        final int length = removedList.size();
        removedList.clear();
        return length;
    }
}
