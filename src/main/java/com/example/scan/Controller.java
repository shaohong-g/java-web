package com.example.scan;

import com.example.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/")
public class Controller {
    @Autowired
    ApplicationContext applicationContext;

    @GetMapping("health")
    public String health(){
        return "Hello Health";
    }

    @GetMapping(value ="/beans", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> test() {
        System.out.println("Test");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("context", applicationContext.getBeanDefinitionNames());
        try {
            map.put("parent-context", applicationContext.getParent().getBeanDefinitionNames());
        } catch (Exception ex) {
            map.put("parent-context", "Fail to retrieve");
        }
        map.put("datetime", ZonedDateTime.now().toString());
        return map;
    }

    @GetMapping(value ="/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getConfig() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mailPassword", Config.mailPassword);
        map.put("mailUsername", Config.mailUsername);
        map.put("datetime", ZonedDateTime.now().toString());
        return map;
    }

}
