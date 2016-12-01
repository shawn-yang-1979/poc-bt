package com.shawnyang.poc.bt;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/*
 * PUT /bootInfo
 * {"host":"127.0.0.1","username":"mqtt-admin","password":"mqtt-admin","leafSpaceId":"CellSpace-14","deviceIds":["Placemark-12","Placemark-11"]}
 * 
 */
public class BootInfoRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private String username;
	private String password;
	private String leafSpaceId;
	private List<String> deviceIds = new LinkedList<>();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

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

	public String getLeafSpaceId() {
		return leafSpaceId;
	}

	public void setLeafSpaceId(String leafSpaceId) {
		this.leafSpaceId = leafSpaceId;
	}

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

}
