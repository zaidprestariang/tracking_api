package com.skin.ptech.app.tracking.domain;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BLElocations")
public class BLELocation {

	  private String id;
	  private String deviceID;
	  private String type;
	  private GeoJsonPoint geometry;
	  private HashMap<String, String>  properties;

	  
	public BLELocation(
			@JsonProperty("deviceID") String deviceID,
			@JsonProperty("type") String type,
			@JsonProperty("geometry") GeoJsonPoint geometry,
			@JsonProperty("properties") HashMap properties) {
		super();
		this.deviceID = deviceID;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}

	public BLELocation() {
		
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public GeoJsonPoint getGeometry() {
		return geometry;
	}

	public void setGeometry(GeoJsonPoint geometry) {
		this.geometry = geometry;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}


}
