package com.example.productapp.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class ProductController {

	@Autowired
	KeycloakRestTemplate restemplate;

	@GetMapping(path = "/products")
	public String getProducts(Model model) {
		ResponseEntity<String[]> response = restemplate.getForEntity("http://localhost:8094/products", String[].class);

		model.addAttribute("products", response.getBody());
		return "products";
	}

	@GetMapping(path = "/logout")
	public String logout(HttpServletRequest request) throws ServletException {
		request.logout();
		return "/";
	}
}
