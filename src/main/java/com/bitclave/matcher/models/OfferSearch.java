package com.bitclave.matcher.models;

import com.bitclave.matcher.models.deserializers.OfferSearchActionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

@Value
public class OfferSearch {
  private Long id = 0L;
  private Long searchRequestId;
  private Long offerId;

  @JsonDeserialize(using = OfferSearchActionDeserializer.class)
  private OfferResultAction state = OfferResultAction.NONE;

  private OfferSearch(Long searchRequestId, Long offerId) {
    this.searchRequestId = searchRequestId;
    this.offerId = offerId;
  }

  public static OfferSearch newOfferSearch(Long searchRequestId, Long offerId) {
    return new OfferSearch(searchRequestId, offerId);
  }

  public enum OfferResultAction {
    NONE,
    ACCEPT,         // set by ???
    REJECT,         // set by User when rejects the offer
    EVALUATE,       // set by User when following external redirect link
    CONFIRMED,      // set by Offer Owner when user completed external action
    REWARDED,       // set by Offer Owner when Owner paid out the promised reward
    COMPLAIN,       // set by User when complains on the offer
    CLAIMPURCHASE;   // set by User to communicate that he mad the purchase for external offer

    public static OfferResultAction fromString(String value) {
      for (OfferResultAction state : values()) {
        if (state.name().equalsIgnoreCase(value)) {
          return state;
        }
      }
      //in case if we get an unknown status consider it as NONE (instead of introducing UNKNOWN)
      return NONE;
    }
  }
}
