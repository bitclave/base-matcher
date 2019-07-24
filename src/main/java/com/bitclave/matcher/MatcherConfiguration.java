package com.bitclave.matcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class MatcherConfiguration {
  @Value("${matcher.base.url}")
  private String rootUri;

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    RestTemplateBuilder builder = new RestTemplateBuilder()
        .errorHandler(new CustomErrorHandler())
        .rootUri(rootUri);
    return builder.build();
  }
}
