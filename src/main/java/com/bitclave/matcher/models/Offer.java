package com.bitclave.matcher.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {
  private long id;
  private String owner;
  private String description = "";
  private String title = "";
  private String imageUrl = "";
  private String worth = BigDecimal.ZERO.toString();
  private Map<String, String> tags = new HashMap<>();
  private Map<String, String> compare = new HashMap<>();
  private Map<String, CompareAction> rules = new HashMap<>();

  public Offer() {
    id = 0;
    owner = "0x0";
  }

  public Offer(long id, String owner) {
    this.id = id;
    this.owner = owner;
  }

  @AllArgsConstructor
  public enum CompareAction {
    EQUALLY("="),
    NOT_EQUAL("!="),
    LESS_OR_EQUAL("<="),
    MORE_OR_EQUAL(">="),
    MORE(">"),
    LESS("<");

    @Getter
    private String value;
  }
}
