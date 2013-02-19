package net.letscode.game.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.File;
import java.io.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles loading and saving of server configuration in YAML files.
 * @author timothyb89
 */
public class Config {
	
	public static final File CONFIG_FILE = new File("config.yml");
	
	private static Config instance;
	
	private Logger logger = LoggerFactory.getLogger(Config.class);
	
	private ConfigBase config;
	
	private Config() {
		_load();
	}
	
	private void _load() {
		logger.debug("Loading configuration from " + CONFIG_FILE);
		try {
			FileReader reader = new FileReader(CONFIG_FILE);
			config = new YamlReader(reader).read(ConfigBase.class);
			reader.close();
		} catch (Exception ex) {
			logger.error("Failed to load configuration from " + CONFIG_FILE, ex);
		}
	}
	
	public static void load() {
		instance._load();
	}
	
	public static ConfigBase get() {
		if (instance == null) {
			instance = new Config();
		}
		
		return instance.config;
	}
	
	public static class ConfigBase {

		public ConfigBase() {
		}
		
		public ServerConfig server;
		public DatabaseConfig database;
		
	}
	
	public static class ServerConfig {
		
		public int port;
		public String webDirectory = "/blahblahblah";
		
	}
	
	public static class DatabaseConfig {
		
		public String uri;
		public String username;
		public String password;
		
	}
	
	public static void main(String[] args) {
		ConfigBase base = Config.get();
		System.out.println(base);
		System.out.println("\tserver: " + base.server);
		System.out.println("\t\tport: " + base.server.port);
		System.out.println("\t\twebDirectory: " + base.server.webDirectory);
		System.out.println("\tdatabase: " + base.database);
		System.out.println("\t\turi: " + base.database.uri);
		System.out.println("\t\tusername: " + base.database.username);
	}
	
}
