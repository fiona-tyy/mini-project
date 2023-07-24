package tfip.project.appbackend.models;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LineItem {
    String item;
    BigDecimal amount;
    @JsonProperty("split_with")
    @JsonInclude(value = Include.NON_NULL, content = Include.NON_EMPTY)
    List<String> splitWith = new LinkedList<>();
}
