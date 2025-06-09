
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration loader for YAML files.
 */
public class YamlConfigLoader implements com.github.a20118dfd.configutils.loader.ConfigLoader {
    
    private final Yaml yaml;
    
    public YamlConfigLoader() {
        this.yaml = new Yaml();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> load(Path path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            Object data = yaml.load(fis);
            
            if (data instanceof Map) {
                return (Map<String, Object>) data;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            throw new Exception("Failed to load YAML file: " + path, e);
        }
    }
}