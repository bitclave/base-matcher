package com.bitclave.matcher.models;

import lombok.Value;

@Value
public class OfferSearch {
  private Long id = 0L;
  private String owner;
  private Long searchRequestId;
  private Long offerId;

  private OfferSearch(Long searchRequestId, Long offerId) {
    this.owner = "0x0";
    this.searchRequestId = searchRequestId;
    this.offerId = offerId;
  }

  public static OfferSearch newOfferSearch(Long searchRequestId, Long offerId) {
    return new OfferSearch(searchRequestId, offerId);
  }
}
