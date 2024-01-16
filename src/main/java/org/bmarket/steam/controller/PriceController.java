package org.bmarket.steam.controller;

import lombok.AllArgsConstructor;
import org.bmarket.steam.entity.*;
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
    public List<Item> calculateBundlePrices(@RequestBody List<Bundle> items,
                                            @RequestParam("currency") Currency currency,
                                            @RequestParam("application") Application application) {
        return priceCalculator.calculateBundlePrices(items, currency, application);
    }

    @GetMapping("/lowest-price")
    public Item getLowestPriceBundle(@RequestBody List<Bundle> items,
                                     @RequestParam("currency") Currency currency,
                                     @RequestParam("application") Application application) {
        var bundles = priceCalculator.calculateBundlePrices(items, currency, application);
        return priceCalculator.getLowestPriceBundle(bundles);
    }
}
