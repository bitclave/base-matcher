package com.bitclave.matcher.models.deserializers;

import com.bitclave.matcher.models.OfferSearch;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class OfferSearchActionDeserializer extends JsonDeserializer<OfferSearch.OfferResultAction> {

    @Override
    public OfferSearch.OfferResultAction deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        return OfferSearch.OfferResultAction.fromString(parser.getValueAsString());
    }
}

