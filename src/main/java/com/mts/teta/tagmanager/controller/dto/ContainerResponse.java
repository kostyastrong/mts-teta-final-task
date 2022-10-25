package com.mts.teta.tagmanager.controller.dto;

import com.mts.teta.tagmanager.domain.Container;
import com.mts.teta.tagmanager.domain.Trigger;
import com.mts.teta.tagmanager.domain.Trigger.TriggerAttributes;
import com.mts.teta.tagmanager.domain.Trigger.TriggerType;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class ContainerResponse {

  private final long id;
  private final String name;
  private final List<TriggerResponse> triggers;

  public ContainerResponse(Container container) {
    this.id = container.getId();
    this.name = container.getName();
    this.triggers = container.getTriggers()
        .stream()
        .map(TriggerResponse::new)
        .toList();
  }

  @Getter
  public static class TriggerResponse {

    private final long id;
    private final String name;
    private final TriggerType type;
    private final TriggerAttributes attributes;

    public TriggerResponse(Trigger trigger) {
      this.id = trigger.getId();
      this.name = trigger.getName();
      this.type = trigger.getType();
      this.attributes = trigger.getAttributes();
    }
  }
}
