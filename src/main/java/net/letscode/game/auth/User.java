package net.letscode.game.auth;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * An Account class that provides some basic details for users.
 * @author timothyb89
 */
@Entity("Users")
public abstract class User {
	
	/**
	 * This user's name. This is also the main ID for the user.
	 */
	@Id
	private String username;
	
	/**
	 * The hashed password for this user.
	 */
	private String password;
	
	/**
	 * The base64-encoded password salt for this user.
	 */
	private String passwordSalt;

	private List<String> roles;
	private List<String> permissions;
	
	public User() {
		roles = new ArrayList<String>();
		permissions = new ArrayList<String>();
	}
	
	/**
	 * Creates an account with the given username and password. Password hashing
	 * is left to subclasses
	 * @param username the username of the account
	 * @param password the password to be salted and hashed
	 */
	public User(String username, String password) {
		this.username = username;
		
		hashPassword(password);
		
		roles = new ArrayList<String>();
		permissions = new ArrayList<String>();
	}

	/**
	 * An internal method for subclasses to implement 
	 * @param password 
	 */
	protected abstract void hashPassword(String password);
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "User[" 
				+ "username=" + username
				+ "]";
	}
	
}
