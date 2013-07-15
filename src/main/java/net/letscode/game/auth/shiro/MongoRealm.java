package net.letscode.game.auth.shiro;

import lombok.extern.slf4j.Slf4j;
import net.letscode.game.auth.User;
import net.letscode.game.db.Database;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * An authentication realm backed by the resident mongodb instance. The realm
 * implements the database backing for Apache Shiro authentication, and handles
 * authentication (checking of credentials) and authorization (permissions),
 * among other things.
 * @author timothyb89
 */
@Slf4j
public class MongoRealm extends AuthorizingRealm {

	public static final String NAME = "mongo";
	
	public MongoRealm() {
		HashedCredentialsMatcher m = new HashedCredentialsMatcher("SHA-256");
		m.setHashIterations(1024);
		m.setStoredCredentialsHexEncoded(false); // use base64
		setCredentialsMatcher(m);
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException {
		
		// assume a String username for now, but we may add different auth
		// types in the future, e.g. private key for saved logins, etc
		String username = (String) token.getPrincipal();

		User u = Database.get().getUser(username);
		if (u == null) {
			log.info("User not found: " + username);
			return null; // no user found
		}
		
		log.info("User found: " + u);

		// make sure we actually got a ShiroUser
		// this will always happen unless we add some sort of different user
		// type in the future
		if (u instanceof ShiroUser) {
			return (ShiroUser) u;
		} else {
			return null;
		}
	}
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		for (Object o : principals.asList()) {
			log.info("Authorz Principal class: " + o.getClass());
			
			// assume a String username for now, but we may add different auth
			// types in the future, e.g. private key for saved logins, etc
			String username = (String) o; // TODO: check this
			
			User u = Database.get().getUser(username);
			if (u == null) {
				continue; // no user found
			}
			
			// make sure we actually got a ShiroUser
			// this will always happen unless we add some sort of different user
			// type in the future
			if (u instanceof ShiroUser) {
				return (ShiroUser) u;
			} else {
				log.error("Invalid user class: " + u.getClass().getName());
			}
		}
		
		// found nothing, return null
		return null;
	}
	
}
