package tfip.project.appbackend.models;

import lombok.Data;

@Data
public class PaymentInfo {
    private Long amount;
    private String currency;    
}
