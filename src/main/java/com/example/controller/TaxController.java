package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.service.TaxService;
import com.example.model.Tax;

import java.util.List;

@Controller
@RequestMapping("/taxes")
public class TaxController {

	@Autowired
	private TaxService taxService;

	@GetMapping
	public String index(Model model) {
		List<Tax> all = taxService.findAll();
		model.addAttribute("listTax", all);
		return "tax/index";
	}
}
