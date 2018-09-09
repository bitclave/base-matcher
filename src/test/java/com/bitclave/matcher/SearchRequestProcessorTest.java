package com.bitclave.matcher;

import static com.bitclave.matcher.models.OfferSearch.newOfferSearch;
import static java.util.Collections.EMPTY_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig(classes = MatcherTestConfiguration.class)
public class SearchRequestProcessorTest {

  @Autowired
  private BaseClient baseClientService;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private SearchRequestProcessor processor;

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
    SearchRequest request1 = new SearchRequest(1L, "owner");
    Offer offer1 = new Offer(1L, "owner");
    OfferSearch offerSearch1 = newOfferSearch(request1.getId(), offer1.getId());

    doReturn(Arrays.asList(offer1)).when(offerStore).search(any());
    processor.process(Arrays.asList(request1));

    verify(baseClientService).saveOfferSearch(Arrays.asList(offerSearch1));
  }
}
