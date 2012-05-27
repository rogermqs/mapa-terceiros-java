package com.example.controller;

import java.util.HashMap;
import java.util.List;
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
	
	private @Autowired MapsController mapsController;
	
	@RequestMapping("/terceiros")
	public String listarTerceiros(Map<String, Object> map)
	{
		if(request.getSession().getAttribute("access_token") != null)
		{
			HttpHeaders headers = new HttpHeaders(); 
			headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
			
			HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
			
			String url = "https://vpsa-oauth-server.herokuapp.com/terceiros.json?token=" + request.getSession().getAttribute("access_token");
			ResponseEntity<Terceiro[]> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Terceiro[].class,  (Object)null);
			
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
			
			String url = "https://vpsa-oauth-server.herokuapp.com/terceiros/" + idTerceiro + ".json?token=" + request.getSession().getAttribute("access_token");
			
			ResponseEntity<Terceiro> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Terceiro.class,  (Object)null);
			
			Terceiro terceiro = exchange.getBody();
			
			List resultadoPesquisa = mapsController.search(terceiro.getEndereco().getEnderecoCompleto());
			
			if(resultadoPesquisa.size() > 0)
			{
				String enderecoFormatado = getEnderecoFormatado(resultadoPesquisa);
				
				map.put("endereco_formatado", enderecoFormatado);
				map.put("latitude", getLatitude(resultadoPesquisa));
				map.put("longitude", getLongitude(resultadoPesquisa));
			}
			else
			{
				map.put("latitude", 0);
				map.put("longitude", 0);
			}
			
			
			map.put("terceiro", terceiro);
			
			return "show";
		}
		else
		{
			request.getSession().setAttribute("url_before_auth", request.getPathInfo());
			return "redirect:/mvc/auth/oauth";
		}
	}

	private String getLatitude(List pesquisa) {
		return ((HashMap) ((HashMap)((HashMap)pesquisa.get(0)).get("geometry")).get("location")).get("lat").toString();
	}
	
	private String getLongitude(List pesquisa) {
		return ((HashMap) ((HashMap)((HashMap)pesquisa.get(0)).get("geometry")).get("location")).get("lng").toString();
	}

	private String getEnderecoFormatado(List pesquisa) {
		return (String) ((HashMap)pesquisa.get(0)).get("formatted_address");
	}
}
