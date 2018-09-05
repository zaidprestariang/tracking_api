package com.skin.ptech.app.tracking.rest;

import com.skin.ptech.app.tracking.domain.RegisteredVehicle;
import com.skin.ptech.app.tracking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleResource {

    Logger logger = LoggerFactory.getLogger(VehicleResource.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addVehicle(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam("deviceid") String deviceid,
            @RequestParam("description") String description,
            @RequestParam("plateNo") String plateNo,
            @RequestParam(value = "status", required = false) String status) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();


        RegisteredVehicle lEntity = new RegisteredVehicle(id,deviceid,description,plateNo,"NEW");

        if(id != null) {
            if (this.vehicleRepository.findById(id) != null) {
                logger.info("update mode");
                lEntity = new RegisteredVehicle(id,deviceid,description,plateNo,status);
            }
        }

        try {
            this.vehicleRepository.save(lEntity);
            logger.info("addVehicle executed");
            logger.info(lEntity.toString());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(Exception e) {
            logger.debug(e.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<RegisteredVehicle> getVehicle(
            @RequestParam(required = false, value = "status") String status
    ) {
        logger.info("getVehicle executed");

        if(status != null)
        {
            logger.info("status not null");
            return this.vehicleRepository.findByStatus(status);
        }
        return this.vehicleRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Optional<RegisteredVehicle> findOneVehicle(@PathVariable("id") String id) {
        logger.info("findOneVehicle executed " + id);
        return this.vehicleRepository.findById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteVehicle(@PathVariable("id") String id) {
        logger.info("deleteVehicle executed " + id);
        this.vehicleRepository.deleteById(id);
    }

}
