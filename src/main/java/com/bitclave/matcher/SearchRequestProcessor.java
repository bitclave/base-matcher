package com.bitclave.matcher;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchRequestProcessor {

  @Autowired
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  public void process(List<SearchRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return;
    }

    List<OfferSearch> offerSearches = requests.stream()
        .flatMap(this::match)
        .collect(toList());

    baseRepository.saveOfferSearch(offerSearches);
  }

  private Stream<? extends OfferSearch> match(SearchRequest request) {
    List<Offer> matches = offerStore.search(request.getTags());
    return matches.stream()
        .map(offer -> newOfferSearch(offer.getId(), request.getId()));
  }
}
