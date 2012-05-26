package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.example.model.Authorization;
import com.example.model.RequestInformation;

@Controller
public class OauthController {

	@Autowired
	private RestTemplate restTemplate;
	
	private @Autowired HttpServletRequest request;
	
	@RequestMapping("/oauth/vpsa/callback")
	public String callback()
	{
		HttpHeaders headers = new HttpHeaders(); 
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<RequestInformation> httpEntity = new HttpEntity<RequestInformation>(getRequestInformation(request.getParameter("code")), headers);
		
		ResponseEntity<Authorization> exchange = restTemplate.exchange("https://vpsa-oauth-server.herokuapp.com/oauth/token", HttpMethod.POST, httpEntity, Authorization.class,  (Object)null);
		request.getSession().setAttribute("access_token", exchange.getBody().getAccess_token());	    	
			    		
		return "redirect:/mvc" + request.getSession().getAttribute("url_before_auth");
	}
	
	private RequestInformation getRequestInformation(String requestToken) {
		RequestInformation requestInformation = new RequestInformation();
		
		requestInformation.setCode(requestToken);
		requestInformation.setRedirect_uri("http://localhost:8080/mapa-terceiros-java/mvc/oauth/vpsa/callback");
		requestInformation.setClient_secret("00dfecbb71109cabf885cb155c997e1fc0bb758d053b783d0b2f1479309e6691");
		requestInformation.setClient_id("https://vpsa-oauth-server.herokuapp.com/clients/4fc017ef9343dd0001000011");
		requestInformation.setGrant_type("authorization_code");
		
		return requestInformation;
	}

	@RequestMapping("/auth/oauth")
	public String auth()
	{
		String urlAuth = "https://vpsa-oauth-server.herokuapp.com/oauth/authorization?";
		urlAuth += "response_type=code&";
		urlAuth += "scope=all&";
		urlAuth += "client_id=https://vpsa-oauth-server.herokuapp.com/clients/4fc017ef9343dd0001000011&";
		urlAuth += "redirect_uri=http://localhost:8080/mapa-terceiros-java/mvc/oauth/vpsa/callback";
			    		
		return "redirect:" + urlAuth;
	}
}
