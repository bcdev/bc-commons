package com.bc.util.watch;

import java.io.File;


public interface DirectoryWatchListener {

    void filesAdded(File[] files);
    void filesRemoved(File[] files);
}
