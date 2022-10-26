package com.mts.teta.enricher.process;

import com.mts.teta.enricher.Message;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class EnrichedMessage {
  private final Message message;
  private final String msisdn;
}
