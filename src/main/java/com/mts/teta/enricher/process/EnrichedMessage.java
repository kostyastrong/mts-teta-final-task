package com.mts.teta.enricher.process;

import com.mts.teta.enricher.Message;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EnrichedMessage {
  private  Message message;
  private  String msisdn;
}
