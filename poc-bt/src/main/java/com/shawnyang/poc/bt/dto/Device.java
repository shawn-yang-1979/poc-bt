package com.shawnyang.poc.bt.dto;

import java.io.Serializable;

public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private Type type;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id=").append(id).append(", ");
		builder.append("type=").append(type);
		return builder.toString();
	}
}
