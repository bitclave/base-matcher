package com.bitclave.matcher;

import java.util.List;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.OfferSearch;
import com.bitclave.matcher.models.SearchRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BaseClientService {
  private RestTemplate restTemplate = new RestTemplate();

  public List<Offer> offers() {
    ResponseEntity<List<Offer>> rateResponse =
        restTemplate.exchange("https://base-node-staging.herokuapp.com/v1/client/0x0/offer/",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Offer>>() {
            });
    return rateResponse.getBody();
  }

  public List<SearchRequest> searchRequests() {
    ResponseEntity<List<SearchRequest>> rateResponse =
        restTemplate
            .exchange("https://base-node-staging.herokuapp.com/v1/client/0x0/searcb/request/",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<SearchRequest>>() {
                });
    return rateResponse.getBody();
  }

  public void saveOfferSearch(List<OfferSearch> offerSearches) {

  }
}
