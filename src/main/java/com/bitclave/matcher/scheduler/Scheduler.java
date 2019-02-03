package com.bitclave.matcher.scheduler;

import com.bitclave.matcher.BaseClient;
import com.bitclave.matcher.SearchRequestProcessor;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferSearchStore;
import com.bitclave.matcher.store.OfferStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scheduler {

  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

  @Autowired
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private SearchRequestProcessor requestProcessor;

  @Autowired
  private OfferSearchStore offerSearchStore;

  @Scheduled(fixedDelay = 5000)
  public void matchingCycle() {
    log.info("Matching cycle - Begin");

    int offers = offerStore.insert(baseRepository.offers());
    log.info("Fetched offers " + offers);

    int offerSearches = offerSearchStore.insert(baseRepository.offerSearches());
    log.info("Fetched offer searches  " + offerSearches);

    List<SearchRequest> requests = baseRepository.searchRequests();
    log.info("Found " + requests.size() + " to process");
    requestProcessor.process(requests);

    log.info("Matching cycle - End");
  }
}