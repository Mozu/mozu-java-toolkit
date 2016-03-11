package com.mozu.base.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubnavLink {
	
	private ExtensionParent parent;
	private String[] path;
	private String href;
	private String appId;
	private String windowTitle;
	
	private String modalWindowTitle;
	private String location;
	private String badgeInitials;
	private String badgeImage;
	
	private DisplayMode displayMode = DisplayMode.modal;
	
	public ExtensionParent getParentId() {
		return parent;
	}
	public void setParentId(ExtensionParent parent) {
		this.parent = parent;
	}
	public String[] getPath() {
		return path;
	}
	public void setPath(String[] strings) {
		this.path = strings;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getWindowTitle() {
		return windowTitle;
	}
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}
    public ExtensionParent getParent() {
        return parent;
    }
    public void setParent(ExtensionParent parent) {
        this.parent = parent;
    }
    public String getModalWindowTitle() {
        return modalWindowTitle;
    }
    public void setModalWindowTitle(String modalWindowTitle) {
        this.modalWindowTitle = modalWindowTitle;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getBadgeInitials() {
        return badgeInitials;
    }
    public void setBadgeInitials(String badgeInitials) {
        this.badgeInitials = badgeInitials;
    }
    public String getBadgeImage() {
        return badgeImage;
    }
    public void setBadgeImage(String badgeImage) {
        this.badgeImage = badgeImage;
    }
    public DisplayMode getDisplayMode() {
        return displayMode;
    }
    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

}
