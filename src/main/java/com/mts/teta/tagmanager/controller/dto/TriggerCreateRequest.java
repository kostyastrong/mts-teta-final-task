package com.mts.teta.tagmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mts.teta.tagmanager.domain.Trigger.TriggerAttributes;
import com.mts.teta.tagmanager.domain.Trigger.TriggerType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TriggerCreateRequest {

  @NotNull
  private final String name;
  @NotNull
  private final TriggerType type;
  @NotNull
  @Valid
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
