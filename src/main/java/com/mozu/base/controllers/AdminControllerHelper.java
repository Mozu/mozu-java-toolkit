package com.mozu.base.controllers;

import java.net.URLDecoder;
import java.text.MessageFormat;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.api.security.Crypto;
import com.mozu.encryptor.PropertyEncryptionUtil;

public class AdminControllerHelper {
    private static final Logger logger = LoggerFactory.getLogger(AdminControllerHelper.class);
    private String sharedSecret;
    private String spiceKey;

    public static final String SECURITY_COOKIE = "MozuToken";
    public static final String TENANT_ID_COOKIE = "MozuTenant";
    
    private String body = null;
    public AdminControllerHelper() {
        this.sharedSecret = AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret();
        this.spiceKey = null;
    }

    public AdminControllerHelper(String saltKey, String sharedSecret) {
        this.sharedSecret = sharedSecret;
        this.spiceKey = saltKey;
    }

    
    public boolean securityCheck(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        boolean isAuthorized = false;
        try {
            body = IOUtils.toString(httpRequest.getInputStream());
            String msgHash = httpRequest.getParameter("messageHash");
            String dateKey = httpRequest.getParameter("dt");
            String tenantId = httpRequest.getParameter("tenantId");
            
            logger.info("Http request parameters: tenantId={}, date={}, messageHash={}", 
            		tenantId, dateKey, msgHash);
            
            ApiContext apiContext = new MozuApiContext(new Integer(tenantId));
            apiContext.setHeaderDate(dateKey);
            apiContext.setHmacSha256(msgHash);
            
            String decodedBody = URLDecoder.decode(body, "ISO-8859-1");
        
            // validate request by making sure it can be decrypted and the tenant id in the URLs in the boxy matches contains the tenantId in the calling URL.
            if (Crypto.isRequestValid(apiContext, decodedBody) && decodedBody.contains("t"+tenantId+".")) {
            	logger.info("The request is valid");
                isAuthorized = true;
                String encryptKey = PropertyEncryptionUtil.decryptProperty(spiceKey, sharedSecret);
                
                httpResponse.addCookie(new Cookie(SECURITY_COOKIE, 
                        ConfigurationSecurityInterceptor.encrypt(DateTime.now().toString(), 
                        		encryptKey, tenantId)));
                httpResponse.addCookie(new Cookie(TENANT_ID_COOKIE, tenantId));
            }
        } catch (Exception e) {
            logger.warn("Validation exception: " + e.getMessage());
            isAuthorized = false;
        }
        
        logger.info("Is authorized: {}", isAuthorized);
        return isAuthorized;
    }

    public String getBody() {
        return body;
    }
}
