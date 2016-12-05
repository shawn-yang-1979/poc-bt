package com.shawnyang.poc.bt.dto;

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
	private boolean success;
	private String message;
	private List<String> successDeviceIds = new LinkedList<>();
	private List<String> errorDeviceIds = new LinkedList<>();
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getSuccessDeviceIds() {
		return successDeviceIds;
	}
	public void setSuccessDeviceIds(List<String> successDeviceIds) {
		this.successDeviceIds = successDeviceIds;
	}
	public List<String> getErrorDeviceIds() {
		return errorDeviceIds;
	}
	public void setErrorDeviceIds(List<String> errorDeviceIds) {
		this.errorDeviceIds = errorDeviceIds;
	}
	


}
