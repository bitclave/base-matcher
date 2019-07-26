package com.bitclave.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger log = LoggerFactory.getLogger(CustomErrorHandler.class);

  @Override
  protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
    Charset charset = getCharset(response);
    byte[] body = getResponseBody(response);
    String statusText = response.getStatusText() != null ? response.getStatusText() : bodyAsString(body, charset);
    HttpHeaders headers = response.getHeaders();

    log.warn("getStatusText: " + (response.getStatusText() == null ? "null" : response.getStatusText()));
    log.warn("body: " + bodyAsString(body, charset));

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
