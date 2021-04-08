package com.esberfes.plugin.core;

import java.io.File;
import java.nio.file.WatchEvent;

public interface FileHandler {

    /**
     * Method is invoked post file event is detected
     *
     * @param file
     * @param fileEvent
     * @throws InterruptedException
     */
    void handle(File file, WatchEvent.Kind<?> fileEvent) throws InterruptedException;
}