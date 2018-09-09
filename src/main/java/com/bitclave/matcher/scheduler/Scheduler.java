package com.bitclave.matcher.scheduler;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import com.bitclave.matcher.BaseClientService;
import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

  @Autowired
  private BaseClientService baseRepository;

  @Autowired
  private OfferStore offerStore;

  @Scheduled(fixedDelay = 5000)
  public void fetchOffers() {
    offerStore.insert(baseRepository.offers());
  }

  @Scheduled(fixedDelay = 5000)
  public void fetchSearchRequests() {
    List<SearchRequest> requests = baseRepository.searchRequests();

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