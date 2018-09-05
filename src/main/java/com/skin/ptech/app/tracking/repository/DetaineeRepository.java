package com.skin.ptech.app.tracking.repository;

import com.skin.ptech.app.tracking.domain.Detainee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DetaineeRepository extends MongoRepository<Detainee, String> {

    List<Detainee> findByStatus(String status);
}
