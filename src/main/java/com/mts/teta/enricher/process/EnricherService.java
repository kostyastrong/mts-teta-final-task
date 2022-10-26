package com.mts.teta.enricher.process;

import com.mts.teta.enricher.Message;
import com.mts.teta.enricher.cache.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnricherService {
  private final UserInfoRepository userInfoRepository;

  // Обогащение очень простое: смотрит только на поле userId.
  // Можно ли сделать его поинтересней? Например, добавить несколько полей, которые можно проверять.
  public EnrichedMessage enrich(Message message) {
    final var msisdn = userInfoRepository.findMsisdnByUserId(message.getUserId()).orElse("");
    return new EnrichedMessage(message, msisdn);
  }
}
