package org.bmarket.steam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.bmarket.steam.entity.Application;
import org.bmarket.steam.entity.Currency;
import org.bmarket.steam.entity.SteamTradeMarketResponse;
import org.bmarket.steam.exception.ItemNotFoundException;
import org.bmarket.steam.exception.ItemRetryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@AllArgsConstructor
public class MarketService {
    private static final String URI = "http://steamcommunity.com/market/priceoverview/?currency=%s" + //18
            "&format=json" +
            "&appid=%s" + //570
            "&market_hash_name=%s";

    private RestTemplate restTemplate;

    public SteamTradeMarketResponse getItemPriceInformation(String itemName,
                                                            Currency currency,
                                                            Application application) {
        var tradeMarketUri = buildTradeMarketUri(itemName, currency, application);
        var response = restTemplate.getForEntity(tradeMarketUri, String.class);
        if (HttpStatus.FOUND == response.getStatusCode()
                && response.getHeaders().containsKey(HttpHeaders.LOCATION)) {
            var redirectUri = response.getHeaders()
                    .get(HttpHeaders.LOCATION)
                    .getFirst();
            return sendRedirectRequest(redirectUri, itemName);
        }
        throw new ItemNotFoundException(String.format("Cant find %s item", itemName));
    }

    private SteamTradeMarketResponse sendRedirectRequest(String uri, String itemName) {
        var getRequest = new HttpGet(uri);
        var httpClient = HttpClientBuilder.create().build();
        var retryer = RetryerBuilder.<SteamTradeMarketResponse>newBuilder()
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .build();
        try {
            return retryer.call(() -> {
                try (var response = httpClient.execute(getRequest)) {
                    var responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    var item = new ObjectMapper().readValue(responseBody, SteamTradeMarketResponse.class);
                    if (Objects.isNull(item)
                            || Objects.isNull(item.getSuccess())
                            || Boolean.FALSE == Boolean.parseBoolean(item.getSuccess())) {
                        log.info("----Banned by IP----");
                        throw new ItemRetryException(String.format("An error occurred while trying to get information about '%s' item", itemName));
                    }
                    log.info("{} item: {}", itemName, item);
                    return item;
                } catch (IOException e) {
                    throw new RuntimeException("Can't get an answer: ".concat(itemName));
                } catch (ParseException e) {
                    throw new RuntimeException("Can't parse an object: ".concat(itemName));
                }
            });
        } catch (ExecutionException | RetryException e) {
            throw new RuntimeException("Unexpected exception: " + e);
        }
    }

    private String buildTradeMarketUri(String itemName, Currency currency, Application application) {
        return String
                .format(URI, currency.getCode(), application.getApplicationId(), URLEncoder.encode(itemName, StandardCharsets.UTF_8)
                        .replace("+", "%20"));
    }
}
