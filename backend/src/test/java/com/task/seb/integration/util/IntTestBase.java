package com.task.seb.integration.util;

import com.task.seb.SebTaskApplication;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static java.util.Objects.requireNonNull;

@SpringBootTest(classes = SebTaskApplication.class)
@ActiveProfiles({"integration"})
public abstract class IntTestBase {
  @Autowired
  private EraseDbHelper eraseDbHelper;
  @Autowired
  CacheManager cacheManager;

  @AfterEach
  void afterEach() {
    eraseDbHelper.cleanupDatabase();
    cacheManager.getCacheNames().forEach(cacheName -> requireNonNull(cacheManager.getCache(cacheName)).clear());
  }
}