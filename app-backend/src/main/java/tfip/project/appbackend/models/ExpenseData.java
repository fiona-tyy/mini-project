package tfip.project.appbackend.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExpenseData {
    
    String description;
    Long date;
    @JsonProperty("who_paid")
    @JsonInclude(Include.NON_NULL)
    User whoPaid;
    @JsonProperty("line_items")
    @JsonInclude(value = Include.NON_NULL, content = Include.NON_EMPTY)
    List<LineItem> lineItems;
    @JsonProperty("service_charge")
    BigDecimal serviceCharge;
    BigDecimal gst;
    @JsonProperty("recorded_by")
    @JsonInclude(Include.NON_NULL)
    User recordedBy;
    @JsonProperty("recorded_date")
    @JsonInclude(Include.NON_NULL)
    Long recordedDate;
    String attachment;
}
