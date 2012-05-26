package com.example.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.example.model.Terceiro;

@Controller
public class TerceiroController {

	@Autowired
	private RestTemplate restTemplate;
	
	private @Autowired HttpServletRequest request;
	
	@RequestMapping("/terceiros")
	public String listarTerceiros(Map<String, Object> map)
	{
		if(request.getSession().getAttribute("access_token") != null)
		{
			HttpHeaders headers = new HttpHeaders(); 
			headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
			
			HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
			
			ResponseEntity<ArrayList> exchange = restTemplate.exchange("https://vpsa-oauth-server.herokuapp.com/terceiros.json?token=" + request.getSession().getAttribute("access_token"), HttpMethod.GET, httpEntity, ArrayList.class,  (Object)null);
			
			map.put("terceiros", exchange.getBody());
			
			return "terceiros";
		}
		else
		{
			request.getSession().setAttribute("url_before_auth", request.getPathInfo());
			return "redirect:/mvc/auth/oauth";
		}
	}
	
	@RequestMapping("/terceiros/{idTerceiro}")
	public String getTerceiroPorId(@PathVariable("idTerceiro") Long idTerceiro, Map<String, Object> map)
	{
		if(request.getSession().getAttribute("access_token") != null)
		{
			HttpHeaders headers = new HttpHeaders(); 
			headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
			
			HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
			
			ResponseEntity<Terceiro> exchange = restTemplate.exchange("https://vpsa-oauth-server.herokuapp.com/terceiros/" + idTerceiro + ".json?token=" + request.getSession().getAttribute("access_token"), HttpMethod.GET, httpEntity, Terceiro.class,  (Object)null);
			
			map.put("terceiro", exchange.getBody());
			
			return "show";
		}
		else
		{
			request.getSession().setAttribute("url_before_auth", request.getPathInfo());
			return "redirect:/mvc/auth/oauth";
		}
	}
}
