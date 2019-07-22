package com.bitclave.matcher;

import com.bitclave.matcher.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
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
  private static final int MAX_REPEAT_COUNT = 5;

  @Value("${matcher.base.url}")
  private String rootUri;

  @Value("${matcher.publicKey}")
  private String publicKey;

  @Value("${matcher.privateKey}")
  private String privateKey;

  private RestTemplate restTemplate;

  @Autowired
  public BaseClient(@NonNull final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Offer> offers() {
    int repeat = 0;
    final List<Offer> allOffers = new ArrayList<>();
    boolean pageThrough = true;
    final Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 255);

    while (pageThrough) {
      try {
        ResponseEntity<SliceResponse<Offer>> offerResponse =
            restTemplate.exchange("/v1/consumers/offers?page={page}&size={size}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<SliceResponse<Offer>>() {
                }, params);

        allOffers.addAll(offerResponse.getBody()
            .getContent());
        pageThrough = offerResponse.getBody()
            .hasNext();
        params.put("page", params.get("page") + 1);
        repeat = 0;
      } catch (Throwable e) {
        repeat++;
        log.warn("offers", e);
        if (repeat > MAX_REPEAT_COUNT) {
          throw e;
        }
      }
    }
    return allOffers;
  }

  public Slice<Account> accounts(@NonNull final Integer page, @NonNull final Integer size) {
    int repeat = 0;
    final Map<String, Integer> params = new HashMap<>();
    params.put("page", page);
    params.put("size", size);

    while (true) {
      try {
        ResponseEntity<SliceResponse<Account>> accountResponse =
            restTemplate.exchange("/v1/consumers/accounts?page={page}&size={size}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<SliceResponse<Account>>() {
                }, params);

        return accountResponse.getBody();

      } catch (Throwable e) {
        repeat++;
        log.warn("accounts", e);
        if (repeat > MAX_REPEAT_COUNT) {
          throw e;
        }
      }
    }
  }

  public List<OfferSearch> offerSearchesByOwners(@NonNull final List<String> owners) {
    int repeat = 0;
    final List<OfferSearch> allOfferSearches = new ArrayList<>();
    boolean pageThrough = true;
    final Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 255);

    while (pageThrough) {
      try {
        ResponseEntity<SliceResponse<OfferSearch>> offerSearchResponse =
            restTemplate.exchange("/v1/consumers/search/results?page={page}&size={size}",
                HttpMethod.POST, new HttpEntity<>(owners),
                new ParameterizedTypeReference<SliceResponse<OfferSearch>>() {
                }, params);

        allOfferSearches.addAll(offerSearchResponse.getBody()
            .getContent());
        pageThrough = offerSearchResponse.getBody()
            .hasNext();

        params.put("page", params.get("page") + 1);
        repeat = 0;

      } catch (Throwable e) {
        repeat++;
        log.warn("offerSearches", e);
        if (repeat > MAX_REPEAT_COUNT) {
          throw e;
        }
      }
    }
    return allOfferSearches;
  }

  public List<SearchRequest> searchRequestsByOwners(@NonNull final List<String> owners) {
    int repeat = 0;
    final List<SearchRequest> allRequests = new ArrayList<>();
    boolean pageThrough = true;
    final Map<String, Integer> params = new HashMap<>();
    params.put("page", 0);
    params.put("size", 255);

    while (pageThrough) {
      try {
        ResponseEntity<SliceResponse<SearchRequest>> searchRequestResponse =
            restTemplate.exchange("/v1/consumers/search/requests?page={page}&size={size}",
                HttpMethod.POST, new HttpEntity<>(owners),
                new ParameterizedTypeReference<SliceResponse<SearchRequest>>() {
                }, params);

        allRequests.addAll(searchRequestResponse.getBody()
            .getContent());
        pageThrough = searchRequestResponse.getBody()
            .hasNext();
        params.put("page", params.get("page") + 1);
        repeat = 0;

      } catch (Throwable e) {
        repeat++;
        log.warn("searchRequests", e);
        if (repeat > MAX_REPEAT_COUNT) {
          throw e;
        }
      }
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
      } while (retries > 0);
    }
  }

  private ResponseEntity<OfferSearch> saveOfferSearch(AtomicLong nonce, OfferSearch offerSearch) {
    final SignedRequest<OfferSearch> signedRequest = newSignedRequest(offerSearch, publicKey,
        nonce.incrementAndGet());
    signedRequest.signMessage(privateKey);

    log.info("Saving offerSearch to BASE: {}", signedRequest.toString());
    final HttpEntity<SignedRequest> request = new HttpEntity<>(signedRequest);

    try {
      return restTemplate.exchange("/v1/search/result", HttpMethod.POST, request, OfferSearch.class);

    } catch (HttpServerErrorException e) {
      log.warn("saveOfferSearch", e);

      return ResponseEntity
          .badRequest()
          .build();
    }
  }

  public long getNonce() {
    try {
      final Long nonce = restTemplate.getForObject("/v1/nonce/" + publicKey, long.class);

      return nonce != null ? nonce : -1;

    } catch (Throwable e) {
      log.error("getNonce", e);
    }

    return -1;
  }
}
