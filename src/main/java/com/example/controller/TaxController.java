package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.service.TaxService;
import com.example.constants.Message;
import com.example.model.Tax;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

	@GetMapping("/{id}")
	public String show(Model model, @PathVariable("id") Long id) {
		if (id != null) {
			Optional<Tax> tax = taxService.findOne(id);
			model.addAttribute("tax", tax.get());
		}
		return "tax/show";
	}

	@GetMapping(value = "/new")
	public String create(Model model, @ModelAttribute Tax entity) {
		model.addAttribute("tax", entity);
		return "tax/form";
	}

	@PostMapping
	public String create(@ModelAttribute Tax entity, BindingResult result,
			RedirectAttributes redirectAttributes) {
		List<Tax> all = taxService.findAll();
		Set<Integer> rateSet = new HashSet<>();
		for (Tax tax : all) {
			rateSet.add(tax.getRate());
		}
		try {
			if (entity.getName() == null || entity.getName().length() == 0 ||
					entity.getRate() == null) {
				throw new Exception();
			} else if (rateSet.contains(entity.getRate())) {
				throw new Exception();
			}

			for (int i = 0; i < 6; i++) {
				Tax tax = new Tax();
				tax.setName(entity.getName());
				tax.setRate(entity.getRate());
				tax.setInUse(0);
				if (i % 2 == 0) {
					tax.setIncluded(true);
				} else {
					tax.setIncluded(false);
				}
				tax.setRounding((i / 2) % 3);
				taxService.save(tax);
			}
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_INSERT);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();
		}
		return "redirect:/taxes";
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			if (id != null) {
				Optional<Tax> entity = taxService.findOne(id);
				if (entity.get().getInUse() == 0) {
					taxService.delete(entity.get());
					redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_DELETE);
				} else {
					throw new Exception("現在使用中です");
				}
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/taxes";
	}
}
