package com.davidantasdev.nomismavault.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Cache local de dados de mercado; pode ser substituído por Redis sem tocar no domínio. */
@Configuration
public class MarketDataCacheConfig {

  @Bean
  public CacheManager cacheManager(@Value("${market-data.cache.ttl:PT5M}") Duration ttl) {
    CaffeineCacheManager cacheManager =
        new CaffeineCacheManager(
            "assets",
            "assetsByTicker",
            "assetsByCategory",
            "allAssets",
            "stock-quotes",
            "stock-info");
    cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(1_000).expireAfterWrite(ttl));
    return cacheManager;
  }
}
