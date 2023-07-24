package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ShareSplit {
    private String email;
    private String name;
    @JsonProperty("share_amount")
    private BigDecimal shareAmount;
}
