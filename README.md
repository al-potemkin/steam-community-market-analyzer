## Steam - Community market analyzer

This project is conceived as a study of open data on the prices of items within the in-game market
of [Dota 2](https://www.dota2.com/home?l=english) and other
games available on the [Steam platform](https://steamcommunity.com/market/).

The idea for the project was inspired by the in-game mechanics of one of the events in [Dota
2](https://www.dota2.com/home?l=english) - [Frostivus 2023](https://www.dota2.com/frostivus2023?l=english). The
essence is that items from the event's chest are divided into levels (items of different levels can drop from the chest,
regardless of their sequence). To advance to the next level, players are provided with the "set/item transformation"
mechanic:

- to obtain a set/item of the next level, a player needs to transform 5 (within
  the [Frostivus 2023 event](https://www.dota2.com/frostivus2023?l=english)) sets/items from the same chest (positioned
  as a way to eliminate duplicates)
- 5 levels available in total (within the [Frostivus 2023 event](https://www.dota2.com/frostivus2023?l=english)),
  but the highest level available for conversion is the 4th (5 sets/items of the 3rd level are converted into 1 set/item
  of the 4th level)
- as far as I know, this mechanic was present in the game during
  the [Diretide 2022 event](https://www.dota2.com/diretide/?l=english)
- game also features a mechanic for combining items from an already opened set, which returns it to its original
  closed state, making it eligible for further conversion.

To optimize funds and make the most cost-effective investment in cosmetic items, the idea arose to calculate the price
difference between open (when all items need to be purchased separately) and closed (complete set) states, and find the
most advantageous option for both purchase and transformation.

---

During the development process, I encountered the main and only difficulty - the lack of any open API from the Steam
side to process market data available for research. According to
this [stackoverflow](https://stackoverflow.com/questions/29902280/get-price-of-item-in-steam-community-market-with-json),
some data can still be obtained. Through research, I found that with iterative requests to the endpoint, similar
requests (in my case, the difference was only in different item names), I discovered that every ***~20 requests***
result in a
***temporary ban*** on sending new requests for about ***50 seconds***. This may increase processing time depending on
the number of
requests to the endpoint.

> Note: this application ***does not use data*** from ***your real or any Steam account*** in any way.
> A ban in this case means the
> inability to resend a request to the same endpoint within a short period of time. In case of any difficulties with
> resending the request, change the IP in any way convenient for you, such as rebooting the router if you have a dynamic
> binding.
---

#### Implemented functionality includes next requests:

- Request for basic information about an item

```curl
http://localhost:8080/price
```

- Request for information about bundle

```curl
http://localhost:8080/price/items
```

- Compares things with each other and finds a set that will be the most profitable among the rest

```curl
http://localhost:8080/price/lowest
```

- Used to analyze the profit if you convert N lower level sets to convert into a set of the next level

```curl
http://localhost:8080/price/compare
```

- Combining endpoints logic for simple data manipulation and obtaining results

```curl
http://localhost:8080/price/compare/full
```

---

##### For testing, you can use the data located in the following path: `src/main/resources/chests/`

##### Path to postman collection: `src/main/resources/postman/PriceController.postman_collection.json`
