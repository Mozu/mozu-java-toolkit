package com.mozu.base.models;

public class AppInfo {
	private String nameSpace;
    private String version;
    private String packageName;
    private String buildVersion;
    private String mozuSdkVersion;
     
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPackage() {
		return packageName;
	}
	public void setPackage(String package1) {
		packageName = package1;
	}
	public String getBuildVersion() {
		return buildVersion;
	}
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
    public String getMozuSdkVersion() {
        return mozuSdkVersion;
    }
    public void setMozuSdkVersion(String mozuSdkVersion) {
        this.mozuSdkVersion = mozuSdkVersion;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
