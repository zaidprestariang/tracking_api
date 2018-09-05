package com.skin.ptech.app.tracking.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "RegisteredVehicles")
public class RegisteredVehicle {

	@Id
	String id;
	String deviceid;
	String description;
	String plateNo;
	String status;

	public RegisteredVehicle() {
	}

	public RegisteredVehicle(String id, String deviceid, String description, String plateNo, String status) {
		this.id = id;
		this.deviceid = deviceid;
		this.description = description;
		this.plateNo = plateNo;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
