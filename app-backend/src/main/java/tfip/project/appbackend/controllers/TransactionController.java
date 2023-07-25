package tfip.project.appbackend.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;

import jakarta.json.Json;
import tfip.project.appbackend.models.ExpenseData;
import tfip.project.appbackend.models.ExpenseProcessed;
import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.models.SettlementData;
import tfip.project.appbackend.models.Transaction;
import tfip.project.appbackend.services.MessageService;
import tfip.project.appbackend.services.ReceiptOCRService;
import tfip.project.appbackend.services.TransactionException;
import tfip.project.appbackend.services.TransactionService;
import tfip.project.appbackend.services.UserException;

@RestController
@RequestMapping(path = "/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transSvc;

    @Autowired
    private ReceiptOCRService ocrSvc;

    @Autowired
    private MessageService msgSvc;

    @PostMapping(path = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getLineItems(@RequestPart MultipartFile file){

        try {
            
            ExpenseData receipt = ocrSvc.callOCRApi(file);
            ObjectMapper objectMapper = new ObjectMapper();
            String resp = objectMapper.writeValueAsString(receipt);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }
    }

    @PostMapping(path = "/expense", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postNewTransaction(@RequestPart String expense, @RequestPart(required = false) MultipartFile file){
        ObjectMapper objectMapper = new ObjectMapper();
        ExpenseData trans;
        try {
            trans = objectMapper.readValue(expense, ExpenseData.class);
            ExpenseProcessed processed = transSvc.processTransaction(trans, file);
            try {
                String msgId = msgSvc.sendNotificationToTopic(processed);
                System.out.println(">>message sent "+ msgId);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
            String resp = objectMapper.writeValueAsString(processed);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }catch (UserException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        } 
    }

    @PostMapping(path = "/settlement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> recordSettlement(@RequestPart String settlement, @RequestPart(required = false) MultipartFile file){
        System.out.println(">> settlement payload" + settlement);
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        SettlementData settlementData;
        try {
            settlementData = objectMapper.readValue(settlement, SettlementData.class);

            SettlementData savedSettlement = transSvc.recordSettlement(settlementData, file);
            try {
                String msgId = msgSvc.sendNotificationToTopic(savedSettlement);
                System.out.println(">>> Settlement msg sent: " + msgId);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
            String resp = objectMapper.writeValueAsString(savedSettlement);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                    .add("json error", e.getMessage())
                                    .build().toString());
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }  
    }

    @GetMapping(path= "/outstanding/{userId}")
    public ResponseEntity<String> getOutstandingWithFriends(@PathVariable String userId){
        List<Friend> friends = transSvc.getOutstandingWithFriends(userId);
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

    @GetMapping(path = "/records/{userId}")
    public ResponseEntity<String> getTransactionsWithFriend(@PathVariable String userId, @RequestParam(required = true) String friendId){
        List<Transaction> transactions = transSvc.getTransactionsWithFriend(userId, friendId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String resp = objectMapper.writeValueAsString(transactions);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    }

    @GetMapping(path= "/record/{transactionId}")
    public ResponseEntity<String> getTransactionById(@PathVariable String transactionId){

        List<Transaction> transactions = transSvc.getTransactionDetailById(transactionId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String resp = objectMapper.writeValueAsString(transactions);
            // System.out.println(">>>> transactions by id " + resp);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    }

    @DeleteMapping(path = "/record/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String transactionId){

        try {
            transSvc.deleteTransaction(transactionId);
            return ResponseEntity.ok().build();
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    
    }
    @GetMapping(path= "/recent/{userEmail}")
    public ResponseEntity<String> getRecentActivity(@PathVariable String userEmail){

        List<Transaction> transactions = transSvc.getRecentTransactions(userEmail);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String resp = objectMapper.writeValueAsString(transactions);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    }
}
