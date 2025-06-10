package com.study.notificationservice.service.impl;


import com.study.comonlibrary.Service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

@Slf4j
@DubboService
public class NotifyServiceimpl implements CommonService {

    @Override
    public String senMessage(String username) {

        return "Message from Notifycation by Dubbo: Delete account with username :"+username+ "  successfully !! ";
    }
}
