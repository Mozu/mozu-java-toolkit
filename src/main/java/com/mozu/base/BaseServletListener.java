package com.mozu.base;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.stereotype.Component;

import com.mozu.api.security.AppAuthenticator;
import com.mozu.logger.MozuLogger;

@Component
public class BaseServletListener implements ServletContextListener{
	private static final MozuLogger logger = MozuLogger.getLogger(BaseServletListener.class);
	
	public void contextDestroyed(ServletContextEvent sce) {
		AppAuthenticator.invalidateAuth();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                logger.debug("Deregister driver: " + driver.getClass().getName());
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.warn("Error deregistering driver: " + e.getMessage());
            }
        }
	}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Do Nothing
    }
}
