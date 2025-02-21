package com.yscorp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpHeaders;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Test {

	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {

	}
}

public class NhisClient {
	private final RestTemplate restTemplate = new RestTemplate();

	public void fetchHtml(RequestHeadersDTO requestHeaders, String url, String outputFilePath) {
		HttpHeaders headers = new HttpHeaders();

		// 요청 헤더 추가
		headers.set("Accept", requestHeaders.getAccept());
		headers.set("Accept-Language", requestHeaders.getAcceptLanguage());
		headers.set("User-Agent", requestHeaders.getUserAgent());

		// 쿠키 추가 (객체에서 문자열 자동 생성)
		headers.set("Cookie", requestHeaders.getCookie().getCookieHeader());

		// 요청 엔터티 생성
		HttpEntity<String> entity = new HttpEntity<>(headers);

		// GET 요청 수행
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		// 응답을 HTML 파일로 저장
		saveResponseToFile(response.getBody(), outputFilePath);
	}

	private void saveResponseToFile(String content, String filePath) {
		try (FileWriter writer = new FileWriter(new File(filePath))) {
			writer.write(content);
			System.out.println("응답이 " + filePath + " 파일로 저장되었습니다.");
		} catch (IOException e) {
			System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
		}
	}
}


@Getter
@Setter
@NoArgsConstructor
class CookieDTO {
	private String wmonid;
	private String xtvid;
	private String locale;
	private String jsessionidNhisWww;
	private String voiceSpeed;
	private String voiceVolum;
	private String voiceStart;
	private String voiceStartX;
	private String fontColorIndex;
	private String bgColorIndex;
	private String zoomVal;
	private String netFunnelId;
	private String jsessionid;
	private String xloc;
	private String ssotoken;

	public String getCookieHeader() {
		return String.format(
			"WMONID=%s; XTVID=%s; locale=%s; JSESSIONID_NHIS_WWW=%s; voiceSpeed=%s; voiceVolum=%s; voiceStart=%s; " +
				"voiceStartX=%s; fontColorIndex=%s; bgColorIndex=%s; zoomVal=%s; NetFunnel_ID=%s; JSESSIONID=%s; xloc=%s; ssotoken=%s",
			wmonid, xtvid, locale, jsessionidNhisWww, voiceSpeed, voiceVolum, voiceStart,
			voiceStartX, fontColorIndex, bgColorIndex, zoomVal, netFunnelId, jsessionid, xloc, ssotoken
		);
	}
}

@Getter
@Setter
@NoArgsConstructor
class RequestHeadersDTO {
	private String accept;
	private String acceptLanguage;
	private String userAgent;
	private CookieDTO cookie; // 쿠키 DTO를 포함

	public RequestHeadersDTO(CookieDTO cookie) {
		this.cookie = cookie;
	}
}