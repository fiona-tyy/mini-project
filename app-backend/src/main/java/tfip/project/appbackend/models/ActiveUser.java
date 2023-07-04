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
public class ActiveUser {
    private String name;
    @JsonInclude(Include.NON_NULL)
    private String email;
    @JsonInclude(Include.NON_NULL)
    private String token;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("token_expiration_date")
    private Long tokenExpirationDate;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("google_token")
    private String googleToken;
    
}
