package com.simulationlab.QA_2.controller;

import com.simulationlab.QA_2.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingController {
    @Autowired
    QAService qaService;

    @RequestMapping(path={"/setting"}, method = {RequestMethod.GET})
    @ResponseBody
    public String setting() {
        return "Setting OK\n <br>" + qaService.getMsg(1);
    }

}
