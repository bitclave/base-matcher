package com.bitclave.matcher;

import com.bitclave.matcher.store.OfferStore;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

public class MatcherTestConfiguration {

  @Value("${matcher.base.url}")
  private String rootUri;

  @Bean
  @Primary
  public BaseClient baseClientService() {
    return Mockito.mock(BaseClient.class);
  }

  @Bean
  @Primary
  public OfferStore offerStore() {
    return Mockito.mock(OfferStore.class);
  }

  @Bean
  @Primary
  public SearchRequestProcessor processor() {
    return new SearchRequestProcessor();
  }

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    RestTemplateBuilder builder = new RestTemplateBuilder().rootUri(rootUri);
    return builder.build();
  }
}
