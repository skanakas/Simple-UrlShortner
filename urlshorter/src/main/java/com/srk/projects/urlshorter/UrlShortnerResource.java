package com.srk.projects.urlshorter;

import java.nio.charset.StandardCharsets;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

@RestController
@RequestMapping("tinyUrl")
public class UrlShortnerResource {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@GetMapping("/{id}")
	public String getUrl(@PathVariable(name = "id") String id) {
		
		String URL = redisTemplate.opsForValue().get(id);
		
		if(URL == null) {
			throw new RuntimeException("URL not found for ="+id);
		}
		
		return URL;
	}
	
	@PostMapping
	public String create(@RequestBody String url) {
		UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
		if(!urlValidator.isValid(url)) {
			throw new RuntimeException("Invalid URL");
		}
		
		String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
		
		redisTemplate.opsForValue().set(id, url);
		System.out.println("ID generated = "+id);
		return id;
	}

}
