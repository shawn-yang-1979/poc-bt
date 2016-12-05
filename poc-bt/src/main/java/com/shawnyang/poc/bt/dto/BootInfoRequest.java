package com.shawnyang.poc.bt.dto;

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
	private String id;
	private Type type;
	private String name;
	private String leafSpaceId;
	private List<Device> devices = new LinkedList<>();

	private String host;
	private String username;
	private String password;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeafSpaceId() {
		return leafSpaceId;
	}

	public void setLeafSpaceId(String leafSpaceId) {
		this.leafSpaceId = leafSpaceId;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("host=").append(host).append(", ");
		builder.append("username=").append(username).append(", ");
		builder.append("password=").append(password).append(", ");
		builder.append("leafSpaceId=").append(leafSpaceId).append(", ");
		builder.append("devices=").append(devices);
		return builder.toString();
	}

}
