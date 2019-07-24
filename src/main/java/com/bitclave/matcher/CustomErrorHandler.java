package com.bitclave.matcher;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CustomErrorHandler extends DefaultResponseErrorHandler {
  private static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

  @Override
  protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
    Charset charset = getCharset(response);
    byte[] body = getResponseBody(response);
    String statusText = response.getStatusText() != null ? response.getStatusText() : bodyAsString(body, charset);
    HttpHeaders headers = response.getHeaders();

    switch (statusCode.series()) {
      case CLIENT_ERROR:
        throw HttpClientErrorException.create(statusCode, statusText, headers, body, charset);
      case SERVER_ERROR:
        throw HttpServerErrorException.create(statusCode, statusText, headers, body, charset);
      default:
        throw new UnknownHttpStatusCodeException(statusCode.value(), statusText, headers, body, charset);
    }
  }

  protected String bodyAsString(byte[] body, Charset charset) {
    if (charset == null) {
      return new String(body, DEFAULT_CHARSET);
    }
    return new String(body, charset);
  }
}
