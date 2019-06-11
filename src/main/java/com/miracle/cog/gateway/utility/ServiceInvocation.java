package com.miracle.cog.gateway.utility;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.miracle.cognitive.global.bean.Velocity;
import com.miracle.common.bean.FeatureWithEstimates;
import com.miracle.common.response.FeatureResponse;

@Service
public class ServiceInvocation {

	@Async
	public CompletableFuture<List<FeatureWithEstimates>> invokeBacklogService(double version) throws Exception {
		String backlogServiceUrl = "http://backlog-service:8080/backlog-service/backlog?version=" + version;
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
	public CompletableFuture<List<Velocity>> invokeBandwidthService(double version) throws Exception {
		String bandwidthServiceUrl = "http://bandwidth-service:8080/bandwidth-service/bandwidth?version=" + version;
		URI uri = new URI(bandwidthServiceUrl);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<FeatureResponse> result = restTemplate.getForEntity(uri, FeatureResponse.class);
		if (!result.getBody().isSuccess()) {
			throw new Exception("Retrieving bandwidth service response failed");
		}
		List<Velocity> velocityList = (List<Velocity>) result.getBody().getObject();
		return CompletableFuture.completedFuture(velocityList);
	}
}
