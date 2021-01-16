package com.kishore.netassetvalue.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalRestServiceClient {
	
	@Autowired
	RestTemplate restTemplate;

	public String getHoldingsData() {
		return restTemplate.getForObject(
				"https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/holding", String.class);
	}
	
	public String getPriceData() {
		return restTemplate.getForObject(
				"https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/pricing", String.class);
	}
	
}
