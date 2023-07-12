package tfip.project.appbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tfip.project.appbackend.services.EmailService;

@RestController
@RequestMapping(path = "/api/email")
public class EmailController {

    @Autowired
    private EmailService emailSvc;

    @PostMapping(path = "/remind")
    public ResponseEntity<String> sendEmailReminder(){

        emailSvc.sendSimpleMessage("fiona", "zyyfiona@hotmail.com", "lily", "lily@email.com", 10.0);

        return ResponseEntity.ok().build();
    }
    
}
