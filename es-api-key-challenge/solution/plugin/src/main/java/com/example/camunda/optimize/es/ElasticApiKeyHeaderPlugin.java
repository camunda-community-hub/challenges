package com.example.camunda.optimize.es;

import io.camunda.plugin.search.header.CustomHeader;
import io.camunda.plugin.search.header.DatabaseCustomHeaderSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ElasticApiKeyHeaderPlugin implements DatabaseCustomHeaderSupplier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticApiKeyHeaderPlugin.class);

  private static final String ENV_B64 = "ES_API_KEY_B64";      // Base64 of: <id>:<apiKey>
  private static final String ENV_ID  = "ES_API_KEY_ID";
  private static final String ENV_KEY = "ES_API_KEY_VALUE";

  private final String authorizationValue;

  public ElasticApiKeyHeaderPlugin() {
    LOGGER.info("Initializing ElasticApiKeyHeaderPlugin");

    String b64 = trimToNull(System.getenv(ENV_B64));

    if (b64 != null) {
      LOGGER.debug("Using Base64 API key from environment variable {}", ENV_B64);
    } else {
      String id = trimToNull(System.getenv(ENV_ID));
      String key = trimToNull(System.getenv(ENV_KEY));

      if (id != null && key != null) {
        String combined = id + ":" + key;
        b64 = Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
        LOGGER.debug("Constructed Base64 API key from environment variables {} and {}", ENV_ID, ENV_KEY);
      }
    }

    if (b64 == null) {
      LOGGER.error("No API key found. Provide {} or both {} and {}", ENV_B64, ENV_ID, ENV_KEY);
      throw new IllegalStateException(
              "No API key found. Please provide ES_API_KEY_B64 or both ES_API_KEY_ID and ES_API_KEY_VALUE."
      );
    }

    this.authorizationValue = "ApiKey " + b64;
    LOGGER.info("ElasticApiKeyHeaderPlugin initialized successfully");
  }

  @Override
  public CustomHeader getSearchDatabaseCustomHeader() {
    LOGGER.debug("Providing custom Authorization header for Elasticsearch connection");
    return new CustomHeader("Authorization", authorizationValue);
  }

  private static String trimToNull(String s) {
    if (s == null) return null;
    String trimmed = s.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}