package com.bitclave.matcher.store;

import com.bitclave.matcher.models.Offer;

import java.util.List;
import java.util.Map;

public interface OfferStore {
  int insert(List<Offer> offers);

  List<Offer> search(Map<String, String> tags);
}
