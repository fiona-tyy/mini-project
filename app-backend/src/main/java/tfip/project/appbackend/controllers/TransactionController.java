package tfip.project.appbackend.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.models.ReceiptData;
import tfip.project.appbackend.models.SettlementData;
import tfip.project.appbackend.models.ExpenseData;
import tfip.project.appbackend.models.ExpenseProcessed;
import tfip.project.appbackend.models.Transaction;
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

    @PostMapping(path = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getLineItems(@RequestPart MultipartFile file){

        // send postrequest to Mindee
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
        

        //placeholder items -to be replaced
        // JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

        // arrBuilder.add(Json.createObjectBuilder()
        //                     .add("item","pork strips")
        //                     .add("amount", 27.0));

        // arrBuilder.add(Json.createObjectBuilder()
        //                     .add("item","breaded squid")
        //                     .add("amount", 26.0));

        // arrBuilder.add(Json.createObjectBuilder()
        //                     .add("item","mango juice")
        //                     .add("amount", 5.5));

        // JsonObject result = Json.createObjectBuilder()
        //                         .add("description", "WALA")
        //                         .add("date", 1685548800*1000)
        //                         .add("service_charge", 6.20)
        //                         .add("gst", 4.50)
        //                         .add("line_items", arrBuilder)
        //                         .build();
        // return ResponseEntity.status(HttpStatus.OK)
        //                     .body(result.toString());
        // to replace above
    }

    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postNewTransaction(@RequestBody String payload){
        // System.out.println(">> payload" + payload);
        ObjectMapper objectMapper = new ObjectMapper();
        ExpenseData trans;
        try {
            trans = objectMapper.readValue(payload, ExpenseData.class);
            // System.out.println(">> mapped transaction from Json " + trans);
            ExpenseProcessed processed = transSvc.processTransaction(trans);
            String resp = objectMapper.writeValueAsString(processed);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }
    }

    @PostMapping(path = "/save-receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveReceipt(@RequestPart String transactionId, @RequestPart MultipartFile file){
        // System.out.println(">>in saving receipt method");
        try {
            String url = transSvc.uploadReceipt(transactionId, file);
            System.out.println(url);
            return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Json.createObjectBuilder().add("url", url).build().toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    }

    @PostMapping(path = "/settlement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recordSettlement(@RequestBody String payload){
        // System.out.println(">> settlement payload" + payload);
        ObjectMapper objectMapper = new ObjectMapper();
        SettlementData settlement;
        try {
            settlement = objectMapper.readValue(payload, SettlementData.class);
            // System.out.println(">> mapped settlement after posting: " + settlement);
            // return null;
            SettlementData savedSettlement = transSvc.recordSettlement(settlement);
            String resp = objectMapper.writeValueAsString(savedSettlement);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Json.createObjectBuilder()
                                    .add("error", e.getMessage())
                                    .build().toString());
        }
       
    }

    @GetMapping(path= "/outstanding/{userId}")
    public ResponseEntity<String> getOutstandingWithFriends(@PathVariable String userId){
        List<Friend> friends = transSvc.getOutstandingWithFriends(userId);
        // System.out.println(">>friends with outstanding: " + friends);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String resp = objectMapper.writeValueAsString(friends);
            // System.out.println(">>>friends: " + resp);
            return ResponseEntity.status(HttpStatus.OK)
                                    .body(resp);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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

    //for testing

    @PostMapping(path="/test")
    public ResponseEntity<String> testEndpoint(@RequestPart MultipartFile file){

        String transactionId = "xxcd123456";
        try {
            String url = transSvc.uploadReceipt(transactionId, file);
            System.out.println(url);
            return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Json.createObjectBuilder().add("url", url).build().toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString());
        }
    
    }
    
}
