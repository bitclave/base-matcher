package com.bitclave.matcher.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {
  private Long id;
  private String owner;
  private Map<String, String> tags = new HashMap();
}
