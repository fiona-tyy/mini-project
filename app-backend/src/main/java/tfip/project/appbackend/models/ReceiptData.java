package tfip.project.appbackend.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// can delete
@Getter
@Setter
@ToString
public class ReceiptData {
    String description;
    Long date;
    @JsonProperty("line_items")
    @JsonInclude(value = Include.NON_NULL, content = Include.NON_EMPTY)
    List<LineItem> lineItems;
    @JsonProperty("service_charge")
    BigDecimal serviceCharge;
    BigDecimal gst;
 
}
