package com.bitclave.matcher.store;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.bitclave.matcher.models.Offer;
import com.bitclave.matcher.models.SearchRequest;
import org.junit.Test;

public class InMemoryOfferStoreTest {

  @Test
  public void searchReturnsNoOffersWhenTagsIsEmpty() {
    Offer offer = new Offer(1L, "owner");
    offer.getTags().put("key1", "value1");
    offer.getTags().put("key2", "value2");
    offer.getTags().put("key3", "value3");

    SearchRequest request = new SearchRequest(1L, "owner");

    InMemoryOfferStore offerStore = new InMemoryOfferStore();
    offerStore.insert(Arrays.asList(offer));

    List<Offer> matchedOffers = offerStore.search(request.getTags());
    assertThat(matchedOffers).isEmpty();
  }

  @Test
  public void searchMatchesOffersByKeysAndValues() {
    Offer offer = new Offer(1L, "owner");
    offer.getTags().put("key1", "value1");
    offer.getTags().put("key2", "value2");
    offer.getTags().put("key3", "value3");

    SearchRequest request = new SearchRequest(1L, "owner");
    request.getTags().put("key1", "value1");
    request.getTags().put("key2", "value2");
    request.getTags().put("key3", "value3");

    InMemoryOfferStore offerStore = new InMemoryOfferStore();
    offerStore.insert(Arrays.asList(offer));

    List<Offer> matchedOffers = offerStore.search(request.getTags());
    assertThat(matchedOffers).hasSize(1);
  }

  @Test
  public void searchDoesNotMatchIfRequestHasKeysThatOfferDoestNotHave() {
    Offer offer = new Offer(1L, "owner");
    offer.getTags().put("key1", "value1");
    offer.getTags().put("key2", "value2");

    SearchRequest request = new SearchRequest(1L, "owner");
    request.getTags().put("key1", "value1");
    request.getTags().put("key2", "value2");
    request.getTags().put("key3", "value3");

    InMemoryOfferStore offerStore = new InMemoryOfferStore();
    offerStore.insert(Arrays.asList(offer));

    List<Offer> matchedOffers = offerStore.search(request.getTags());
    assertThat(matchedOffers).isEmpty();
  }

  @Test
  public void searchMatchesOffersThatHasExtraKeys() {
    Offer offer = new Offer(1L, "owner");
    offer.getTags().put("key1", "value1");
    offer.getTags().put("key2", "value2");
    offer.getTags().put("key3", "value3");

    SearchRequest request = new SearchRequest(1L, "owner");
    request.getTags().put("key1", "value1");
    request.getTags().put("key2", "value2");

    InMemoryOfferStore offerStore = new InMemoryOfferStore();
    offerStore.insert(Arrays.asList(offer));

    List<Offer> matchedOffers = offerStore.search(request.getTags());
    assertThat(matchedOffers).hasSize(1);
  }

  @Test
  public void searchFindsAllMatchingOffers() {
    Offer offer1 = new Offer(1L, "owner1");
    offer1.getTags().put("key1", "value1");
    offer1.getTags().put("key2", "value2");
    offer1.getTags().put("key3", "value3");

    Offer offer2 = new Offer(2L, "owner2");
    offer2.getTags().put("key1", "value1");
    offer2.getTags().put("key2", "value2");

    Offer offer3 = new Offer(3L, "owner2");
    offer3.getTags().put("key1", "value1");
    offer3.getTags().put("key4", "value4");

    SearchRequest request = new SearchRequest(1L, "owner");
    request.getTags().put("key1", "value1");
    request.getTags().put("key2", "value2");

    InMemoryOfferStore offerStore = new InMemoryOfferStore();
    offerStore.insert(Arrays.asList(offer1, offer2, offer3));

    List<Offer> matchedOffers = offerStore.search(request.getTags());
    assertThat(matchedOffers).hasSize(2);

    List<Long> offerIds = matchedOffers.stream().map(offer -> offer.getId()).collect(toList());
    assertThat(offerIds).contains(1L, 2L);
  }
}