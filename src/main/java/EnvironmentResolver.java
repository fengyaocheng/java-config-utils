
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for resolving environment variables in configuration values.
 */
public class EnvironmentResolver {
    
    // Pattern to match ${VAR_NAME:default_value} or ${VAR_NAME}
    private static final Pattern ENV_VAR_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?\\}");
    
    /**
     * Resolve environment variables in the configuration map.
     */
    public static Map<String, Object> resolve(Map<String, Object> config) {
        Map<String, Object> resolved = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            resolved.put(entry.getKey(), resolveValue(entry.getValue()));
        }
        
        return resolved;
    }
    
    @SuppressWarnings("unchecked")
    private static Object resolveValue(Object value) {
        if (value instanceof String) {
            return resolveString((String) value);
        } else if (value instanceof Map) {
            return resolve((Map<String, Object>) value);
        } else if (value instanceof java.util.List) {
            java.util.List<Object> list = (java.util.List<Object>) value;
            java.util.List<Object> resolvedList = new java.util.ArrayList<>();
            for (Object item : list) {
                resolvedList.add(resolveValue(item));
            }
            return resolvedList;
        }
        
        return value;
    }
    
    private static String resolveString(String value) {
        if (value == null) {
            return null;
        }
        
        Matcher matcher = ENV_VAR_PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String defaultValue = matcher.group(2);
            
            String envValue = System.getenv(varName);
            if (envValue == null) {
                envValue = System.getProperty(varName);
            }
            
            String replacement = (envValue != null) ? envValue : (defaultValue != null ? defaultValue : "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(sb);
        return sb.toString();
    }
}