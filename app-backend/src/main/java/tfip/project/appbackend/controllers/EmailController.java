package tfip.project.appbackend.controllers;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.project.appbackend.services.EmailService;

@RestController
@RequestMapping(path = "/api/email")
public class EmailController {

    @Autowired
    private EmailService emailSvc;

    @PostMapping(path = "/remind")
    public ResponseEntity<String> sendEmailReminder(@RequestBody String payload){
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject obj = reader.readObject();

        emailSvc.sendSimpleMessage(obj.getString("toName"), obj.getString("toEmail"), obj.getString("senderName"), obj.getString("senderEmail"), obj.getJsonNumber("amount").doubleValue());
       
        return ResponseEntity.ok().build();
    }
}
