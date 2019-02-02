package com.bitclave.matcher.store;

import com.bitclave.matcher.models.OfferSearch;

import java.util.List;

public interface OfferSearchStore {
    int insert(List<OfferSearch> offerSearches);
    boolean exists(OfferSearch offerSearch);
}
