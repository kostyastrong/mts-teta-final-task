package com.mts.teta.tagmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mts.teta.tagmanager.domain.Trigger.TriggerAttributes;
import com.mts.teta.tagmanager.domain.Trigger.TriggerType;
import java.util.Map;
import lombok.Getter;

@Getter
public class TriggerCreateRequest {

  private final String name;
  private final TriggerType type;
  private final TriggerAttributes attributes;

  @JsonCreator
  public TriggerCreateRequest(
      String name,
      TriggerType type,
      TriggerAttributes attributes
  ) {
    this.name = name;
    this.type = type;
    this.attributes = attributes;
  }
}
