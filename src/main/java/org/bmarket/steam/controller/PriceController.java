package org.bmarket.steam.controller;

import lombok.AllArgsConstructor;
import org.bmarket.steam.entity.Bundle;
import org.bmarket.steam.entity.BundlePriceInfo;
import org.bmarket.steam.entity.SteamTradeMarketResponse;
import org.bmarket.steam.entity.enums.Application;
import org.bmarket.steam.entity.enums.Currency;
import org.bmarket.steam.service.MarketService;
import org.bmarket.steam.service.PriceCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class PriceController {
    private MarketService marketService;
    private PriceCalculator priceCalculator;

    @GetMapping("/price")
    public SteamTradeMarketResponse getItemPriceInformation(@RequestParam("item") String itemName,
                                                            @RequestParam("currency") Currency currency,
                                                            @RequestParam("application") Application application) {
        return marketService.getItemPriceInformation(itemName, currency, application);
    }

    @GetMapping("/price/items")
    public List<BundlePriceInfo> calculateBundlePrices(@RequestBody List<Bundle> items,
                                                       @RequestParam("currency") Currency currency,
                                                       @RequestParam("application") Application application) {
        return priceCalculator.calculateBundlePrices(items, currency, application);
    }
}
