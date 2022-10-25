package com.mts.teta.tagmanager.repository;

import com.mts.teta.tagmanager.domain.Container;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
  List<Container> findAllByAppId(Long appId);
}
