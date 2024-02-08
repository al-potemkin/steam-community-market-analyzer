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

/**
 * Path to postman collection: src/main/resources/postman/PriceController.postman_collection.json
 */
@RestController
@AllArgsConstructor
@RequestMapping("/price")
public class PriceController {
    private MarketService marketService;
    private PriceCalculator priceCalculator;
    private CostAnalysisService costAnalysisService;

    /**
     * Request for basic information about an item
     *
     * @param itemName    item name
     * @param currency    supported currencies
     * @param application application to which the item belongs
     */
    @GetMapping
    public SteamTradeMarketResponse getItemPriceInformation(@RequestParam("item") String itemName,
                                                            @RequestParam("currency") Currency currency,
                                                            @RequestParam("application") Application application) {
        return marketService.getItemPriceInformation(itemName, currency, application);
    }

    /**
     * Request for information about bundle.
     * Takes information both about the set separately and about each item - summarizing it provides information for
     * comparison if you buy the set separately
     *
     * @param items       listing sets and things they consist of
     * @param currency    supported currencies
     * @param application application to which the item belongs
     */
    @GetMapping("/items")
    public List<BundlePriceInfo> calculateBundlePrices(@RequestBody List<Bundle> items,
                                                       @RequestParam("currency") Currency currency,
                                                       @RequestParam("application") Application application) {
        return priceCalculator.calculateBundlePrices(items, currency, application);
    }

    /**
     * Compares things with each other and finds a set that will be the most profitable among the rest
     */
    @GetMapping("/lowest")
    public BundlePriceInfo findLowestPriceBundle(@RequestBody List<BundlePriceInfo> bundlePriceInfo) {
        return costAnalysisService.findLowestPriceBundle(bundlePriceInfo);
    }

    /**
     * Mechanics of the Dota 2 game.
     * Used to analyze the profit if you convert N lower level sets to convert into a set of the next level.
     *
     * @param bundlePriceInfo        selection of the most profitable sets of different levels
     * @param numberOfItemsToRecycle in some events, the number of converted sets may differ.
     * @return text verdict with data regarding whether the conversion is profitable or not
     */
    @GetMapping("/compare")
    public List<String> compare(@RequestBody List<BundlePriceInfo> bundlePriceInfo,
                                @RequestParam("numberOfItemsToRecycle") int numberOfItemsToRecycle) {
        return costAnalysisService.compareTiers(bundlePriceInfo, numberOfItemsToRecycle);
    }

    /**
     * Combining calculateBundlePrices / findLowestPriceBundle / compare endpoints logic for simple data manipulation
     * and obtaining results
     */
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
