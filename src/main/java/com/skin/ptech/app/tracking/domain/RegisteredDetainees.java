package com.skin.ptech.app.tracking.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "RegisteredDetainees")
public class RegisteredDetainees {

	String id;
	String uuid;
	String description;
	String status;
	
	public RegisteredDetainees(String uuid, String description, String status) {
		super();
		this.uuid = uuid;
		this.description = description;
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
