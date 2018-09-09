package com.bitclave.matcher.store;

import java.util.List;
import java.util.Map;

import com.bitclave.matcher.models.Offer;

public interface OfferStore {
  public int insert(List<Offer> offers);

  public List<Offer> search(Map<String, String> tags);
}
