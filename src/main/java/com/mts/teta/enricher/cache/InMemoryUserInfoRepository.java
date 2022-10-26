package com.mts.teta.enricher.cache;

import com.mts.teta.properties.UserIdProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Простая реализация, которая хранит userId и msisdn в памяти.
 */
@Service
public class InMemoryUserInfoRepository implements
    UserInfoRepository {

  private final Map<String, String> data;

  public InMemoryUserInfoRepository(UserIdProperties userIdProperties) {
    this.data = userIdProperties.getUserIdToMsisdn();
  }

  @Override
  public Optional<String> findMsisdnByUserId(String userId) {
    return Optional.ofNullable(data.get(userId));
  }

  @Override
  public List<String> findAllUserIds() {
    return new ArrayList<>(data.keySet());
  }
}
