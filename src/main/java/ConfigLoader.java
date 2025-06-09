package com.github.a20118dfd.configutils.loader;

import java.nio.file.Path;
import java.util.Map;

/**
 * Interface for configuration file loaders.
 */
public interface ConfigLoader {
    
    /**
     * Load configuration from a file.
     * 
     * @param path the path to the configuration file
     * @return a map containing the configuration data
     * @throws Exception if loading fails
     */
    Map<String, Object> load(Path path) throws Exception;
}