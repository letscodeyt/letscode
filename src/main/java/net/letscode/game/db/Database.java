package net.letscode.game.db;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import java.util.List;
import net.letscode.game.auth.User;
import net.letscode.game.auth.shiro.ShiroUser;
import net.letscode.game.config.Config;
import net.letscode.game.config.Config.DatabaseConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A database wrapper class, intended to abstract as much of the database as
 * possible from other code.
 * @author timothyb89
 */
public class Database {
	
	private static Database instance;
	
	private Logger logger = LoggerFactory.getLogger(Database.class);
	
	private Mongo mongo;
	private Morphia morphia;
	private Datastore ds;
	
	private Database() {
		try {
			DatabaseConfig config = Config.get().database;
			
			logger.info("Connecting database: " + config.uri);
			
			MongoURI uri = new MongoURI(config.uri);
			mongo = new Mongo(uri);
			
			// have morphia use our logger (slf4j)
			MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
			morphia = new Morphia();
			
			initMappings();
			
			String user = config.username;
			if (user != null) {
				ds = morphia.createDatastore(
						mongo, uri.getDatabase(),
						user, config.password.toCharArray());
			} else {
				ds = morphia.createDatastore(mongo, uri.getDatabase());
			}
			
			ds.ensureCaps();
			ds.ensureIndexes();
		} catch (Exception ex) {
			logger.error("Error initializing database", ex);
		}
	}
	
	/**
	 * Initializes class mappings. This uses
	 * <a href="http://code.google.com/p/reflections">reflections</a> to scan
	 * for classes annotated with {@code @Entity} and tells morphia to map them.
	 */
	private void initMappings() {
		Reflections r = new Reflections("net.letscode.game");
		for (Class c : r.getTypesAnnotatedWith(Entity.class)) {
			logger.info("Mapping class " + c);
			morphia.map(c);
		}
	}
	
	public static Database get() {
		if (instance == null) {
			instance = new Database();
		}
		
		return instance;
	}
	
	/*
	 * Getters for various entity classes below
	 */
	
	// User
	public List<User> getUsers() {
		return ds.find(User.class).asList();
	}
	
	public User getUser(String username) {
		return ds.get(ShiroUser.class, username);
	}

	public <T> Iterable<Key<T>> save(T... entities) {
		return ds.save(entities);
	}

	public <T> Key<T> save(T entity) {
		return ds.save(entity);
	}
	
	public static void main(String[] args) {
		ShiroUser user = new ShiroUser("test", "test");
		Database.get().save(user);
	}
	
}
