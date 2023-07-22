package tfip.project.appbackend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class User {

    private String email;

    private String name;

    @JsonProperty("phone_number")
    @JsonInclude(Include.NON_NULL)
    private String phoneNumber;
}
