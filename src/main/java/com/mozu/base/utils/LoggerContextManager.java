package com.mozu.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import com.mozu.base.models.AppInfo;

public class LoggerContextManager {

	private static final String TENANT_ID = "TenantId";
	private static final String SITE_ID = "SiteId";
	private static final String VERSION = "package.version";
	private static final String PACKAGE_NAME = "package.name";
	private static final String BUILD_VERSION = "package.build_version";
	private static final String NAMESPACE = "package.namespace";
	private static final String SDK_VERSION = "package.mozu_sdk_version";

	public static void setApplicationLoggingContext() {
		AppInfo appInfo = ApplicationUtils.getAppInfo();
		
		MDC.put(NAMESPACE, appInfo.getNameSpace());
		MDC.put(VERSION, appInfo.getVersion());
		MDC.put(PACKAGE_NAME, appInfo.getPackage());
		MDC.put(SDK_VERSION, appInfo.getMozuSdkVersion());
	}
	
	public static void setApplicationLoggingContext(final HttpServletRequest request) {
		setApplicationLoggingContext();
		
		String buildVersion = getBuildVersion(request);
    	
		MDC.put(BUILD_VERSION, buildVersion);
	}


	public static void setTenantLoggingContext(final String tenantId, final String siteId) {
		MDC.put(TENANT_ID, tenantId);
		MDC.put(SITE_ID, siteId);
	}

	public static void setTenantLoggingContext(final String tenantId) {
		MDC.put(TENANT_ID, tenantId);
	}

	public static void clearTenantLoggingContext() {
		MDC.remove(TENANT_ID);
		MDC.remove(SITE_ID);
	}

	public static void clearApplicationLoggingContext() {
		MDC.remove(BUILD_VERSION);
		MDC.remove(NAMESPACE);
		MDC.remove(PACKAGE_NAME);
		MDC.remove(SDK_VERSION);
		MDC.remove(VERSION);
	}

	private static String getBuildVersion(HttpServletRequest request) {
		ServletContext context = request.getSession().getServletContext();
		InputStream manifestStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");

		String buildVersion = "";
		if (manifestStream != null) {
			Manifest manifest;
			try {
				manifest = new Manifest(manifestStream);
				Attributes attributes = manifest.getMainAttributes();
				buildVersion = attributes.getValue("Implementation-Version");
			} catch (IOException e) {
			}
		}

		if (buildVersion == null) {
			buildVersion = "Unknown Build";
		}
		return buildVersion;
	}
}
