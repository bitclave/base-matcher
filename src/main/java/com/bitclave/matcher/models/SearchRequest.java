package com.bitclave.matcher.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {
  private Long id;
  private String owner;
  private Map<String, String> tags = new HashMap<>();

  public SearchRequest(Long id, String owner) {
    this.id = id;
    this.owner = owner;
  }

  public SearchRequest() {
    id = 0L;
    owner = "0x";
  }
}
