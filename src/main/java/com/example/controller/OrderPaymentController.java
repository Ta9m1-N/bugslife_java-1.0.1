package com.example.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.constants.Message;
import com.example.form.OrderForm;
import com.example.form.OrderPaymentsData;
import com.example.model.Order;
import com.example.model.OrderPayment;
import com.example.service.OrderPaymentService;
import com.example.service.OrderService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/orders/paying")
public class OrderPaymentController {

	@Autowired
	private OrderPaymentService orderPaymentService;

	@Autowired
	private OrderService orderService;

	@GetMapping
	public String index(Model model) {
		List<Order> orders = orderService.findByPaymentStatusNot("paid");
		model.addAttribute("orders", orders);
		return "order/paying";
	}

	@PutMapping
	public String update(@ModelAttribute("orderPaymentsData") OrderPaymentsData orderPaymentsData,
			RedirectAttributes redirectAttributes) {
		try {
			OrderPayment[] orderPayments = orderPaymentsData.getOrderPayments();
			for (OrderPayment orderPayment : orderPayments) {
				Order order = orderService.findOne(orderPayment.getOrderId()).get();
				if (orderPayment.getMethod().equals("credit_card")) {
					String orderStatus = order.getStatus();
					if (orderStatus.equals("ordered")) {
						orderPayment.setType("credit");
					} else if (orderStatus.equals("shipped")) {
						orderPayment.setType("complete");
					} else {
						continue;
					}
				} else if (orderPayment.getMethod().equals("deffered_payment")) {
					if (!orderPayment.getType().equals("pronpt") && !orderPayment.getType().equals("complete")) {
						continue;
					}
				}
				OrderForm.CreatePayment createPayment = orderPayment.changeClass(new OrderForm.CreatePayment());
				orderService.createPayment(createPayment);
				if (order.getStatus().equals("shipped") && order.getPaymentStatus().equals("paid")) {
					order.setStatus("completed");
					orderService.save(order);
				}
			}
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_UPDATE);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.getStackTrace();
		}
		return "redirect:/orders/paying";
	}

	@PostMapping("/download")
	public String download(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try (OutputStream os = response.getOutputStream();) {
			String csvData = "orderId,type,paid\n";

			String attachment = "attachment; filename=data_" + new Date().getTime() + ".csv";
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", attachment);

			os.write(csvData.getBytes());
			os.flush();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return "redirect:/orders/paying";
	}

	@PostMapping
	public String uploadFile(Model model, @RequestParam("file") MultipartFile uploadFile,
			RedirectAttributes redirectAttributes) {
		if (uploadFile.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "ファイルを選択してください");
		}
		if (!"text/csv".equals(uploadFile.getContentType())) {
			redirectAttributes.addFlashAttribute("error", "CSVファイルを選択してください");
		}
		List<String[]> orderPaymentsList = new ArrayList<>();
		try {
			orderPaymentsList = orderPaymentService.importCSV(uploadFile);
		} catch (Exception e) {
			redirectAttributes.addAttribute("error", Message.MSG_ERROR);
			e.getStackTrace();
			return "redirect:/orders/paying";
		}
		OrderPayment[] orderPayments = new OrderPayment[orderPaymentsList.size()];
		for (int i = 0; i < orderPaymentsList.size(); i++) {
			String[] split = orderPaymentsList.get(i);
			if (split.length != 3) {
				redirectAttributes.addAttribute("error", "カラム数が異なります");
				return "redirect:/orders/paying";
			}
			Long orderId;
			String type;
			Double paid;
			try {
				orderId = Long.parseLong(split[0]);
				type = split[1];
				paid = Double.parseDouble(split[2]);
			} catch (Exception e) {
				redirectAttributes.addAttribute("error", "想定される型ではありません");
				return "redirect:/orders/paying";
			}
			OrderPayment orderPayment = new OrderPayment();
			orderPayment.setOrderId(orderId);
			orderPayment.setType(type);
			orderPayment.setPaid(paid);
			Order order = orderService.findOne(orderId).get();
			orderPayment.setMethod(order.getPaymentMethod());
			orderPayment.setPaidAt(Timestamp.valueOf(LocalDateTime.now()));

			orderPayments[i] = orderPayment;
		}
		List<Order> orders = orderService.findByPaymentStatusNot("paid");
		OrderPaymentsData orderPaymentsData = new OrderPaymentsData();
		orderPaymentsData.setOrderPayments(orderPayments);
		model.addAttribute("orders", orders);
		model.addAttribute("orderPaymentsData", orderPaymentsData);
		return "order/paying";
	}
}
