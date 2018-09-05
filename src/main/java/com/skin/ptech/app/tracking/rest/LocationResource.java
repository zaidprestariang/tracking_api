package com.skin.ptech.app.tracking.rest;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.client.DistinctIterable;
import com.skin.ptech.app.tracking.domain.BLELocation;
import com.skin.ptech.app.tracking.domain.GPSLocation;
import com.skin.ptech.app.tracking.repository.BLELocationRepository;
import com.skin.ptech.app.tracking.repository.GPSLocationRepository;

@Controller
public class LocationResource {
	
	@Autowired
	private GPSLocationRepository gpsRepository;
	
	@Autowired
	private BLELocationRepository bleRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private KafkaTemplate<String, GPSLocation> kafkaTemplate;

	@Value("${spring.kafka.topicid}")
	private String topic;
	
	Logger logger = LoggerFactory.getLogger(LocationResource.class);

	
	@GetMapping("/")
    public String index() {
        return "index";
    }

	@GetMapping("/detainee")
	public String detainee() {
		return "detainee";
	}


	@RequestMapping(value = "updateGPSLocation" , method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity addCurrentGPSLocationGET(@RequestParam("deviceid") String deviceid,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) {
		logger.info("addCurrentGPSLocationGET executed");
		return addCurrentGPSLocation(deviceid,latitude,longitude);
	}

    @RequestMapping(value = "updateGPSLocation" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addCurrentGPSLocation(@RequestParam("deviceid") String deviceid,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
		GPSLocation lEntity = new GPSLocation(deviceid, "Feature", new GeoJsonPoint(latitude, longitude), data);
		
		
		try {
			//this.gpsRepository.save(lEntity);

			 Message<GPSLocation> message = MessageBuilder
		                .withPayload(lEntity)
		                .setHeader(KafkaHeaders.TOPIC, topic)
		                .build();
		        
			this.kafkaTemplate.send(message);
			logger.info("Sent sample message [" + lEntity.toString() + "] to " + topic);
			
			logger.info("addCurrentGPSLocation executed");
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch(Exception e) {
			logger.info(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
		}		
	}

	@RequestMapping(value = "updateBLELocation" , method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity addCurrentBLELocationGET(@RequestParam("deviceid") String deviceid,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) {
		logger.info("addCurrentBLELocationGET executed");
		return addCurrentBLELocation(deviceid,latitude,longitude);
	}

	@RequestMapping(value = "updateBLELocation" , method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addCurrentBLELocation(@RequestParam("deviceid") String deviceid,@RequestParam("latitude") double latitude,@RequestParam("longitude") double longitude) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
		BLELocation lEntity = new BLELocation(deviceid, "Feature", new GeoJsonPoint(latitude, longitude), data);
		
		try {
			//this.bleRepository.save(lEntity);
			//this.template.convertAndSend("/topic/detaineeTracking",lEntity);

			Message<BLELocation> message = MessageBuilder
					.withPayload(lEntity)
					.setHeader(KafkaHeaders.TOPIC, "ble_topic")
					.build();

			this.kafkaTemplate.send(message);
			logger.info("Sent sample message [" + lEntity.toString() + "] to " + "ble_topic");

			logger.info("addCurrentBLELocation executed");
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}catch(Exception e) {
			logger.info(e.getStackTrace().toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}		
	}
	
	@RequestMapping(value = "vehicleLocationHistory" , method = RequestMethod.GET)
	@ResponseBody
	public List<GPSLocation> getVehicleLocationHistory(String deviceid, @RequestParam(defaultValue="50", required=false) Integer size) throws Exception {
		logger.info("getVehicleLocationHistory executed");
		//return this.gpsRepository.findFirst50ByDeviceIDOrderByIdDesc(deviceid);
		Query query = new Query();
		query.addCriteria((Criteria.where("deviceID").is(deviceid)));
		query.with(new Sort(Sort.Direction.DESC, "id"));
		query.limit(size);
		List<GPSLocation> maxObject = mongoTemplate.find(query, GPSLocation.class);

		maxObject.forEach( gps -> {
			HashMap<String,String> oldHash = gps.getProperties();
			oldHash.put("frequencies","1");
			gps.setProperties(oldHash);
		});
		return maxObject;
	}
	
	@RequestMapping(value = "vehicleLocationHistoryLatest" , method = RequestMethod.GET)
	@ResponseBody
	public List<GPSLocation> vehicleLocationHistoryLatest() throws Exception {
		logger.info("vehicleLocationHistoryLatest executed");
		List<GPSLocation> returnVal = new ArrayList<GPSLocation>();
		
			Criteria criteria = new Criteria();
			criteria.where("deviceID");
			Query query = new Query();
			query.addCriteria(criteria);
			DistinctIterable<String> list = mongoTemplate.getCollection("GPSlocations")
			    .distinct("deviceID",query.getQueryObject(), String.class);
			for (String document : list) {
			    returnVal.add(this.gpsRepository.findTopByDeviceIDOrderByIdDesc(document).get());
			}
		return returnVal;
	}

	@RequestMapping(value = "detaineeLocationHistoryLatest" , method = RequestMethod.GET)
	@ResponseBody
	public List<BLELocation> detaineeLocationHistoryLatest() throws Exception {
		logger.info("detaineeLocationHistoryLatest executed");
		List<BLELocation> returnVal = new ArrayList<BLELocation>();

		Criteria criteria = new Criteria();
		criteria.where("deviceID");
		Query query = new Query();
		query.addCriteria(criteria);
		DistinctIterable<String> list = mongoTemplate.getCollection("BLElocations")
				.distinct("deviceID",query.getQueryObject(), String.class);
		for (String document : list) {
			returnVal.add(this.bleRepository.findTopByDeviceIDOrderByIdDesc(document).get());
		}
		return returnVal;
	}
	
	@RequestMapping(value = "detaineeLocationHistory" , method = RequestMethod.GET)
	@ResponseBody
	public List<BLELocation> getDetaineeLocationHistory(@RequestParam("deviceid") String deviceid) throws Exception {
		logger.info("getDetaineeLocationHistory executed");
		return this.bleRepository.findFirst50ByDeviceIDOrderByIdDesc(deviceid);
	}

//	// to populate data
//	@RequestMapping(value = "insertlocations" , method = RequestMethod.POST)
//	@ResponseBody
//	public final void addLocation() {
//		HashMap<String, String> data = new HashMap<String, String>();
//		data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
//		double x = -148.53011186739687;
//		double y = 72.049751694850791;
//		for (int i=0; i<5; i++) {
//			GPSLocation lEntity = new GPSLocation("TK1111", "Feature", new GeoJsonPoint(x+i+1, y+i+1), data);
//			this.gpsRepository.save(lEntity);
//		}
//		System.out.println("Added location");
//	}
//	
//
//	// to populate data
//	@RequestMapping(value = "insertBLElocations" , method = RequestMethod.POST)
//	@ResponseBody
//	public final void addBLELocation() {
//		HashMap<String, String> data = new HashMap<String, String>();
//		data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
//		double x = -148.53011186739687;
//		double y = 72.049751694850791;
//		for (int i=0; i<50; i++) {
//			BLELocation lEntity = new BLELocation("uuid1002", "Feature", new GeoJsonPoint(x+i+1, y+i+1), data);
//			this.bleRepository.save(lEntity);
//		}
//		System.out.println("Added BLE location");
//	}
	

}
