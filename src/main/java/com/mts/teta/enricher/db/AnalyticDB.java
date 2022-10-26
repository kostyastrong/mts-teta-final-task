package com.mts.teta.enricher.db;

import com.mts.teta.enricher.process.EnrichedMessage;

public interface AnalyticDB {

  void persistMessage(EnrichedMessage message);
}
