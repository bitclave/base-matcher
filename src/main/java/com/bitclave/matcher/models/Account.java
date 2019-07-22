package com.bitclave.matcher.models;

import lombok.Value;
import org.springframework.lang.NonNull;

import java.util.Date;

@Value
public class Account {
  @NonNull
  private final String publicKey;
  @NonNull
  private final Long nonce;
  @NonNull
  private final Date createdAt;
  @NonNull
  private final Date updatedAt;
}
