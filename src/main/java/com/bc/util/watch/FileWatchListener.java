package com.bc.util.watch;

import java.io.File;

public interface FileWatchListener {

    void filesChanged(File[] filesChanged);
}
