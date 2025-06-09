package com.github.a20118dfd.configutils.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader for Properties files.
 */
public class PropertiesConfigLoader implements com.github.a20118dfd.configutils.loader.ConfigLoader {
    
    @Override
    public Map<String, Object> load(Path path) throws Exception {
        Properties properties = new Properties();
        
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            properties.load(fis);
        } catch (IOException e) {
            throw new Exception("Failed to load properties file: " + path, e);
        }
        
        return convertToNestedMap(properties);
    }
    
    /**
     * Convert flat properties to nested map structure.
     * Example: "database.host" -> {database: {host: value}}
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToNestedMap(Properties properties) {
        Map<String, Object> result = new HashMap<>();
        
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            setNestedValue(result, key, value);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private void setNestedValue(Map<String, Object> map, String key, Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object existing = current.get(part);
            
            if (!(existing instanceof Map)) {
                existing = new HashMap<String, Object>();
                current.put(part, existing);
            }
            
            current = (Map<String, Object>) existing;
        }
        
        current.put(parts[parts.length - 1], value);
    }
}