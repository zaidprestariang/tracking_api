package com.skin.ptech.app.tracking.repository;

import com.skin.ptech.app.tracking.domain.Detainee;
import com.skin.ptech.app.tracking.domain.RegisteredVehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends MongoRepository<RegisteredVehicle, String> {

    List<RegisteredVehicle> findByStatus(String status);

    Optional<RegisteredVehicle> findById(String id);
}
