package com.example.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.constants.Message;
import com.example.form.OrderDeliveryForm;
import com.example.form.OrderShippingData;
import com.example.model.Order;
import com.example.model.OrderDelivery;
import com.example.service.OrderDeliveryService;
import com.example.service.OrderService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequestMapping("/orders/shipping")
public class ShippingController {

	@Autowired
	private OrderDeliveryService orderDeliveryService;

	@Autowired
	private OrderService orderService;

	@GetMapping
	public String index(Model model) {
		List<Order> orders = orderService.findByStatus("ordered");
		model.addAttribute("orders", orders);
		return "order/shipping";
	}

	@PutMapping
	public String update(@ModelAttribute("orderShippingData") OrderShippingData orderShippingData,
			RedirectAttributes redirectAttributes) {
		try {
			List<OrderDeliveryForm> orderShippingList = Arrays.asList(orderShippingData.getOrderShippingList());
			for (OrderDeliveryForm orderDeliveryForm : orderShippingList) {
				Long orderId = orderDeliveryForm.getOrderId();
				Order order = orderService.findOne(orderId).get();
				OrderDelivery orderDelivery = orderDeliveryService.findByOrderId(orderId).get();
				if (orderDeliveryForm.getChecked()) {
					if (order.getPaymentStatus().equals("paid")) {
						order.setStatus("completed");
					} else {
						order.setStatus("shipped");
					}
					orderService.save(order);
					orderDelivery.set(orderDeliveryForm);
					orderDeliveryService.save(orderDelivery);
				}
			}
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_UPDATE);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.getStackTrace();
		}
		return "redirect:/orders/shipping";
	}

	@PostMapping
	public String uploadFile(Model model, @RequestParam("file") MultipartFile uploadFile,
			RedirectAttributes redirectAttributes,
			@ModelAttribute("orderShippingData") OrderShippingData orderShippingData) {
		if (uploadFile.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "ファイルを選択してください");
		}
		if (!"text/csv".equals(uploadFile.getContentType())) {
			redirectAttributes.addFlashAttribute("error", "CSVファイルを選択してください");
		}

		try {
			List<OrderDeliveryForm> orderDeliveries = orderDeliveryService.importCSV(uploadFile);
			orderShippingData = new OrderShippingData(orderDeliveries);
			model.addAttribute("orderShippingData", orderShippingData);
			return "order/shipping";
		} catch (Throwable e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.getStackTrace();
			return "redirect:/orders/shipping";
		}
	}

	@PostMapping("/download")
	public String download(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try (OutputStream os = response.getOutputStream();) {
			List<Order> orders = orderService.findByStatus("ordered");
			List<OrderDelivery> orderDeliveriesData = new ArrayList<>();
			for (Order order : orders) {
				orderDeliveriesData.add(orderDeliveryService.findByOrderId(order.getId()).get());
			}
			String csvData = convertDataToCsv(orderDeliveriesData);

			String attachment = "attachment; filename=data_" + new Date().getTime() + ".csv";
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", attachment);

			os.write(csvData.getBytes());
			os.flush();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return "redirect:/orders/shippinng";
	}

	private String convertDataToCsv(List<OrderDelivery> orderDeliveries) {
		StringBuilder csvBuilder = new StringBuilder();
		csvBuilder.append("orderId,shippingCode,shippingDate,deliveryDate,deliveryTimezone\n");

		for (OrderDelivery orderDelivery : orderDeliveries) {
			csvBuilder.append(orderDelivery.getOrderId()).append(",")
					.append(orderDelivery.getShippingCode()).append(",")
					.append(orderDelivery.getShippingDate()).append(",")
					.append(orderDelivery.getDeliveryDate()).append(",")
					.append(orderDelivery.getDeliveryTimezone()).append("\n");
		}
		return csvBuilder.toString();
	}
}
