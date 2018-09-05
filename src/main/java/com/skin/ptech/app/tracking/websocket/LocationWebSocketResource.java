package com.skin.ptech.app.tracking.websocket;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.skin.ptech.app.tracking.domain.BLELocation;
import com.skin.ptech.app.tracking.domain.GPSLocation;
import com.skin.ptech.app.tracking.repository.BLELocationRepository;
import com.skin.ptech.app.tracking.repository.GPSLocationRepository;

@Controller
public class LocationWebSocketResource {
	
	@Autowired
	private GPSLocationRepository gpsRepository;
	
	@Autowired
	private BLELocationRepository bleRepository;
	
	@MessageMapping("/currentGPSLocation")
	@SendTo("/topic/vehicleTracking")
	public Optional<GPSLocation> getVehicleLocation(String deviceid) throws Exception {
	      return this.gpsRepository.findTopByDeviceIDOrderByIdDesc(deviceid);
	}
		
	@MessageMapping("/currentBLELocation")
	@SendTo("/topic/detaineeTracking")
	public Optional<BLELocation> getDetaineeLocation(String deviceid) throws Exception {
	      return this.bleRepository.findTopByDeviceIDOrderByIdDesc(deviceid);
	}
	
	@MessageMapping("/vehicleLocationHistory")
	@SendTo("/topic/vehicleTracking")
	public List<GPSLocation> getVehicleLocationHistory(String deviceid) throws Exception {
	      return this.gpsRepository.findFirst50ByDeviceIDOrderByIdDesc(deviceid);
	}
	
	@MessageMapping("/detaineeLocationHistory")
	@SendTo("/topic/detaineeTracking")
	public List<BLELocation> getDetaineeLocationHistory(String deviceid) throws Exception {
	      return this.bleRepository.findFirst50ByDeviceIDOrderByIdDesc(deviceid);
	}


}
