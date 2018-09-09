package com.bitclave.matcher.models;

import lombok.Value;

@Value
public class OfferSearch {
  public enum OfferResultAction {
    NONE,
    ACCEPT,
    REJECT;
  }

  private Long id = 0L;
  private Long searchRequestId;
  private Long offerId;
  private OfferResultAction state = OfferResultAction.NONE;

  private OfferSearch(Long searchRequestId, Long offerId) {
    this.searchRequestId = searchRequestId;
    this.offerId = offerId;
  }

  public static OfferSearch newOfferSearch(Long searchRequestId, Long offerId) {
    return new OfferSearch(searchRequestId, offerId);
  }
}
