package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class SettlementData {
    @JsonProperty("transaction_type")
    @JsonInclude(Include.NON_NULL)
    String transactionType;

    @JsonProperty("transaction_id")
    @JsonInclude(Include.NON_NULL)
    String transactionId;

    @JsonInclude(Include.NON_NULL)
    String description;

    Long date;

    @JsonProperty("recorded_by")
    User recordedBy;
    
    @JsonProperty("recorded_date")
    Long recordedDate;
   
    @JsonProperty("repayment_amount")
    BigDecimal repaymentAmount;

    @JsonProperty("who_paid")
    User whoPaid;
    
    @JsonProperty("who_received")
    User whoReceived;

    String attachment;
}
