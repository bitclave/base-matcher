package com.bitclave.matcher.models;

import java.math.BigInteger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bitcoinj.core.ECKey;

@Data
public class SignedRequest<T> {

  private T data = null;
  private String pk;
  private String sig;
  private Long nonce = 0L;

  public void signMessage(String privateKey) {
    ECKey key = ECKey.fromPrivate(new BigInteger(privateKey, 16));
    Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    this.sig = key.signMessage(GSON.toJson(this.data));
  }

  public static <T> SignedRequest<T> newSignedRequest(T  data, String publicKey, long nonce) {
    SignedRequest<T> request = new SignedRequest();
    request.data = data;
    request.pk = publicKey;
    request.nonce = nonce;
    return request;
  }
}
