package tfip.project.appbackend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import tfip.project.appbackend.models.ActiveUser;
import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.services.UserException;
import tfip.project.appbackend.services.UserService;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    
    @Autowired
    private UserService userSvc;

    @PostMapping(path = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> signupWithEmail(@RequestBody MultiValueMap<String,String> form){
        String name = form.getFirst("name").toLowerCase();
        String email = form.getFirst("email").toLowerCase();
        String password = form.getFirst("password");
        String phoneNumber = form.getFirst("phoneNumber");

        try {
            ActiveUser user = userSvc.signupWithEmail(name, email, password, phoneNumber);
        
            ObjectMapper objectMapper = new ObjectMapper();
            String resp = objectMapper.writeValueAsString(user);
    
            return ResponseEntity.status(HttpStatus.CREATED)
                                    .body(resp);
        } catch (HttpClientErrorException ex) {
            
            return ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getResponseBodyAsString());
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(Json.createObjectBuilder().add("error", Json.createObjectBuilder().add("message", e.getMessage())).build().toString());
        } catch (JsonProcessingException e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Json.createObjectBuilder().add("error", Json.createObjectBuilder().add("message", e.getMessage())).build().toString());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> loginByEmail(@RequestBody MultiValueMap<String,String> form){

        String email = form.getFirst("email").toLowerCase();
        String password = form.getFirst("password");

        try {
            ActiveUser user = userSvc.loginWithEmail(email, password);
            ObjectMapper objectMapper = new ObjectMapper();
            String resp = objectMapper.writeValueAsString(user);
    
            return ResponseEntity.status(HttpStatus.CREATED)
                                    .body(resp);
        } catch (HttpClientErrorException ex) {
            
            return ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getResponseBodyAsString());
      
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(Json.createObjectBuilder().add("error", Json.createObjectBuilder().add("message", e.getMessage())).build().toString());
        }
    }

    @PostMapping (path = "/google-login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> loginByGoogle(@RequestBody MultiValueMap<String,String> form){
        System.out.println("google login method called");
        String email = form.getFirst("email").toLowerCase();
        // String name = form.getFirst("name").toLowerCase();
        String googleToken = form.getFirst("googleToken");
        try {
            ActiveUser user =this.userSvc.loginWithGoogle(email, googleToken);
            ObjectMapper objectMapper = new ObjectMapper();
            String resp = objectMapper.writeValueAsString(user);
    
            return ResponseEntity.status(HttpStatus.CREATED)
                                    .body(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(Json.createObjectBuilder().add("error", Json.createObjectBuilder().add("message", e.getMessage())).build().toString());
        } 
    }

    @GetMapping(path = "/{userEmail}/add-friend")
    public ResponseEntity<String> addFriendByEmail(@PathVariable String userEmail, @RequestParam(required = true) String email){

        try{
            this.userSvc.addFriend(userEmail, email);
        } catch (UserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Json.createObjectBuilder()
                                            .add("error", e.getMessage())
                                            .build()
                                            .toString());

        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping(path = "/{userEmail}/friends")
    public ResponseEntity<String> getFriendsOfUser(@PathVariable String userEmail){
        List<Friend> friends = userSvc.getFriends(userEmail);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String resp = objectMapper.writeValueAsString(friends);

            return ResponseEntity.status(HttpStatus.OK)
                                    .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                            .add("error", e.getMessage())
                                            .build()
                                            .toString());
        }
    }

    @GetMapping(path = "/request-code")
    public ResponseEntity<String> requestResetCode(@RequestParam String email){

        try {
            userSvc.requestPasswordResetCode(email);
            return ResponseEntity.ok().build();
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getResponseBodyAsString());
        }
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody MultiValueMap<String,String> form){
        String oobCode = form.getFirst("oobCode");
        String newPassword = form.getFirst("newPassword");

        try {
            String email = userSvc.resetPassword(oobCode, newPassword);
            return ResponseEntity.ok(Json.createObjectBuilder().add("email", email).build().toString());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getResponseBodyAsString());
        }
    }
}
