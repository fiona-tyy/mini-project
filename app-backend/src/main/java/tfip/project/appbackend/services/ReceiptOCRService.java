package tfip.project.appbackend.services;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.project.appbackend.models.LineItem;
import tfip.project.appbackend.models.ReceiptData;
import tfip.project.appbackend.models.ExpenseData;

@Service
public class ReceiptOCRService {

    @Value ("${MINDEE_ACCESS_KEY}")
    private String API_KEY; 

    private String API_URL = "https://api.mindee.net/v1/products/mindee/expense_receipts/v5/predict";


    
    public ExpenseData callOCRApi(MultipartFile file) throws IOException{

        JsonObject jsonObj = Json.createObjectBuilder()
                                .add("document", Base64.getEncoder().encodeToString(file.getBytes()))
                                .build();

        RequestEntity<String> req = RequestEntity.post(API_URL)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("Authorization", "Token " + API_KEY)
                                                .body(jsonObj.toString());
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

        String payload = resp.getBody();

        System.out.println(">>> payload from ocr api:" + payload);

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject obj = reader.readObject();

        JsonObject prediction = ((obj.getJsonObject("document")).getJsonObject("inference")).getJsonObject("prediction");
        String date = (prediction.getJsonObject("date")).getString("value","");
        LocalDate localDate = LocalDate.parse(date);
        ZoneId zoneId = ZoneId.systemDefault();
        Long dateEpochSeconds = localDate.atStartOfDay(zoneId).toEpochSecond();
        // JsonArrayBuilder lineItemsArrBuilder = Json.createArrayBuilder();
        List<LineItem> lineItems = new LinkedList<>();
        JsonArray lineItemsFromReciept = prediction.getJsonArray("line_items");
        lineItemsFromReciept.stream()
                            .map(o -> (JsonObject) o)
                            .forEach( o -> {
                                BigDecimal amount = BigDecimal.ZERO;
                                if(o.get("total_amount") != JsonObject.NULL){
                                    amount = o.getJsonNumber("total_amount").bigDecimalValue();
                                }
                                LineItem lineItem = new LineItem();
                                lineItem.setItem(o.getString("description", ""));
                                lineItem.setAmount(amount);
                                lineItems.add(lineItem);
                            });
        String description = (prediction.getJsonObject("supplier_name")).getString("value","");
        BigDecimal svcCharge = BigDecimal.ZERO;
        BigDecimal gst = BigDecimal.ZERO;
        if ((prediction.getJsonObject("tip")).get("value") != JsonObject.NULL){
            svcCharge = (prediction.getJsonObject("tip")).getJsonNumber("value").bigDecimalValue();
        };
        if ((prediction.getJsonObject("total_tax")).get("value") != JsonObject.NULL){
            gst = (prediction.getJsonObject("total_tax")).getJsonNumber("value").bigDecimalValue();
        };

        ExpenseData receipt = new ExpenseData();
        receipt.setDescription(description);
        receipt.setDate(dateEpochSeconds);
        receipt.setServiceCharge(svcCharge);
        receipt.setGst(gst);
        receipt.setLineItems(lineItems);
    
        return receipt;
    }
    
    // public ReceiptData callOCRApi(MultipartFile file) throws IOException{

    //     JsonObject jsonObj = Json.createObjectBuilder()
    //                             .add("document", Base64.getEncoder().encodeToString(file.getBytes()))
    //                             .build();

    //     RequestEntity<String> req = RequestEntity.post(API_URL)
    //                                             .contentType(MediaType.APPLICATION_JSON)
    //                                             .header("Authorization", "Token " + API_KEY)
    //                                             .body(jsonObj.toString());
        
    //     RestTemplate restTemplate = new RestTemplate();
    //     ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

    //     String payload = resp.getBody();

    //     System.out.println(">>> payload from ocr api:" + payload);

    //     JsonReader reader = Json.createReader(new StringReader(payload));
    //     JsonObject obj = reader.readObject();

    //     JsonObject prediction = ((obj.getJsonObject("document")).getJsonObject("inference")).getJsonObject("prediction");
    //     String date = (prediction.getJsonObject("date")).getString("value","");
    //     LocalDate localDate = LocalDate.parse(date);
    //     ZoneId zoneId = ZoneId.systemDefault();
    //     Long dateEpochSeconds = localDate.atStartOfDay(zoneId).toEpochSecond();
    //     // JsonArrayBuilder lineItemsArrBuilder = Json.createArrayBuilder();
    //     List<LineItem> lineItems = new LinkedList<>();
    //     JsonArray lineItemsFromReciept = prediction.getJsonArray("line_items");
    //     lineItemsFromReciept.stream()
    //                         .map(o -> (JsonObject) o)
    //                         .forEach( o -> {
    //                             BigDecimal amount = BigDecimal.ZERO;
    //                             if(o.get("total_amount") != JsonObject.NULL){
    //                                 amount = o.getJsonNumber("total_amount").bigDecimalValue();
    //                             }
    //                             LineItem lineItem = new LineItem();
    //                             lineItem.setItem(o.getString("description", ""));
    //                             lineItem.setAmount(amount);
    //                             lineItems.add(lineItem);
    //                         });
    //     String description = (prediction.getJsonObject("supplier_name")).getString("value","");
    //     BigDecimal svcCharge = BigDecimal.ZERO;
    //     BigDecimal gst = BigDecimal.ZERO;
    //     if ((prediction.getJsonObject("tip")).get("value") != JsonObject.NULL){
    //         svcCharge = (prediction.getJsonObject("tip")).getJsonNumber("value").bigDecimalValue();
    //     };
    //     if ((prediction.getJsonObject("total_tax")).get("value") != JsonObject.NULL){
    //         gst = (prediction.getJsonObject("total_tax")).getJsonNumber("value").bigDecimalValue();
    //     };

    //     ReceiptData receipt = new ReceiptData();
    //     receipt.setDescription(description);
    //     receipt.setDate(dateEpochSeconds);
    //     receipt.setServiceCharge(svcCharge);
    //     receipt.setGst(gst);
    //     receipt.setLineItems(lineItems);
    
    //     return receipt;
    // }
}
