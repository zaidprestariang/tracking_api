package com.skin.ptech.app.tracking.domain;

import java.util.HashMap;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "GPSlocations")
public class GPSLocation {

	  private String id;
	  private String deviceID;
	  private String type;
	  private GeoJsonPoint geometry;
	  private HashMap<String, String>  properties;

	@JsonCreator  
	public GPSLocation(@JsonProperty("id") String id, @JsonProperty("deviceID") String deviceID,@JsonProperty("type") String type, 
			@JsonProperty("geometry") GeoJsonPoint geometry, @JsonProperty("properties") HashMap properties) {
		this.id = id;
		this.deviceID = deviceID;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}
	
	public GPSLocation(String deviceID, String type, GeoJsonPoint geometry, HashMap properties) {
		super();
		this.deviceID = deviceID;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}

	public GPSLocation() {
		
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
