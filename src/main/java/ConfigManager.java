
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Main configuration manager that handles loading, merging, and monitoring configuration files.
 */
public class ConfigManager {
    private final List<String> configFiles;
    private final boolean enableEnvironmentVariables;
    private final boolean enableHotReload;
    private final long hotReloadInterval;
    private final Consumer<Config> changeListener;
    private final Map<String, com.github.a20118dfd.configutils.loader.ConfigLoader> loaders;
    private final Map<String, Long> lastModified;
    
    private Config currentConfig;
    private ScheduledExecutorService scheduler;
    
    private ConfigManager(Builder builder) {
        this.configFiles = new ArrayList<>(builder.configFiles);
        this.enableEnvironmentVariables = builder.enableEnvironmentVariables;
        this.enableHotReload = builder.enableHotReload;
        this.hotReloadInterval = builder.hotReloadInterval;
        this.changeListener = builder.changeListener;
        this.lastModified = new HashMap<>();
        
        this.loaders = new HashMap<>();
        this.loaders.put("properties", new com.github.a20118dfd.configutils.loader.PropertiesConfigLoader());
        this.loaders.put("yml", new YamlConfigLoader());
        this.loaders.put("yaml", new YamlConfigLoader());
        this.loaders.put("json", new JsonConfigLoader());
        
        loadConfiguration();
        
        if (enableHotReload) {
            startHotReload();
        }
    }
    
    /**
     * Get the current configuration.
     */
    public Config getConfig() {
        return currentConfig;
    }
    
    /**
     * Reload configuration from all sources.
     */
    public synchronized void reload() {
        loadConfiguration();
    }
    
    /**
     * Shutdown the configuration manager and stop hot reload if enabled.
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    private void loadConfiguration() {
        Map<String, Object> mergedConfig = new HashMap<>();
        
        // Load configuration files
        for (String configFile : configFiles) {
            try {
                Map<String, Object> config = loadConfigFile(configFile);
                mergedConfig = ConfigMerger.merge(mergedConfig, config);
            } catch (Exception e) {
                System.err.println("Failed to load config file: " + configFile + " - " + e.getMessage());
            }
        }
        
        // Apply environment variables
        if (enableEnvironmentVariables) {
            mergedConfig = EnvironmentResolver.resolve(mergedConfig);
        }
        
        Config newConfig = new Config(mergedConfig);
        
        // Notify change listener if config changed
        if (changeListener != null && !Objects.equals(currentConfig, newConfig)) {
            changeListener.accept(newConfig);
        }
        
        this.currentConfig = newConfig;
    }
    
    private Map<String, Object> loadConfigFile(String configFile) throws Exception {
        Path path = Paths.get(configFile);
        File file = path.toFile();
        
        if (!file.exists()) {
            throw new IllegalArgumentException("Configuration file not found: " + configFile);
        }
        
        String extension = getFileExtension(configFile);
        com.github.a20118dfd.configutils.loader.ConfigLoader loader = loaders.get(extension.toLowerCase());
        
        if (loader == null) {
            throw new IllegalArgumentException("Unsupported configuration file format: " + extension);
        }
        
        // Update last modified time
        lastModified.put(configFile, file.lastModified());
        
        return loader.load(path);
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
    
    private void startHotReload() {
        scheduler = Executors.newScheduledThreadPool(1);
        
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                boolean configChanged = false;
                
                for (String configFile : configFiles) {
                    File file = new File(configFile);
                    if (file.exists()) {
                        long currentModified = file.lastModified();
                        Long previousModified = lastModified.get(configFile);
                        
                        if (previousModified == null || currentModified > previousModified) {
                            configChanged = true;
                            break;
                        }
                    }
                }
                
                if (configChanged) {
                    loadConfiguration();
                }
            } catch (Exception e) {
                System.err.println("Error during hot reload: " + e.getMessage());
            }
        }, hotReloadInterval, hotReloadInterval, TimeUnit.MILLISECONDS);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<String> configFiles = new ArrayList<>();
        private boolean enableEnvironmentVariables = false;
        private boolean enableHotReload = false;
        private long hotReloadInterval = 5000; // 5 seconds
        private Consumer<Config> changeListener;
        
        public Builder addConfigFile(String configFile) {
            this.configFiles.add(configFile);
            return this;
        }
        
        public Builder enableEnvironmentVariables() {
            this.enableEnvironmentVariables = true;
            return this;
        }
        
        public Builder enableHotReload() {
            this.enableHotReload = true;
            return this;
        }
        
        public Builder enableHotReload(long intervalMs) {
            this.enableHotReload = true;
            this.hotReloadInterval = intervalMs;
            return this;
        }
        
        public Builder onConfigChange(Consumer<Config> listener) {
            this.changeListener = listener;
            return this;
        }
        
        public ConfigManager build() {
            if (configFiles.isEmpty()) {
                throw new IllegalArgumentException("At least one configuration file must be specified");
            }
            return new ConfigManager(this);
        }
    }
}