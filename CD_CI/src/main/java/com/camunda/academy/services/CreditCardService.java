package com.camunda.academy.services;

import com.camunda.academy.handler.CreditDeductionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
public class CreditCardService {

  Logger logger = LoggerFactory.getLogger(CreditDeductionHandler.class);
  public void chargeAmount(String cardNumber, String cvc, String expiryDate, Double amount) {
    logger.info("charging card {} that expires on {} and has cvc {} with amount of {} {}",
      cardNumber, expiryDate, cvc, amount, System.lineSeparator());

    logger.info("payment completed");
  }
}