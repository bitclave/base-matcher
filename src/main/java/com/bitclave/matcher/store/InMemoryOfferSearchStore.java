package com.bitclave.matcher.store;

import com.bitclave.matcher.models.OfferSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOfferSearchStore implements OfferSearchStore {
    private static final Logger log = LoggerFactory.getLogger(InMemoryOfferSearchStore.class);

    private Map<Long, List<OfferSearch>> store = new ConcurrentHashMap<>();

    @Override
    public int insert(List<OfferSearch> offerSearches) {
        offerSearches.forEach(offerSearch -> {
            List<OfferSearch> searches = store.getOrDefault(offerSearch.getSearchRequestId(), new ArrayList<>());
            searches.add(offerSearch);
            store.put(offerSearch.getSearchRequestId(), searches);
        });
        return offerSearches.size();
    }

    @Override
    public boolean exists(OfferSearch offerSearch) {
        List<OfferSearch> existing = store.get(offerSearch.getSearchRequestId());
        if (existing == null || existing.isEmpty()) {
            return false;
        }
        for (OfferSearch existingOne : existing) {
            if (existingOne.getSearchRequestId().equals(offerSearch.getSearchRequestId())
                    && existingOne.getOfferId().equals(offerSearch.getOfferId())) {
                return true;
            }
        }
        return false;
    }
}
