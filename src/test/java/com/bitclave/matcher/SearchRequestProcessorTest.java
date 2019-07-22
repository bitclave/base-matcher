package com.bitclave.matcher;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferSearchStore;
import com.bitclave.matcher.store.OfferStore;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.Collections.EMPTY_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig(classes = MatcherTestConfiguration.class)
public class SearchRequestProcessorTest {

  @Autowired
  private BaseClient baseClient;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private OfferSearchStore offerSearchStore;

  @Autowired
  private SearchRequestProcessor processor;

  @After
  public void tearDown() {
    reset(baseClient);
    reset(offerStore);
  }

  @Test
  public void processorDoNothingWhenNoNewRequests() {
    processor.process(EMPTY_LIST);
    verifyZeroInteractions(offerStore);
  }

  @Test
  public void processorDoNothingWhenPassedNull() {
    processor.process(null);
    verifyZeroInteractions(offerStore);
  }

  @Test
  public void processorSavesOfferSearchForEveryMatchedOffer() {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");
    OfferSearch offerSearch = newOfferSearch(request.getId(), offer.getId());

    doReturn(false).when(offerSearchStore).exists(any());
    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    processor.process(Arrays.asList(request));

    verify(baseClient).saveOfferSearch(Arrays.asList(offerSearch));
  }

  @Test
  public void processorSkipsOfferSearchIfAlreadyExists() {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");

    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    doReturn(true).when(offerSearchStore).exists(any());

    processor.process(Arrays.asList(request));

    verify(baseClient, never()).saveOfferSearch(any());
  }

  @Test
  public void processorHandlesUnknownFieldsInOfferSearch() throws IOException {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");

    doReturn(true).when(offerSearchStore).exists(any());
    doReturn(Arrays.asList(offer)).when(offerStore).search(any());

    processor.process(Arrays.asList(request));

    verify(baseClient, never()).saveOfferSearch(any());
  }
}
