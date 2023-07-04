package tfip.project.appbackend.services;

import java.io.StringReader;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.project.appbackend.models.ActiveUser;
import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.repositories.UserRepository;

@Service
public class UserService {

    private String SIGNUP_AUTH_URL="https://identitytoolkit.googleapis.com/v1/accounts:signUp";

    private String LOGIN_AUTH_URL="https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";
    private String GOOGLE_LOGIN_URL="https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp";

    @Value ("${FIREBASE_API_KEY}")
    private String API_KEY; 

    @Autowired
    private UserRepository userRepo;

    public ActiveUser signupWithEmail(String name, String email, String password, String phoneNumber) throws HttpClientErrorException, UserException{

        String url = UriComponentsBuilder
                        .fromUriString(SIGNUP_AUTH_URL)
                        .queryParam("key", API_KEY)
                        .toUriString();

        JsonObject jsonObj = Json.createObjectBuilder()
                                .add("displayName",name)
                                .add("email", email)
                                .add("password", password)
                                .add("returnSecureToken", true)
                                .build();


        RequestEntity<String> req = RequestEntity.post(url)
                                                .body(jsonObj.toString());
        
        RestTemplate restTemplate = new RestTemplate();
        
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        String payload = resp.getBody();
        System.out.println(">>> payload from firebase signup:" + payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject obj = reader.readObject();

        
        // String uid = obj.getString("localId");

        userRepo.addNewUser( email, name);
        
        ActiveUser user = new ActiveUser();
        user.setName(name);
        user.setEmail(email);
        user.setToken(obj.getString("idToken"));

        Long expirationDate = Instant.now().toEpochMilli() + (Long.parseLong( obj.getString("expiresIn"))*1000);
        user.setTokenExpirationDate(expirationDate);
    
        return user;

        //must catch exception

    }

    public ActiveUser loginWithEmail(String email, String password){
        
        String url = UriComponentsBuilder
                        .fromUriString(LOGIN_AUTH_URL)
                        .queryParam("key", API_KEY)
                        .toUriString();

        JsonObject jsonObj = Json.createObjectBuilder()
                                .add("email", email)
                                .add("password", password)
                                .add("returnSecureToken", true)
                                .build();


        RequestEntity<String> req = RequestEntity.post(url)
                                                .body(jsonObj.toString());
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        if(resp.getStatusCode() != HttpStatusCode.valueOf(200)){
            // to write exception handling
        }

        String payload = resp.getBody();
        // System.out.println(">>> payload from firebase login:" + payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject obj = reader.readObject();

        // String uid = obj.getString("localId");
        Long expirationDate = Instant.now().toEpochMilli() + (Long.parseLong( obj.getString("expiresIn"))*1000);

        ActiveUser user = new ActiveUser();
        user.setName(obj.getString("displayName"));
        user.setEmail(email);
        user.setToken(obj.getString("idToken"));
        user.setTokenExpirationDate(expirationDate);

        return user;
    }

    public ActiveUser loginWithGoogle(String email, String googleToken) throws UserException{

        String url = UriComponentsBuilder
                        .fromUriString(GOOGLE_LOGIN_URL)
                        .queryParam("key", API_KEY)
                        .toUriString();

        JsonObject jsonObj = Json.createObjectBuilder()
                                .add("requestUri", "http://localhost")
                                .add("postBody", "id_token=" + googleToken + "&providerId=google.com")
                                .add("returnIdpCredential", true)
                                .add("returnSecureToken", true)
                                .build();


        RequestEntity<String> req = RequestEntity.post(url)
                                                .body(jsonObj.toString());
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        if(resp.getStatusCode() != HttpStatusCode.valueOf(200)){
            // to write exception handling
        }

        String payload = resp.getBody();
        // System.out.println(">>> payload from firebase login:" + payload);
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject obj = reader.readObject();

        Long expirationDate = Instant.now().toEpochMilli() + (Long.parseLong( obj.getString("expiresIn"))*1000);

        ActiveUser user = new ActiveUser();
        user.setName(obj.getString("displayName"));
        user.setEmail(email);
        user.setToken(obj.getString("idToken"));
        user.setTokenExpirationDate(expirationDate);
        user.setGoogleToken(googleToken);

        //check if email in users database
        try {
            userRepo.getUserByEmail(email);
        } catch (UserException e) {
            userRepo.addNewUser(email, user.getName());
        }
        return user;
    }

    public void addFriend(String userEmail, String friendEmail) throws UserException{

        userRepo.addFriend(userEmail, friendEmail);
    }

    public List<Friend> getFriends(String userEmail){
        return this.userRepo.getFriends(userEmail);
    }

}
