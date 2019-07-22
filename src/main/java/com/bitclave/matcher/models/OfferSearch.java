package com.bitclave.matcher.models;

import lombok.Value;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OfferSearch that = (OfferSearch) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
