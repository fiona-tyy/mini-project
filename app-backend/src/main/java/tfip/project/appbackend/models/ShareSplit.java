package tfip.project.appbackend.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShareSplit {
    private String id;
    private String name;
    @JsonProperty("share_amount")
    private BigDecimal shareAmount;
}
