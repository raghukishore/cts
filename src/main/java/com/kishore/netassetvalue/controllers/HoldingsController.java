package com.kishore.netassetvalue.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kishore.netassetvalue.beans.Holder;
import com.kishore.netassetvalue.beans.Price;
import com.kishore.netassetvalue.util.ExternalRestServiceClient;

@RestController
public class HoldingsController {
	@Autowired
	ExternalRestServiceClient client;
	private List<Holder> allHoldings = new ArrayList<>();
	private List<Price> allHoldingsPrice = new ArrayList<>();
	private static final ObjectMapper MAPPER_NON_NULL = new ObjectMapper();

	@GetMapping("/holdings")
	public List<Holder> getAllHoldings() {
		if (allHoldings.size() > 0) {
			return allHoldings;
		} else
			return convertStringToHolding(client.getHoldingsData());
	}

	@GetMapping("/price")
	public List<Price> getAllHoldingsPrice() {
		if (allHoldingsPrice.size() > 0) {
			return allHoldingsPrice;
		} else
			return convertStringToHoldingsPrice(client.getPriceData());
	}

	@GetMapping("/price/{date}")
	public List<Price> getHoldingPrice(@PathVariable String date) {
		List<Price> listOfPricesOfHoldingsOnDate = null;
		if (date != null && !"".equals(date.trim())) {
			List<Price> listOfAllHoldingsPrice = getAllHoldingsPrice();
			listOfPricesOfHoldingsOnDate = listOfAllHoldingsPrice.stream()
					.filter(currHold -> date.equals(currHold.getDate())).collect(Collectors.toList());
		}
		return listOfPricesOfHoldingsOnDate;
	}

	@GetMapping("/holdings/{date}")
	public List<Holder> getHoldingsOn(@PathVariable String date) {
		List<Holder> listOfHoldingsOnDate = null;
		if (date != null && !"".equals(date.trim())) {
			List<Holder> listOfAllHoldings = getAllHoldings();
			listOfHoldingsOnDate = listOfAllHoldings.stream().filter(currHold -> date.equals(currHold.getDate()))
					.collect(Collectors.toList());
		}
		return listOfHoldingsOnDate;
	}

	public double getPriceOfSecurity(String date, String sec) {
		List<Price> listOfPricesOfHoldingsOnDate = getHoldingPrice(date);
		List<Price> pList = null;
		double price = 0.0;
		if (sec != null && !"".equals(sec.trim())) {
			pList = listOfPricesOfHoldingsOnDate.stream().filter(p -> sec.equals(p.getSecurity()))
					.collect(Collectors.toList());
		}
		if (pList != null && !pList.isEmpty()) {
			price = pList.get(0).getPrice();
		}
		return price;
	}

	@GetMapping("netasset/{date}")
	public double getNetAssetValueOnDate(@PathVariable String date) {
		double netAssetValue = 0.0;
		List<Holder> listOfHoldingsOnDate = getHoldingsOn(date);
		for (Holder hld : listOfHoldingsOnDate) {
			netAssetValue = netAssetValue + (hld.getQuantity() * getPriceOfSecurity(date, hld.getSecurity()));
		}
		return netAssetValue;
	}

	public List<Price> convertStringToHoldingsPrice(String response) {
		try {
			allHoldingsPrice = (List<Price>) MAPPER_NON_NULL.readValue(response, new TypeReference<List<Price>>() {
			});
			return allHoldingsPrice;
		} catch (IOException e) {
		}
		return null;
	}

	public List<Holder> convertStringToHolding(String response) {
		try {
			allHoldings = (List<Holder>) MAPPER_NON_NULL.readValue(response, new TypeReference<List<Holder>>() {
			});
			return allHoldings;
		} catch (IOException e) {
		}
		return null;
	}
}
