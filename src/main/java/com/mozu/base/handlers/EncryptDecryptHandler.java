package com.mozu.base.handlers;


import org.springframework.stereotype.Component;

import com.mozu.api.security.AppAuthenticator;
import com.mozu.encryptor.EncryptionUtil;

@Component
/**
 * This class is used to encrypt and decrypt sensitive data in data stores used by mozu applications. 
 * @author bob_hewett
 *
 */
public class EncryptDecryptHandler {
	
    /**
     * Encrypts value.  
     * @param value the value to encrypt
     * @return an encrypted String
     */
	public String encrypt(String value)  {
		return encrypt(null, value);
	}
	
	/**
	 * Encrypts the value by using 'key' as salt value for the password.
	 * @param key salt value used to encrypt the value
	 * @param value the string to encrypt
	 * @return an encrypted string.
	 */
	public String encrypt(String key, String value)  {
        if (value==null) {
            throw new IllegalArgumentException("String to be encrypted required");
        }
		
		String sharedSecret = AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret();
		if (sharedSecret==null && key==null) {
		    throw new IllegalArgumentException("Password key required");
		}
		String newKey = sharedSecret+key;
		
		return EncryptionUtil.encrypt(newKey, value);
	}
	
	/**
	 * Decrypts the give string  
	 * @param encryptedStr an encrypted string to decrypt.
	 * @return the decrypted 
	 */
	public String decrypt( String encryptedStr)  {
	    return decrypt(null, encryptedStr);
	}
	
	/**
	 * Decrypt the encrypted string with the salt key that was used to encrypt it.  
	 * @param key the salt to add to encryption password.
	 * @param encryptedStr the string to decrypt
	 * @return a string in clear text.
	 */
	public String decrypt(String key, String encryptedStr)  {
	    if (encryptedStr==null) {
	        throw new IllegalArgumentException("String to be decrypted required");
	    }
		
		String pw = AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret()+key;
		
		return EncryptionUtil.decrypt(pw, encryptedStr);
	}
	
}
