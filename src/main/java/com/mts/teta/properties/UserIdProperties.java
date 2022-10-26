package com.mts.teta.properties;

import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("user-id")
@ConstructorBinding
@Getter
public class UserIdProperties {
  @NotNull
  private final Map<String, String> userIdToMsisdn;

  public UserIdProperties(Map<String, String> userIdToMsisdn) {
    this.userIdToMsisdn = userIdToMsisdn;
  }
}
