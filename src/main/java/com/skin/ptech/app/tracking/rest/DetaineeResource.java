package com.skin.ptech.app.tracking.rest;

import com.skin.ptech.app.tracking.domain.Detainee;
import com.skin.ptech.app.tracking.domain.RegisteredVehicle;
import com.skin.ptech.app.tracking.repository.DetaineeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/detainees")
public class DetaineeResource {

    Logger logger = LoggerFactory.getLogger(DetaineeResource.class);

    @Autowired
    private DetaineeRepository  detaineeRepository;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addDetainee(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam("name") String name,
            @RequestParam("deviceid") String deviceid,
            @RequestParam("picture") String picture,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam(value = "status", required = false) String status) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("timestamp",(new Timestamp(System.currentTimeMillis())).toString());
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();

        Detainee lEntity = new Detainee(name,deviceid,"NEW",picture, new GeoJsonPoint(latitude, longitude),timestamp);

        if(id != null) {
            if (this.detaineeRepository.findById(id) != null) {
                logger.info("update mode");
                lEntity = new Detainee(id, name, deviceid, status, picture, new GeoJsonPoint(latitude, longitude), timestamp);
            }
        }

        try {
            this.detaineeRepository.save(lEntity);
            logger.info("addDetainee executed");
            logger.info(lEntity.toString());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(Exception e) {
            logger.debug(e.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Detainee> getDetainee(
            @RequestParam(required = false, value = "status") String status)
    {
        logger.info("getDetainee executed");

        if(status != null)
        {
            logger.info("status not null");
            return this.detaineeRepository.findByStatus(status);
        }

        return this.detaineeRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Optional<Detainee> findOneDetainee(@PathVariable("id") String id) {
        logger.info("findOneDetainee executed " + id);
        return this.detaineeRepository.findById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteDetainee(@PathVariable("id") String id) {
        logger.info("deleteDetainee executed " + id);
        this.detaineeRepository.deleteById(id);
    }

}
