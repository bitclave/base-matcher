package com.bitclave.matcher.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferSearchResultItem {
  private OfferSearch offerSearch;
  private Offer offer;
}
