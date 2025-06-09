
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration loader for JSON files.
 */
public class JsonConfigLoader implements com.github.a20118dfd.configutils.loader.ConfigLoader {
    
    private final ObjectMapper objectMapper;
    
    public JsonConfigLoader() {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public Map<String, Object> load(Path path) throws Exception {
        try {
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> data = objectMapper.readValue(path.toFile(), typeRef);
            return data != null ? data : new HashMap<>();
        } catch (IOException e) {
            throw new Exception("Failed to load JSON file: " + path, e);
        }
    }
}