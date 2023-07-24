package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Friend {

    private String email;
    private String name;
    @JsonProperty("amount_outstanding")
    private BigDecimal amountOutstanding;
    
}
