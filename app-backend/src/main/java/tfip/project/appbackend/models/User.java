package tfip.project.appbackend.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private String email;
    private String name;
    @JsonProperty("phone_number")
    @JsonInclude(Include.NON_NULL)
    private String phoneNumber;
}
