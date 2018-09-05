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

@RestController
@RequestMapping("/vehicles_json")
public class VehicleJsonResource {

    Logger logger = LoggerFactory.getLogger(VehicleJsonResource.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addVehicle(@RequestBody RegisteredVehicle registeredVehicle) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();

        registeredVehicle.setStatus("NEW");
        Optional<RegisteredVehicle> lEntity = Optional.ofNullable(registeredVehicle);

        //RegisteredVehicle lEntity = new RegisteredVehicle(id,deviceid,description,plateNo,"NEW");

        if(registeredVehicle.getId() !=  null) {
            lEntity = this.vehicleRepository.findById(registeredVehicle.getId());
            if (lEntity.get() != null) {
                logger.info("update mode");

               lEntity.get().setDescription(registeredVehicle.getDescription());
               lEntity.get().setStatus(registeredVehicle.getStatus());
               lEntity.get().setDeviceid(registeredVehicle.getDeviceid());
               lEntity.get().setPlateNo(registeredVehicle.getPlateNo());
            }
        }

        try {
            this.vehicleRepository.save(lEntity.get());
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
