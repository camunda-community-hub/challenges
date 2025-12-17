package com.example.camunda.optimize.es;

import io.camunda.plugin.search.header.CustomHeader;
import io.camunda.plugin.search.header.DatabaseCustomHeaderSupplier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ElasticApiKeyHeaderPlugin implements DatabaseCustomHeaderSupplier {

  private static final String ENV_B64 = "ES_API_KEY_B64";    // base64 of: <id>:<apiKey>
  private static final String ENV_ID  = "ES_API_KEY_ID";
  private static final String ENV_KEY = "ES_API_KEY_VALUE";

  private final String authorizationValue;

  public ElasticApiKeyHeaderPlugin() {
  	System.out.println("IN PLUGIN!!!!!!!!");
    String b64 = trimToNull(System.getenv(ENV_B64));
    if (b64 == null) {
      String id = trimToNull(System.getenv(ENV_ID));
      String key = trimToNull(System.getenv(ENV_KEY));
      if (id != null && key != null) {
        String combined = id + ":" + key;
        b64 = Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
      }
    }
    if (b64 == null) {
      throw new IllegalStateException(
          "No API key found. Provide ES_API_KEY_B64 or both ES_API_KEY_ID and ES_API_KEY_VALUE.");
    }
    this.authorizationValue = "ApiKey " + b64;
  }

  @Override
  public CustomHeader getSearchDatabaseCustomHeader() {
    return new CustomHeader("Authorization", authorizationValue);
  }

  private static String trimToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}