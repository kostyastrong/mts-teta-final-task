package com.mts.teta.enricher.process;

import com.mts.teta.enricher.Message;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class EnrichedMessage {
  private final Message message;
  private final Boolean inBase;
  private final String msisdn;
}
