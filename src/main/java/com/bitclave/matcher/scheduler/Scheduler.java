package com.bitclave.matcher.scheduler;

import com.bitclave.matcher.BaseClient;
import com.bitclave.matcher.SearchRequestProcessor;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferSearchStore;
import com.bitclave.matcher.store.OfferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Scheduler {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
  private static final Integer MAX_COUNT_OF_SEARCH_REQUESTS = 10;

  @Autowired
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private SearchRequestProcessor requestProcessor;

  @Autowired
  private OfferSearchStore offerSearchStore;

  @Scheduled(fixedDelay = 60 * 1000)
  public void matchingCycle() {
    log.info("Matching cycle - Begin");

    final int offers = offerStore.insert(baseRepository.offers());
    log.info("Fetched offers " + offers);

    boolean pageThrough = true;
    int page = 0;

    while (pageThrough) {
      final Slice<SearchRequest> requests = baseRepository.searchRequests(page, MAX_COUNT_OF_SEARCH_REQUESTS);
      log.info("Found " + requests.getContent().size() + " search requests to process. page is: " + page);

      final List<Long> ids = requests.stream()
          .map(SearchRequest::getId)
          .collect(Collectors.toList());

      final List<OfferSearch> fetched = baseRepository.offerSearchesBySearchRequestId(ids);

      final int offerSearches = offerSearchStore.insert(fetched);
      log.info("Fetched offer searches  " + offerSearches);

      requestProcessor.process(requests.getContent());

      offerSearchStore.clear();

      pageThrough = requests.hasNext();
      page++;
    }

    log.info("Matching cycle - End");
  }
}