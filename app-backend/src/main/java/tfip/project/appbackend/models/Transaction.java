package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Transaction {
    @JsonProperty("transaction_type")
    String transactionType;
    @JsonProperty("transaction_id")
    String transactionId;
    String description;
    Long date;
    @JsonProperty("who_paid")
    User whoPaid;
    @JsonProperty("recorded_by")
    User recordedBy;
    @JsonProperty("recorded_date")
    Long recordedDate;
    @JsonProperty("total_amount")
    BigDecimal totalAmount;
    @JsonProperty("who_borrowed")
    ShareSplit whoBorrowed;
    String attachment;
}
