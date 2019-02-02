package com.bitclave.matcher;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.store.OfferSearchStore;
import com.bitclave.matcher.store.OfferStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(BaseClient.class)
@SpringJUnitConfig(classes = MatcherTestConfiguration.class)
public class BaseClientTest {

  private BaseClient client;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer server;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private OfferStore offerStore;

  @Autowired
  private OfferSearchStore offerSearchStore;

  @Before
  public void setUp() throws Exception {
    this.server = MockRestServiceServer.bindTo(restTemplate).build();
    this.client = new BaseClient(restTemplate);
  }

  @Test
  public void offersAreSavedToStore() throws Exception {
    Offer offer = new Offer(1L, "owner");
    String offerString = objectMapper.writeValueAsString(new Offer[]{offer});

    this.server.expect(requestTo("http://localhost/v1/client/0x0/offer/"))
        .andRespond(withSuccess(offerString, MediaType.APPLICATION_JSON));

    List<Offer> offers = client.offers();
    assertThat(offers).isNotNull();
    assertThat(offers).hasSize(1);
    assertThat(offers).contains(offer);
  }

  @Test
  public void searchRequestsAreProcessed() throws Exception {
    SearchRequest searchRequest = new SearchRequest(1L, "owner");
    String
        searchRequestString =
        objectMapper.writeValueAsString(new SearchRequest[]{searchRequest});

    this.server.expect(requestTo("http://localhost/v1/client/0x0/search/request/"))
        .andRespond(withSuccess(searchRequestString, MediaType.APPLICATION_JSON));

    List<SearchRequest> requests = client.searchRequests();
    assertThat(requests).isNotNull();
    assertThat(requests).hasSize(1);
    assertThat(requests).contains(searchRequest);
  }
}