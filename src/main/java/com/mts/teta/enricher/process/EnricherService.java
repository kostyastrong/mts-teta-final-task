package com.mts.teta.enricher.process;

import com.mts.teta.enricher.Message;
import com.mts.teta.enricher.cache.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnricherService {
  private final UserInfoRepository userInfoRepository;

  // Обогащение очень простое: смотрит только на поле userId.
  // Можно ли сделать его поинтересней? Например, добавить несколько полей, которые можно проверять.
  public EnrichedMessage enrich(Message message) {
    Optional<String> user = userInfoRepository.findMsisdnByUserId(message.getUserId());
    String msisdn = "-";
    boolean inBase = false;
    if (user.isPresent()) {
      msisdn = user.get();
      inBase = true;
    }

    return EnrichedMessage.builder()
            .msisdn(msisdn)
            .inBase(inBase)
            .build();
  }
}
