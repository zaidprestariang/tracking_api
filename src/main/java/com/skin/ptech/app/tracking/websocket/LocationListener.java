package com.skin.ptech.app.tracking.websocket;

import com.skin.ptech.app.tracking.domain.BLELocation;
import com.skin.ptech.app.tracking.repository.BLELocationRepository;
import com.skin.ptech.app.tracking.repository.GPSLocationRepository;
import com.skin.ptech.app.tracking.rest.DetaineeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skin.ptech.app.tracking.domain.GPSLocation;

@Service
public class LocationListener {
		
	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private GPSLocationRepository gpsRepository;

	@Autowired
	private BLELocationRepository bleRepository;

	Logger logger = LoggerFactory.getLogger(LocationListener.class);

	@KafkaListener(topics = "${spring.kafka.topicid}")
    public void receive(@Payload String data,
                        @Headers MessageHeaders headers) {

        logger.info("consuming from kafka = " + data.toString());
		logger.info("headers = " + headers.toString());

		if(headers.get("kafka_receivedTopic").equals("ble_topic")){
			logger.info("ONLY FOR BLE");
		}

        //ObjectMapper mapper = new ObjectMapper();
        Gson gson = new GsonBuilder().create();
		GPSLocation gpsLoc = gson.fromJson(data, GPSLocation.class);
		this.template.convertAndSend("/topic/vehicleTracking",gpsLoc);
		gpsRepository.save(gpsLoc);

    }

	@KafkaListener(topics = "ble_topic")
	public void receiveBLE(@Payload String data,
						@Headers MessageHeaders headers) {

		logger.info("consuming from kafka = " + data.toString());
		logger.info("headers = " + headers.toString());

		if(headers.get("kafka_receivedTopic").equals("ble_topic")){
			logger.info("ONLY FOR BLE");
		}

		//ObjectMapper mapper = new ObjectMapper();
		Gson gson = new GsonBuilder().create();
		BLELocation gpsLoc = gson.fromJson(data, BLELocation.class);
		this.template.convertAndSend("/topic/detaineeTracking",gpsLoc);
		bleRepository.save(gpsLoc);

	}
}
