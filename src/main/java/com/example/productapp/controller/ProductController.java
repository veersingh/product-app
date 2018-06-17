package com.example.productapp.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class ProductController {

	@Autowired
	KeycloakRestTemplate restemplate;

	@GetMapping(path = "/products")
	public String getProducts(Principal principal, Model model) {
		// ResponseEntity<String[]> response =
		// restemplate.getForEntity("http://localhost:8094/products", String[].class);

		// model.addAttribute("products", response.getBody());
		List<String> products = Arrays.asList("iPad", "iPhone", "iPod");
		model.addAttribute("principal", principal);
		model.addAttribute("products", products);
		return "products";

	}

	@GetMapping(path = "/logout")
	public String logout(HttpServletRequest request) throws ServletException {
		request.logout();
		return "/";
	}
}
