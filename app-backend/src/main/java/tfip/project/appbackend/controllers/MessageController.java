package tfip.project.appbackend.controllers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;

import tfip.project.appbackend.models.Subscription;
import tfip.project.appbackend.services.MessageService;

@RestController
@RequestMapping( path = "/api/notification")
public class MessageController {

    @Autowired
    private MessageService msgSvc;

    @PostMapping(path = "/subscribe")
    public ResponseEntity<String> subscribeToNotification(@RequestBody String payload) throws JsonMappingException, JsonProcessingException, FirebaseMessagingException{
        System.out.println(">> subscription payload: "+ payload);
        ObjectMapper objectMapper = new ObjectMapper();
        Subscription sub;
        sub = objectMapper.readValue(payload, Subscription.class);
        msgSvc.subscribeTopic(sub.getToken(), sub.getTopic());
        
        return ResponseEntity.ok().build();
    }
    // @PostMapping(path = "/unsubscribe")
    // public ResponseEntity<String> subscribeToNotification() throws FirebaseMessagingException{
    //     msgSvc.subscribeTopic("", "");
        
    //     return ResponseEntity.ok().build();
    // }

    // @PostMapping(path = "/send")
    // public ResponseEntity<String> sendNotification() throws JsonMappingException, JsonProcessingException, FirebaseMessagingException{
    //     // String topic = "abc-email.com";
    //     List<String> topics = Arrays.asList("abc-email.com", "lily-email.com");

    //     String msgId = msgSvc.sendNotificationToTopic(topics);
    //     System.out.println(">>message to topic: " + msgId);

    //     return ResponseEntity.ok().build();
    // }
    
}
