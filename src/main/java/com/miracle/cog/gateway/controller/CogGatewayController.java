package com.miracle.cog.gateway.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.miracle.cog.gateway.utility.ServiceInvocation;
import com.miracle.common.bean.FeatureWithEstimates;
import com.miracle.common.response.FeatureResponse;

@RestController
public class CogGatewayController {
	@Autowired
	private ServiceInvocation serviceInvocation;

	@GetMapping(value = "/gatewayService")
	public ResponseEntity<FeatureResponse> buildFeatures(@RequestParam(value = "version") double version)
			throws ParseException {
		FeatureResponse response = new FeatureResponse();
		try {

			CompletableFuture<List<FeatureWithEstimates>> featureEstimates = serviceInvocation.invokeBacklogService();
			CompletableFuture<Map<String, Double>> storyStateDetails = serviceInvocation.invokeBandwidthService();

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("featureEstimates", featureEstimates.get());
			dataMap.put("storyStateDetails", storyStateDetails.get());

			RestTemplate restTemplate = new RestTemplate();
			String releaseServiceUri = "/releaseReport";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<?> entity = new HttpEntity<Object>(dataMap, headers);

			FeatureResponse releaseReportResponse = restTemplate.postForObject(releaseServiceUri, entity,
					FeatureResponse.class);

			response.setObject(releaseReportResponse);
			response.setSuccess(true);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.OK);
		} catch (Exception exception) {
			exception.printStackTrace();
			response.setObject("Failed to build release features");
			response.setSuccess(false);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_GATEWAY);
		}
	}

}
