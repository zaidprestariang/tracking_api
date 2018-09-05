package com.skin.ptech.app.tracking.rest;

import com.skin.ptech.app.tracking.domain.Detainee;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
@Controller
@RequestMapping("/status")
public class StatusResource {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String[] getStatusList() {
        return new String[]{"NEW", "APPROVED", "REJECTED"};
    }
}
