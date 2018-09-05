package com.skin.ptech.app.tracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.skin.ptech.app.tracking.domain.GPSLocation;

public interface GPSLocationRepository extends MongoRepository<GPSLocation, String> {

	Optional<GPSLocation> findTopByDeviceIDOrderByIdDesc(String deviceID);

	List<GPSLocation> findFirst50ByDeviceIDOrderByIdDesc(String deviceid);

	  //List<LocationEntity> findBySubjectAndLocationNear(String sid, Point p, Distance d);

}