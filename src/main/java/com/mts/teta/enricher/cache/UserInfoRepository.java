package com.mts.teta.enricher.cache;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для получения msisdn (номера телефона) по userId
 */
public interface UserInfoRepository {

  Optional<String> findMsisdnByUserId(String userId);

  List<String> findAllUserIds();
}
