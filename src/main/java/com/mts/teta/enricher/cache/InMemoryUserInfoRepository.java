package com.mts.teta.enricher.cache;

import com.mts.teta.properties.UserIdProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простая реализация, которая хранит userId и msisdn в памяти.
 */
@Service
public class InMemoryUserInfoRepository implements
    UserInfoRepository {

  private final ConcurrentHashMap<String, String> data;

  public InMemoryUserInfoRepository(UserIdProperties userIdProperties) {
    this.data = new ConcurrentHashMap<>();
    this.data.putAll(userIdProperties.getUserIdToMsisdn());
  }

  @Override
  public Optional<String> findMsisdnByUserId(String userId) {
    return Optional.ofNullable(data.get(userId));
  }

  @Override
  public List<String> findAllUserIds() {
    return new ArrayList<>(data.keySet());
  }

  @Override
  public void AddUserId(String userId, String msisdn) {
    this.data.put(userId, msisdn);
  }
}
