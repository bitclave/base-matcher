package com.bitclave.matcher.scheduler;

import com.bitclave.matcher.BaseClient;
import com.bitclave.matcher.SearchRequestProcessor;
import com.bitclave.matcher.models.Account;
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
  private static final Integer MAX_COUNT_OF_USERS = 100;

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
      final Slice<Account> accounts = baseRepository.accounts(page, MAX_COUNT_OF_USERS);
      final List<String> owners = accounts.stream()
          .map(Account::getPublicKey)
          .collect(Collectors.toList());
      log.info("Fetched clients  " + owners.size());

      final List<OfferSearch> fetched = baseRepository.offerSearchesByOwners(owners);

      final int offerSearches = offerSearchStore.insert(fetched);
      log.info("Fetched offer searches  " + offerSearches);

      final List<SearchRequest> requests = baseRepository.searchRequestsByOwners(owners);
      log.info("Found " + requests.size() + " search requests to process");
      requestProcessor.process(requests);

      offerSearchStore.clear();

      pageThrough = accounts.hasNext();
      page++;
    }

    log.info("Matching cycle - End");
  }
}