package com.bitclave.matcher.models;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.lang.NonNull;

import java.util.Date;

@Value
@AllArgsConstructor
public class Account {
  @NonNull
  private final String publicKey;
  @NonNull
  private final Long nonce;
  @NonNull
  private final Date createdAt;
  @NonNull
  private final Date updatedAt;

  public Account() {
    publicKey = "0";
    nonce = 0L;
    createdAt = new Date();
    updatedAt = new Date();
  }
}
