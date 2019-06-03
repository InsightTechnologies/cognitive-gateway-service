package com.miracle.cog.gateway.controller;

import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.miracle.cog.gateway.utility.ServiceInvocation;
import com.miracle.cognitive.response.FeatureResponse;
import com.miracle.scrum.bean.FeatureWithEstimates;

@RestController
public class CogGatewayController {
	@Autowired
	private ServiceInvocation serviceInvocation;

	@GetMapping(value = "/gatewayService")
	public ResponseEntity<FeatureResponse> buildFeatures(@RequestParam(value = "version") double version)
			throws ParseException {
		FeatureResponse response = new FeatureResponse();
		try {

			CompletableFuture<List<FeatureWithEstimates>> featureEstimcates = serviceInvocation.invokeBacklogService();
			CompletableFuture<Map<String, Double>> storyStateDetails = serviceInvocation.invokeBandwidthService();

			String releaseServiceUri = "";
			URI uri = new URI(releaseServiceUri);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<FeatureResponse> result = restTemplate.getForEntity(uri, FeatureResponse.class);

			response.setSuccess(true);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.OK);
		} catch (Exception exception) {
			exception.printStackTrace();
			response.setObject("Failed to build Backlog features");
			response.setSuccess(false);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_GATEWAY);
		}
	}

}
