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
import com.miracle.cognitive.global.bean.Velocity;
import com.miracle.common.bean.FeatureWithEstimates;
import com.miracle.common.response.FeatureResponse;
import com.miracle.database.bean.Release;
import com.miracle.utility.DataUtility;

@RestController
public class CogGatewayController {

	@Autowired
	private ServiceInvocation serviceInvocation;
	@Autowired
	private DataUtility dataUtility;

	@GetMapping(value = "/gatewayService")
	public ResponseEntity<FeatureResponse> buildFeatures(@RequestParam(value = "version") double version)
			throws ParseException {
		FeatureResponse response = new FeatureResponse();
		try {
			Release release = dataUtility.loadRelease(version);
			if (release == null) {
				response.setObject("Invalid version provided");
				response.setSuccess(false);
				return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_REQUEST);
			}
			CompletableFuture<List<FeatureWithEstimates>> featureEstimates = serviceInvocation
					.invokeBacklogService(version);
			CompletableFuture<List<Velocity>> storyStateDetails = serviceInvocation.invokeBandwidthService(version);

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("featureEstimates", featureEstimates.get());
			System.out.println("Completed backlog service invocation");
			dataMap.put("storyStateDetails", storyStateDetails.get());
			System.out.println("Completed bandwidth calculation service invocation");

			RestTemplate restTemplate = new RestTemplate();
			String releaseServiceUri = "http://releasereport-service:8080/releasereport-service/releaseReport";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<?> entity = new HttpEntity<Object>(dataMap, headers);

			ResponseEntity<FeatureResponse> releaseReportResponse = restTemplate.postForEntity(releaseServiceUri,
					entity, FeatureResponse.class);
			System.out.println("Completed release report service invocation");
			return releaseReportResponse;
		} catch (Exception exception) {
			exception.printStackTrace();
			response.setObject("Failed to build release features");
			response.setSuccess(false);
			return new ResponseEntity<FeatureResponse>(response, HttpStatus.BAD_GATEWAY);
		}
	}

}
