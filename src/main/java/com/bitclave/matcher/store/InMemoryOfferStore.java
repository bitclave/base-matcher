package com.bitclave.matcher.store;

import com.bitclave.matcher.models.Offer;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Repository
public class InMemoryOfferStore implements OfferStore {
  private List<Offer> EMPTY = Collections.emptyList();
  private Map<Long, Offer> store = new ConcurrentHashMap<>();

  @Override
  public int insert(List<Offer> offers) {
    store.clear();
    offers.forEach(offer -> store.put(offer.getId(), offer));
    return offers.size();
  }

  @Override
  public List<Offer> search(Map<String, String> searchTags) {
    if (searchTags.isEmpty()) {
      return EMPTY;
    }
    return store.values().stream().filter(offer -> {
      Map<String, String> offerTags = offer.getTags();

      if (offerTags.isEmpty()) {
        return false;
      }

      if (searchTags.keySet().size() > offerTags.keySet().size()) {
        return false;
      }

      List<String> missingKeys = searchTags.keySet()
          .stream()
          .filter(key -> !offerTags.keySet().contains(key))
          .collect(toList());

      if (!missingKeys.isEmpty()) {
        return false;
      }

      for (String key : searchTags.keySet()) {
        if (!offerTags.get(key).equals(searchTags.get(key))) {
          return false;
        }
      }
      return true;
    }).collect(toList());
  }
}
