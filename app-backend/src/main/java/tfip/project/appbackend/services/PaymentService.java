package tfip.project.appbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import tfip.project.appbackend.models.PaymentInfo;

@Service
public class PaymentService {
    

    public PaymentService(@Value ("${stripe.key.secret}") String stripeSecretKey){
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentIntent createPaymentIntent(Long amount)throws StripeException{
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                                                        // .setTransferData(TransferData)
                                                        .addPaymentMethodType("paynow")
                                                        .setPaymentMethodData(PaymentIntentCreateParams.PaymentMethodData.builder()
                                                                .setType(PaymentIntentCreateParams.PaymentMethodData.Type.PAYNOW).build())   
                                                        .setCurrency("sgd")
                                                        .setAmount(amount)
                                                        // .setReturnUrl(null)
                                                        .build();

        
        

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return paymentIntent;

    }
    
}
