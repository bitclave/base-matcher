package com.bitclave.matcher;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.OfferSearchResultItem;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.Collections.EMPTY_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig(classes = MatcherTestConfiguration.class)
public class SearchRequestProcessorTest {

  @Autowired
  private BaseClient baseClient;

  @Autowired
  private OfferStore offerStore;

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

    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    doReturn(Collections.EMPTY_LIST).when(baseClient).findOfferSearch(request.getId());
    processor.process(Arrays.asList(request));

    verify(baseClient).saveOfferSearch(Arrays.asList(offerSearch));
  }

  @Test
  public void processorSavesOfferSearchOnlyIfExistingOneIsDifferent() {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");
    OfferSearch newOfferSearch = newOfferSearch(request.getId(), offer.getId());
    OfferSearch offerSearch = newOfferSearch(request.getId(), 2L);
    OfferSearchResultItem existingOfferSearch = new OfferSearchResultItem(offerSearch, offer);

    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    doReturn(Arrays.asList(existingOfferSearch)).when(baseClient).findOfferSearch(request.getId());
    processor.process(Arrays.asList(request));

    verify(baseClient).saveOfferSearch(Arrays.asList(newOfferSearch));
  }

  @Test
  public void processorSkipsOfferSearchIfAlreadyExists() {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");
    OfferSearch offerSearch = newOfferSearch(request.getId(), offer.getId());
    OfferSearchResultItem existingOfferSearch = new OfferSearchResultItem(offerSearch, offer);

    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    doReturn(Arrays.asList(existingOfferSearch)).when(baseClient).findOfferSearch(request.getId());

    processor.process(Arrays.asList(request));

    verify(baseClient, never()).saveOfferSearch(any());
  }

  @Test
  public void processorHandlesUnknownFieldsInOfferSearch() throws IOException {
    SearchRequest request = new SearchRequest(1L, "owner");
    Offer offer = new Offer(1L, "owner");

    String offerSearchStr = "{\"offerId\":1, \"searchRequestId\": 1, \"state\": \"XXXXXX\"}";
    OfferSearch offerSearch = new ObjectMapper().readValue(offerSearchStr, OfferSearch.class);
    OfferSearchResultItem existingOfferSearch = new OfferSearchResultItem(offerSearch, offer);

    doReturn(Arrays.asList(offer)).when(offerStore).search(any());
    doReturn(Arrays.asList(existingOfferSearch)).when(baseClient).findOfferSearch(request.getId());

    processor.process(Arrays.asList(request));

    verify(baseClient, never()).saveOfferSearch(any());
  }
}
