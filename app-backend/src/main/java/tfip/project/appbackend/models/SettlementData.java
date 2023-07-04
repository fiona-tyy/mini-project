package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
    @JsonProperty("who_paid")
    User whoPaid;
    @JsonProperty("recorded_by")
    User recordedBy;
    @JsonProperty("recorded_date")
    Long recordedDate;
    @JsonProperty("repayment_amount")
    BigDecimal repaymentAmount;
    @JsonProperty("who_received")
    User whoReceived;
    String attachment;
}
