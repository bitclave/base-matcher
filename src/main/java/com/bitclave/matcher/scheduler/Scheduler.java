package com.bitclave.matcher.scheduler;

import com.bitclave.matcher.BaseClient;
import com.bitclave.matcher.SearchRequestProcessor;
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
  private BaseClient baseRepository;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private SearchRequestProcessor requestProcessor;

  @Scheduled(fixedDelay = 5000)
  public void fetchOffers() {
    offerStore.insert(baseRepository.offers());
  }

  @Scheduled(fixedDelay = 5000)
  public void fetchSearchRequests() {
    requestProcessor.process(baseRepository.searchRequests());
  }
}