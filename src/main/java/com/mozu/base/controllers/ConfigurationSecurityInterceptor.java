package com.mozu.base.controllers;

import java.text.MessageFormat;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mozu.encryptor.PropertyEncryptionUtil;
import com.mozu.logger.LoggerContextManager;
import com.mozu.logger.MozuLogger;

public class ConfigurationSecurityInterceptor extends HandlerInterceptorAdapter {

    private static final MozuLogger logger = MozuLogger.getLogger(ConfigurationSecurityInterceptor.class);

    @Value("${SharedSecret}")
    String sharedSecret;
    
    @Value("${spice: }")
    String spiceKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        logger.debug("Security interceptor for URI: " + request.getRequestURI());
        
        String securityToken = null;
        boolean isValid = false;
        String tenantId = StringUtils.EMPTY;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
	        for (int i=0;i<cookies.length;i++) {
	            if (cookies[i].getName().equals(AdminControllerHelper.SECURITY_COOKIE)) {
	                securityToken = cookies[i].getValue();
	            }
	            if (cookies[i].getName().equals(AdminControllerHelper.TENANT_ID_COOKIE)) {
	                tenantId = cookies[i].getValue();
	            }
	        }
        }

        LoggerContextManager.setLoggingContext(tenantId);
        
        logger.debug(MessageFormat.format("Cookies retrieved from request: securityToken - {0} and tenantId - {1}", 
        		securityToken, tenantId));
        
        String encryptKey = PropertyEncryptionUtil.decryptProperty(spiceKey, sharedSecret);
        try {
            String decryptedValue = decrypt(securityToken, encryptKey, tenantId);
            DateTime dt = new DateTime(decryptedValue);
            
            // Validate date
            if (dt.isAfter(DateTime.now().minusDays(1))) {
                isValid=true;
            }
        } catch (Exception e) {
            logger.error("Decryption exception: " + e.getMessage());
        }
        
        if (!isValid) {
            String msg = "Security exception, unauthorized request"; 
            logger.warn(msg);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        LoggerContextManager.clearLoggingContext();
        return isValid;
    }
    
    public static String encrypt(String data, String sharedSecret, String tenantId) throws Exception {
    	logger.info(MessageFormat.format("Encrypting for data {0} for tenant {1} with key {2}",
    			data, tenantId, sharedSecret));

        int keyLength = Cipher.getMaxAllowedKeyLength("Blowfish");
        boolean isCryptoStrengthLimied = keyLength != Integer.MAX_VALUE; 
        
        logger.info(MessageFormat.format("Java Cryptographic strength policy is set to : {0}", isCryptoStrengthLimied ? "Limited" : "Unlimited"));
        
        int divisor = isCryptoStrengthLimied ? 8 : 134217727;
        keyLength = keyLength / divisor;
        
        String startKeyString = String.format("%s%s", sharedSecret, tenantId);
        String keyString = startKeyString.substring(startKeyString.length()-keyLength);
        
        SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        
        String encryptedString = Base64.encodeBase64String(encrypted);
        logger.info("Encrypted string: " + encryptedString);
        return encryptedString;
    }

    protected static String decrypt(String encryptedString, String sharedSecret, String tenantId) throws Exception {
    	logger.info("Encrypting for tenant " + tenantId);
    	
    	int keyLength = Cipher.getMaxAllowedKeyLength("Blowfish");
        boolean isCryptoStrengthLimied = keyLength != Integer.MAX_VALUE;
        
        logger.info(MessageFormat.format("Java Cryptographic strength policy is set to : {0}", isCryptoStrengthLimied ? "Limited" : "Unlimited"));

        int divisor = isCryptoStrengthLimied ? 8 : 134217727;
        keyLength = keyLength / divisor;
        
        String startKeyString = String.format("%s%s", sharedSecret, tenantId);
        String keyString = startKeyString.substring(startKeyString.length()-keyLength);
        
        SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encrypted = Base64.decodeBase64(encryptedString.getBytes());
        byte[] decrypted = cipher.doFinal(encrypted);
        
        String decryptedString = new String(decrypted);
        logger.info("Decryption string: " + decryptedString);
        return decryptedString;
    }

}
