package tfip.project.appbackend.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ExpenseProcessed {
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
    @JsonProperty("shares_split")
    List<ShareSplit> sharesSplit;
    String attachment;
}
