package com.bc.util.watch;

import java.io.File;

class UnitTestFileListener implements FileWatchListener {

    public int getNumChangedFiles() {
        int numChanged = changed.length;
        changed = new File[0];
        return numChanged;
    }

    public void filesChanged(File[] filesChanged) {
        changed = filesChanged;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private File[] changed;

    UnitTestFileListener() {
        changed = new File[0];
    }
}
