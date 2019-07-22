package com.bitclave.matcher.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SliceResponse<T> extends SliceImpl<T> {
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public SliceResponse(@JsonProperty("content") List<T> content,
                       @JsonProperty("number") int number,
                       @JsonProperty("size") int size,
                       @JsonProperty("pageable") JsonNode pageable,
                       @JsonProperty("last") boolean last,
                       @JsonProperty("hasNext") boolean hasNext,
                       @JsonProperty("first") boolean first,
                       @JsonProperty("numberOfElements") int numberOfElements) {

    super(content, PageRequest.of(number, size), hasNext);
  }

  public SliceResponse(List<T> content, Pageable pageable, boolean hasNext) {
    super(content, pageable, hasNext);
  }

  public SliceResponse(List<T> content) {
    super(content);
  }

  public SliceResponse() {
    super(new ArrayList<>());
  }
}
