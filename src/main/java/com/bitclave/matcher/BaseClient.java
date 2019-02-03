package com.bitclave.matcher;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.PagedResponse;
import com.bitclave.matcher.models.SearchRequest;
import com.bitclave.matcher.models.SignedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.bitclave.matcher.models.SignedRequest.newSignedRequest;

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
    List<Offer> allOffers = new ArrayList<>();
    boolean pageThrough = true;
    Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 20);

    while(pageThrough) {
      ResponseEntity<PagedResponse<Offer>> offerResponse =
              restTemplate.exchange("/v1/offers?page={page}&size={size}",
                      HttpMethod.GET, null,
                      new ParameterizedTypeReference<PagedResponse<Offer>>() {}, params);

      allOffers.addAll(offerResponse.getBody().getContent());
      pageThrough = offerResponse.getBody().hasNext();
      params.put("page", params.get("page") + 1);
    }
    return allOffers;
  }

  public List<OfferSearch> offerSearches() {
    List<OfferSearch> allOfferSearches = new ArrayList<>();
    boolean pageThrough = true;
    Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 20);

    while(pageThrough) {
      ResponseEntity<PagedResponse<OfferSearch>> offerSearchResponse =
              restTemplate.exchange("/v1/search/results?page={page}&size={size}",
                      HttpMethod.GET, null,
                      new ParameterizedTypeReference<PagedResponse<OfferSearch>>() {}, params);

      allOfferSearches.addAll(offerSearchResponse.getBody().getContent());
      pageThrough = offerSearchResponse.getBody().hasNext();
      params.put("page", params.get("page") + 1);
    }
    return allOfferSearches;
  }

  public List<SearchRequest> searchRequests() {
    List<SearchRequest> allRequests = new ArrayList<>();
    boolean pageThrough = true;
    Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 20);

    while(pageThrough) {
      ResponseEntity<PagedResponse<SearchRequest>> searchRequestResponse =
              restTemplate.exchange("/v1/search/requests?page={page}&size={size}",
                      HttpMethod.GET, null,
                      new ParameterizedTypeReference<PagedResponse<SearchRequest>>() {}, params);

      allRequests.addAll(searchRequestResponse.getBody().getContent());
      pageThrough = searchRequestResponse.getBody().hasNext();
      params.put("page", params.get("page") + 1);
    }
    return allRequests;
  }

  public void saveOfferSearch(List<OfferSearch> offerSearches) {
    if (offerSearches == null || offerSearches.isEmpty()) {
      return;
    }
    AtomicLong nonce = new AtomicLong(getNonce());
    for (OfferSearch offerSearch : offerSearches) {
      int retries = 3;
      do {
        ResponseEntity<OfferSearch> result = saveOfferSearch(nonce, offerSearch);
        if (result.getStatusCode() == HttpStatus.CREATED) {
          break;
        } else {
          retries--;
          nonce = new AtomicLong(getNonce()); //see if nonce needs to be fetched again
        }
      } while(retries > 0);
    }
  }

  private ResponseEntity<OfferSearch> saveOfferSearch(AtomicLong nonce, OfferSearch offerSearch) {
    SignedRequest<OfferSearch>
        signedRequest = newSignedRequest(offerSearch, publicKey, nonce.incrementAndGet());
    signedRequest.signMessage(privateKey);
    log.info("Saving offerSearch to BASE: {}", signedRequest.toString());
    HttpEntity<SignedRequest> request = new HttpEntity<>(signedRequest);
    return restTemplate.exchange("/v1/search/result", HttpMethod.POST, request, OfferSearch.class);
  }

  public long getNonce() {
    Long nonce = restTemplate.getForObject("/v1/nonce/" + publicKey, Long.class);
    return nonce.longValue();
  }
}
