# Java Config Utils

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://www.oracle.com/java/)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.fyc/java-config-utils.svg)](https://mvnrepository.com/artifact/com.github.fyc/java-config-utils)

A lightweight Java library for simplified application configuration management. Java Config Utils provides a unified API to work with multiple configuration formats including Properties, YAML, and JSON, with support for environment variables, hot reloading, and type-safe configuration access.

## ✨ Features

- 🔧 **Multi-format support**: Properties, YAML, JSON configuration files
- 🌍 **Environment variables**: Seamless integration with system environment variables
- 🔄 **Hot reloading**: Automatically reload configuration changes without restarting
- 🛡️ **Type safety**: Type-safe configuration access with validation
- ⚡ **Lightweight**: Minimal dependencies and fast performance
- 📝 **Easy to use**: Simple and intuitive API
- 🔍 **Default values**: Built-in support for default values and validation

## 🚀 Quick Start

### Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.fyc</groupId>
    <artifactId>java-config-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

```java
import com.github.afyc.configutils.ConfigManager;
import com.github.afyc.configutils.Config;

// Load configuration from file
ConfigManager manager = ConfigManager.builder()
    .addConfigFile("application.yml")
    .addConfigFile("database.properties")
    .enableEnvironmentVariables()
    .enableHotReload()
    .build();

Config config = manager.getConfig();

// Type-safe access
String dbHost = config.getString("database.host", "localhost");
int dbPort = config.getInt("database.port", 5432);
boolean enableCache = config.getBoolean("cache.enabled", false);

// Nested configuration
DatabaseConfig dbConfig = config.getObject("database", DatabaseConfig.class);
```

### Configuration Files Examples

**application.yml**
```yaml
database:
  host: ${DB_HOST:localhost}
  port: ${DB_PORT:5432}
  username: ${DB_USER:admin}
  password: ${DB_PASSWORD:secret}

cache:
  enabled: true
  ttl: 3600

logging:
  level: INFO
  file: app.log
```

**database.properties**
```properties
database.driver=postgresql
database.maxConnections=20
database.timeout=30000
```

## 📖 Documentation

### Configuration Sources Priority

The library follows this priority order (highest to lowest):

1. Environment variables
2. System properties
3. Configuration files (in order they were added)
4. Default values

### Supported Data Types

- `String` - getString(key, defaultValue)
- `Integer` - getInt(key, defaultValue)
- `Long` - getLong(key, defaultValue)
- `Double` - getDouble(key, defaultValue)
- `Boolean` - getBoolean(key, defaultValue)
- `List<T>` - getList(key, type, defaultValue)
- `Custom Objects` - getObject(key, clazz)

### Environment Variable Substitution

Use `${VAR_NAME:default_value}` syntax in your configuration files:

```yaml
app:
  name: ${APP_NAME:MyApplication}
  port: ${PORT:8080}
  debug: ${DEBUG:false}
```

### Hot Reload

Enable hot reload to automatically pick up configuration changes:

```java
ConfigManager manager = ConfigManager.builder()
    .addConfigFile("application.yml")
    .enableHotReload(5000) // Check every 5 seconds
    .onConfigChange(config -> {
        System.out.println("Configuration updated!");
    })
    .build();
```

## 🏗️ Building from Source

```bash
git clone https://github.com/fyc/java-config-utils.git
cd java-config-utils
mvn clean install
```

## 🧪 Running Tests

```bash
mvn test
```

## 📝 Examples

Check out the [examples](examples/) directory for more detailed usage examples:

- [Basic Configuration](examples/BasicExample.java)
- [Database Configuration](examples/DatabaseExample.java)
- [Hot Reload Example](examples/HotReloadExample.java)
- [Custom Object Mapping](examples/CustomObjectExample.java)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Thanks to the open source community for inspiration and feedback
- Built with ❤️ for Java developers who need simple configuration management

## 📬 Support

If you have any questions or need help, please:

1. Check the [examples](examples/) directory
2. Open an issue on GitHub
3. Contact the maintainer

---

**Made with ❤️ by [fyc](https://github.com/fyc)**
