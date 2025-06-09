
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Configuration access class that provides type-safe access to configuration values.
 */
public class Config {
    private final Map<String, Object> configMap;
    private final ObjectMapper objectMapper;
    
    public Config(Map<String, Object> configMap) {
        this.configMap = new HashMap<>(configMap);
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get a string value from configuration.
     */
    public String getString(String key, String defaultValue) {
        Object value = getValue(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }
    
    /**
     * Get an integer value from configuration.
     */
    public int getInt(String key, int defaultValue) {
        Object value = getValue(key);
        if (value == null) return defaultValue;
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get a long value from configuration.
     */
    public long getLong(String key, long defaultValue) {
        Object value = getValue(key);
        if (value == null) return defaultValue;
        
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get a double value from configuration.
     */
    public double getDouble(String key, double defaultValue) {
        Object value = getValue(key);
        if (value == null) return defaultValue;
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get a boolean value from configuration.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = getValue(key);
        if (value == null) return defaultValue;
        
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        String stringValue = String.valueOf(value).toLowerCase().trim();
        return "true".equals(stringValue) || "yes".equals(stringValue) || "1".equals(stringValue);
    }
    
    /**
     * Get a list of values from configuration.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> elementType, List<T> defaultValue) {
        Object value = getValue(key);
        if (value == null) return defaultValue;
        
        if (value instanceof List) {
            List<T> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (elementType.isInstance(item)) {
                    result.add(elementType.cast(item));
                } else {
                    // Try to convert
                    try {
                        T converted = objectMapper.convertValue(item, elementType);
                        result.add(converted);
                    } catch (Exception e) {
                        // Skip items that can't be converted
                    }
                }
            }
            return result;
        }
        
        return defaultValue;
    }
    
    /**
     * Get a custom object from configuration.
     */
    public <T> T getObject(String key, Class<T> type) {
        Object value = getValue(key);
        if (value == null) return null;
        
        try {
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert configuration value to " + type.getSimpleName(), e);
        }
    }
    
    /**
     * Check if a configuration key exists.
     */
    public boolean hasKey(String key) {
        return getValue(key) != null;
    }
    
    /**
     * Get all configuration keys.
     */
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        collectKeys(configMap, "", keys);
        return keys;
    }
    
    private void collectKeys(Map<String, Object> map, String prefix, Set<String> keys) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);
            
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                collectKeys(nestedMap, key, keys);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private Object getValue(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        
        String[] keyParts = key.split("\\.");
        Map<String, Object> current = configMap;
        
        for (int i = 0; i < keyParts.length - 1; i++) {
            Object value = current.get(keyParts[i]);
            if (!(value instanceof Map)) {
                return null;
            }
            current = (Map<String, Object>) value;
        }
        
        return current.get(keyParts[keyParts.length - 1]);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;
        Config config = (Config) o;
        return Objects.equals(configMap, config.configMap);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(configMap);
    }
}