package com.bitclave.matcher;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.OfferSearchResultItem;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchRequestProcessor {

  private static final Logger log = LoggerFactory.getLogger(SearchRequestProcessor.class);

  @Autowired
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  public void process(List<SearchRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return;
    }

    List<OfferSearch> offerSearches = requests.stream()
        .peek(request -> log.info("Processing request:" + request))
        .flatMap(this::match)
        .collect(toList());

    // skip offerSearches that are already saved
    List<OfferSearch> nonExistingOffers = offerSearches.stream().filter(offerSearch -> {
      List<OfferSearchResultItem>
          existing = baseRepository.findOfferSearch(offerSearch.getSearchRequestId());
      if (existing == null || existing.isEmpty()) {
        return true;
      }
      for (OfferSearchResultItem existingOne : existing) {
        if (existingOne.getOfferSearch().getSearchRequestId()
            .equals(offerSearch.getSearchRequestId())
            && existingOne.getOfferSearch().getOfferId().equals(offerSearch.getOfferId())) {
          log.info("skipping:" + offerSearch);
          return false;
        }
      }
      return true;
    }).collect(toList());

    if (!nonExistingOffers.isEmpty()) {
      baseRepository.saveOfferSearch(nonExistingOffers);
    }
  }

  private Stream<? extends OfferSearch> match(SearchRequest request) {
    List<Offer> matches = offerStore.search(request.getTags());
    return matches.stream()
        .map(offer -> newOfferSearch(request.getId(), offer.getId()));
  }
}
