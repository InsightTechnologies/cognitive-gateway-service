package com.miracle.cog.gateway.utility;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.miracle.common.bean.FeatureWithEstimates;
import com.miracle.common.response.FeatureResponse;

@Service
public class ServiceInvocation {

	@Async
	public CompletableFuture<List<FeatureWithEstimates>> invokeBacklogService() throws Exception {
		String backlogServiceUrl = "";
		URI uri = new URI(backlogServiceUrl);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<FeatureResponse> result = restTemplate.getForEntity(uri, FeatureResponse.class);
		if (!result.getBody().isSuccess()) {
			throw new Exception("Retrieving backlog service response failed");
		}
		List<FeatureWithEstimates> featureWithEstimates = (List<FeatureWithEstimates>) result.getBody().getObject();
		return CompletableFuture.completedFuture(featureWithEstimates);
	}

	@Async
	public CompletableFuture<Map<String, Double>> invokeBandwidthService() throws Exception {
		String bandwidthServiceUrl = "";
		URI uri = new URI(bandwidthServiceUrl);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<FeatureResponse> result = restTemplate.getForEntity(uri, FeatureResponse.class);
		if (!result.getBody().isSuccess()) {
			throw new Exception("Retrieving bandwidth service response failed");
		}
		Map<String, Double> storySprintData = (Map<String, Double>) result.getBody().getObject();
		return CompletableFuture.completedFuture(storySprintData);
	}
}
