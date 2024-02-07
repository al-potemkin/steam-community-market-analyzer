package org.bmarket.steam.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bmarket.steam.entity.enums.Application;
import org.bmarket.steam.entity.enums.Currency;
import org.bmarket.steam.entity.SteamTradeMarketResponse;
import org.bmarket.steam.exception.RedirectionUrlNotFoundException;
import org.bmarket.steam.service.retry.RetryLogic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log4j2
@Service
@AllArgsConstructor
public class MarketService {
    private static final String URL = "http://steamcommunity.com/market/priceoverview/?currency=%s" +
            "&format=json" +
            "&appid=%s" +
            "&market_hash_name=%s";

    private RestTemplate restTemplate;

    public SteamTradeMarketResponse getItemPriceInformation(String itemName,
                                                            Currency currency,
                                                            Application application) {
        var tradeMarketUrl = buildTradeMarketUrl(itemName, currency, application);
        var redirectUrl = getRedirectLink(tradeMarketUrl);
        log.debug("Redirect url-[{}]: {}", itemName, redirectUrl);
        var item = sendRequestToReceiveInformationAboutItem(redirectUrl);
        log.info("[{}]: {}", itemName, item);
        return item;
    }

    /**
     * Can complete 20 requests sequentially, after which ban occurs for 50 seconds
     */
    private SteamTradeMarketResponse sendRequestToReceiveInformationAboutItem(String url) {
        Supplier<SteamTradeMarketResponse> supplier = () -> restTemplate.getForEntity(url, SteamTradeMarketResponse.class).getBody();
        return RetryLogic.retry(supplier, 14, 5, TimeUnit.SECONDS).get();
    }

    private String getRedirectLink(String marketUrl) {
        var response = restTemplate.getForEntity(marketUrl, String.class);
        var isLocationHeaderReceived = checkForSpecificHeader(response);
        if (!isLocationHeaderReceived) {
            throw new RedirectionUrlNotFoundException("LOCATION header isn't present in response");
        }
        return response.getHeaders()
                .get(HttpHeaders.LOCATION)
                .getFirst();
    }

    private String buildTradeMarketUrl(String itemName,
                                       Currency currency,
                                       Application application) {
        return String.format(URL,
                currency.getCode(),
                application.getApplicationId(),
                URLEncoder.encode(itemName, StandardCharsets.UTF_8).replace("+", "%20"));
    }

    private boolean checkForSpecificHeader(ResponseEntity<String> response) {
        return HttpStatus.FOUND == response.getStatusCode()
                && response.getHeaders().containsKey(HttpHeaders.LOCATION);
    }
}
