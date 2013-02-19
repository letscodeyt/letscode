package net.letscode.game.auth.shiro;

import com.google.code.morphia.annotations.Entity;
import java.util.ArrayList;
import java.util.Collection;
import net.letscode.game.auth.User;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

/**
 * A User implementation that is compatible with Apache Shiro authentication.
 * @author timothyb89
 */
@Entity("Users")
public class ShiroUser extends User implements
		SaltedAuthenticationInfo, AuthorizationInfo {

	public ShiroUser() {
	}

	public ShiroUser(String username, String password) {
		super(username, password);
	}
	
	@Override
	protected void hashPassword(String password) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		ByteSource saltBytes = rng.nextBytes();
		
		setPassword(new Sha256Hash(password, saltBytes, 1024).toBase64());
		setPasswordSalt(saltBytes.toBase64());
	}
	
	/**
	 * Gets the principals for this user. In this implementation the user
	 * principals include only the username.
	 * @return a {@link PrincipalCollection} for this user
	 */
	@Override
	public PrincipalCollection getPrincipals() {
		return new SimplePrincipalCollection(getUsername(), MongoRealm.NAME);
	}
	
	/**
	 * Returns the credentials for this user. In this implementation, the
	 * credentials include only the hashed password.
	 * @return the hashed user password, as a string.
	 */
	@Override
	public Object getCredentials() {
		return ByteSource.Util.bytes(Base64.decode(getPassword()));
	}
	
	@Override
	public ByteSource getCredentialsSalt() {
		return ByteSource.Util.bytes(Base64.decode(getPasswordSalt()));
	}

	// Collection<String> getRoles() is implemented by the superclass
	
	@Override
	public Collection<String> getStringPermissions() {
		return getPermissions();
	}

	/**
	 * Gets the object permissions for this user. In this implementation, an
	 * empty list is returned as all of our permissions are stored as
	 * shiro-compatible Strings which are converted by the realm.
	 * @return an (empty) list of object permissions
	 */
	@Override
	public Collection<Permission> getObjectPermissions() {
		return new ArrayList();
	}
	
}
