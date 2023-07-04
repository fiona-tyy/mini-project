package tfip.project.appbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import tfip.project.appbackend.services.PaymentService;


@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentSvc;
   

    @PostMapping(path = "/payment-intent", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> createPaymentIntent(@RequestBody MultiValueMap<String,String> form) throws StripeException{
        Long amount = Long.parseLong(form.getFirst("amount"));

        PaymentIntent paymentIntent = paymentSvc.createPaymentIntent(amount);
        String paymentStr = paymentIntent.toJson();

        return ResponseEntity.status(HttpStatus.OK)
                            .body(paymentStr);

    }
    
}
