package net.letscode.game.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles loading and saving of server configuration in YAML files.
 * @author timothyb89
 */
@Slf4j
public class Config {
	
	public static final File CONFIG_FILE = new File("config.yml");
	
	private static Config instance;
	
	private ConfigBase config;
	
	private Config() {
		_load();
	}
	
	private void _load() {
		log.debug("Loading configuration from " + CONFIG_FILE);
		try (FileReader reader = new FileReader(CONFIG_FILE)) {
			config = new YamlReader(reader).read(ConfigBase.class);
		} catch (Exception ex) {
			log.error("Failed to load configuration from " + CONFIG_FILE
					+ ", defaults will be used.", ex);
			config = new ConfigBase();
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

		public ServerConfig server;
		public DatabaseConfig database;
		public PrefixesConfig prefixes;
		
		public ConfigBase() {
			server = new ServerConfig();
			database = new DatabaseConfig();
			prefixes = new PrefixesConfig();
		}
		
	}
	
	public static class ServerConfig {
		
		public int port = 8333;
		public String webDirectory = "./web";
		public long tickRate = 15;
		
	}
	
	public static class DatabaseConfig {
		
		public String uri;
		public String username;
		public String password;
		
	}
	
	public static class PrefixesConfig {
	
		public List<String> mappings = new ArrayList() {{
			// defaults
			add("net.letscode.game");
		}};
		
		public List<String> messageHandlers = new ArrayList() {{
			add("net.letscode.game.server.message");
		}};
		
		public List<String> responseHandlers = new ArrayList() {{
			add("net.letscode.game.server.message.response");
		}};
		
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
		
		System.out.println("\tprefixes: " + base.prefixes);
		
		System.out.println("\t\tmappings: ");
		for (String s : base.prefixes.mappings) {
			System.out.println("\t\t\t" + s);
		}
		
		System.out.println("\t\tresponseHandlers: ");
		for (String s : base.prefixes.responseHandlers) {
			System.out.println("\t\t\t" + s);
		}
	}
	
}
