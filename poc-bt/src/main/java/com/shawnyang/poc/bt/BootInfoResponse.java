package com.shawnyang.poc.bt;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/*
 * 200 OK
 * {"errorMessage":"","errorDeviceIds":["Placemark-12","Placemark-11"]}
 * 
 */
public class BootInfoResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	private List<String> errorDeviceIds = new LinkedList<>();

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getErrorDeviceIds() {
		return errorDeviceIds;
	}

	public void setErrorDeviceIds(List<String> errorDeviceIds) {
		this.errorDeviceIds = errorDeviceIds;
	}

}
