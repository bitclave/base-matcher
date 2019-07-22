package com.bitclave.matcher;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferSearchStore;
import com.bitclave.matcher.store.OfferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.stream.Collectors.toList;

@Component
public class SearchRequestProcessor {

  private static final Logger log = LoggerFactory.getLogger(SearchRequestProcessor.class);

  @Autowired
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private OfferSearchStore offerSearchStore;

  public void process(List<SearchRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return;
    }

    List<OfferSearch> offerSearches = requests.stream()
        .flatMap(this::match)
        .collect(toList());

    log.info("Found matched requests: " + offerSearches.size());

    log.info("Filtering existing offer searches....");
    // skip offerSearches that are already saved
    List<OfferSearch> newOfferSearches = offerSearches.stream()
        .filter(notExists()).collect(toList());

    if (!newOfferSearches.isEmpty()) {
      log.info("Saving " + newOfferSearches.size() + " to base-node");
      baseRepository.saveOfferSearch(newOfferSearches);
    } else {
      log.info("No new offer searches to be saved....");
    }
  }

  private Predicate<OfferSearch> notExists() {
    return offerSearch -> !offerSearchStore.exists(offerSearch);
  }

  private Stream<? extends OfferSearch> match(SearchRequest request) {
    List<Offer> matches = offerStore.search(request.getTags());
    return matches.stream()
        .map(offer -> newOfferSearch(request.getId(), offer.getId()));
  }
}
