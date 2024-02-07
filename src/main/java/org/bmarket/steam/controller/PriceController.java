package org.bmarket.steam.controller;

import lombok.AllArgsConstructor;
import org.bmarket.steam.entity.Bundle;
import org.bmarket.steam.entity.BundlePriceInfo;
import org.bmarket.steam.entity.SteamTradeMarketResponse;
import org.bmarket.steam.entity.enums.Application;
import org.bmarket.steam.entity.enums.Currency;
import org.bmarket.steam.service.CostAnalysisService;
import org.bmarket.steam.service.MarketService;
import org.bmarket.steam.service.PriceCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/price")
public class PriceController {
    private MarketService marketService;
    private PriceCalculator priceCalculator;
    private CostAnalysisService costAnalysisService;

    @GetMapping
    public SteamTradeMarketResponse getItemPriceInformation(@RequestParam("item") String itemName,
                                                            @RequestParam("currency") Currency currency,
                                                            @RequestParam("application") Application application) {
        return marketService.getItemPriceInformation(itemName, currency, application);
    }

    @GetMapping("/items")
    public List<BundlePriceInfo> calculateBundlePrices(@RequestBody List<Bundle> items,
                                                       @RequestParam("currency") Currency currency,
                                                       @RequestParam("application") Application application) {
        return priceCalculator.calculateBundlePrices(items, currency, application);
    }

    @GetMapping("/lowest")
    public BundlePriceInfo findLowestPriceBundle(@RequestBody List<BundlePriceInfo> bundlePriceInfo) {
        return costAnalysisService.findLowestPriceBundle(bundlePriceInfo);
    }

    @GetMapping("/compare")
    public List<String> compare(@RequestBody List<BundlePriceInfo> bundlePriceInfo,
                                @RequestParam("numberOfItemsToRecycle") int numberOfItemsToRecycle) {
        return costAnalysisService.compareTiers(bundlePriceInfo, numberOfItemsToRecycle);
    }

    @GetMapping("/compare/full")
    public List<String> compareBundles(@RequestBody List<Bundle> items,
                                       @RequestParam("currency") Currency currency,
                                       @RequestParam("application") Application application,
                                       @RequestParam("numberOfItemsToRecycle") int numberOfItemsToRecycle) {
        var bundlePrices = priceCalculator.calculateBundlePrices(items, currency, application);
        var lowestBundlePrices = costAnalysisService.findLowestPriceBundleByTier(bundlePrices);
        return costAnalysisService.compareTiers(lowestBundlePrices, numberOfItemsToRecycle);
    }
}
