package com.mts.teta.enricher.db;

import com.clickhouse.jdbc.ClickHouseDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class ClickhouseWrapper {

  private final ClickHouseDataSource dataSource;
}
