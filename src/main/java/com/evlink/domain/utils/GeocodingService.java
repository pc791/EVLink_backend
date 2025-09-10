package com.evlink.domain.utils;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

	@Value("${naver.api.geocoding.clientId}")
	private String clientId;

	@Value("${naver.api.geocoding.clientSecret}")
	private String clientSecret;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public double[] getCoordinatesFromAddress(String address) throws Exception {
		URI uri = UriComponentsBuilder.fromUriString("https://maps.apigw.ntruss.com/map-geocode/v2/geocode")
				.queryParam("query", address).encode().build().toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
		headers.set("X-NCP-APIGW-API-KEY", clientSecret);


		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

		// API 응답 로그 출력 (디버깅용)
		System.out.println("Naver Geocoding API Response: " + response.getStatusCode());

		// JSON 응답 파싱
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode addressesNode = rootNode.path("addresses");

		if (addressesNode.isArray() && addressesNode.size() > 0) {
			JsonNode firstAddress = addressesNode.get(0);
			double longitude = firstAddress.path("x").asDouble();
			double latitude = firstAddress.path("y").asDouble();
			return new double[] { latitude, longitude };
		}

		throw new IllegalArgumentException("주소를 위도, 경도로 변환할 수 없습니다.");
	}
}