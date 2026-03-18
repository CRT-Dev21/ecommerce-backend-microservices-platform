package com.ecommerce.crtdev.payment_service.client;

import com.ecommerce.crtdev.payment_service.dto.BankInfoResponse;
import com.ecommerce.crtdev.payment_service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class SellerClient {

    private static final Logger log = LoggerFactory.getLogger(SellerClient.class);

    private final WebClient sellerWebClient;

    public SellerClient(WebClient sellerWebClient) {
        this.sellerWebClient = sellerWebClient;
    }

    public BankInfoResponse getBankInfo(Long sellerId) {
        try {
            return sellerWebClient.get()
                    .uri("/sellers/{sellerId}/bank-info", sellerId)
                    .retrieve()
                    .onStatus(
                            status -> status == HttpStatus.NOT_FOUND ||
                                    status == HttpStatus.UNPROCESSABLE_ENTITY,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new SellerNotFoundException(sellerId))
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new SellerBankInfoUnavailableException(
                                            "Seller service error for seller: " + sellerId))
                    )
                    .bodyToMono(BankInfoResponse.class)
                    .block();

        } catch (SellerNotFoundException | SellerBankInfoUnavailableException e) {
            throw e;
        } catch (WebClientResponseException e) {
            log.error("Seller client HTTP error status={} sellerId={}", e.getStatusCode(), sellerId);
            throw new SellerBankInfoUnavailableException(
                    "Could not get bank info for seller: " + sellerId);
        } catch (Exception e) {
            log.error("Unexpected error calling seller service for sellerId={}", sellerId, e);
            throw new SellerBankInfoUnavailableException(
                    "Seller service unreachable for seller: " + sellerId);
        }
    }
}
