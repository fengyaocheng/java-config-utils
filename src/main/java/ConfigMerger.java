
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for merging configuration maps.
 */
public class ConfigMerger {
    
    /**
     * Merge two configuration maps. Values from the second map override values from the first map.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(Map<String, Object> base, Map<String, Object> override) {
        Map<String, Object> result = new HashMap<>(base);
        
        for (Map.Entry<String, Object> entry : override.entrySet()) {
            String key = entry.getKey();
            Object overrideValue = entry.getValue();
            Object baseValue = result.get(key);
            
            if (baseValue instanceof Map && overrideValue instanceof Map) {
                // Both values are maps, merge them recursively
                Map<String, Object> mergedMap = merge(
                    (Map<String, Object>) baseValue,
                    (Map<String, Object>) overrideValue
                );
                result.put(key, mergedMap);
            } else {
                // Override the base value
                result.put(key, overrideValue);
            }
        }
        
        return result;
    }
}