package com.bitclave.matcher.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Value;

@Value
public class SearchRequest {
  private Long id;
  private String owner;
  private Map<String, String> tags = new HashMap();
}
