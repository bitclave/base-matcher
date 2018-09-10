package com.bitclave.matcher;

import static com.bitclave.matcher.models.SignedRequest.newSignedRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.OfferSearchResultItem;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.models.SignedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BaseClient {
  private static final Logger log = LoggerFactory.getLogger(BaseClient.class);

  @Value("${matcher.base.url}")
  private String rootUri;

  @Value("${matcher.publicKey}")
  private String publicKey;

  @Value("${matcher.privateKey}")
  private String privateKey;

  private RestTemplate restTemplate;

  @Autowired
  public BaseClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Offer> offers() {
    ResponseEntity<List<Offer>> rateResponse =
        restTemplate.exchange("/v1/client/0x0/offer/",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Offer>>() {
            });
    return rateResponse.getBody();
  }

  public List<SearchRequest> searchRequests() {
    ResponseEntity<List<SearchRequest>> rateResponse =
        restTemplate.exchange("/v1/client/0x0/search/request/",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<SearchRequest>>() {
            });
    return rateResponse.getBody();
  }

  public void saveOfferSearch(List<OfferSearch> offerSearches) {
    if(offerSearches == null || offerSearches.isEmpty()) {
      return;
    }
    final AtomicLong nonce = new AtomicLong(getNonce());
    offerSearches.stream()
        .map(offerSearch -> newSignedRequest(offerSearches.get(0), publicKey, nonce.incrementAndGet()))
        .forEach(signedRequest -> {
          signedRequest.signMessage(privateKey);
          log.info("Saving offerSearch to BASE: {}", signedRequest.toString());
          HttpEntity<SignedRequest> request = new HttpEntity<>(signedRequest);
          OfferSearch
              saved =
              restTemplate.postForObject("/v1/search/result", request, OfferSearch.class);
          log.info("Saved offerSearch: {}", saved);
        });
  }

  public List<OfferSearchResultItem> findOfferSearch(Long searchRequestId) {
    ResponseEntity<List<OfferSearchResultItem>>
        offerSearchResponse =
        restTemplate.exchange("/v1/search/result/?searchRequestId=" + searchRequestId,
            HttpMethod.GET, null, new ParameterizedTypeReference<List<OfferSearchResultItem>>() {
            });
    return offerSearchResponse.getBody();
  }

  public long getNonce() {
    Long nonce = restTemplate.getForObject("/v1/nonce/"+publicKey, Long.class);
    return nonce.longValue();
  }
}
