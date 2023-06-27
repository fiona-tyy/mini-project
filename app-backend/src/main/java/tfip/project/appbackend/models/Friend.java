package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Friend {

    private String id;
    private String name;
    private String email;
    @JsonProperty("amount_outstanding")
    private BigDecimal amountOutstanding;
    // +ve means owe friend, -ve means being owed by friend
    
}
