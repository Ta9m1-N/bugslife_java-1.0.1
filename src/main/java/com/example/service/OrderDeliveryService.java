package com.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.form.OrderDeliveryForm;
import com.example.model.Order;
import com.example.model.OrderDelivery;
import com.example.repository.OrderDeliveryRepository;

@Service
@Transactional(readOnly = true)
public class OrderDeliveryService {

	@Autowired
	private OrderDeliveryRepository orderDeliveryRepository;

	public Optional<OrderDelivery> findById(Long id) {
		return orderDeliveryRepository.findById(id);
	}

	public Optional<OrderDelivery> findByOrderId(Long orderId) {
		return orderDeliveryRepository.findByOrderId(orderId);
	}

	@Transactional(readOnly = false)
	public OrderDelivery save(OrderDelivery orderDelivery) {
		return orderDeliveryRepository.save(orderDelivery);
	}

	@Transactional(readOnly = false)
	public List<OrderDeliveryForm> importCSV(MultipartFile file) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String line = br.readLine();
			List<OrderDeliveryForm> orderDeliveries = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				final OrderDeliveryForm orderDeliveryForm = new OrderDeliveryForm();
				try {
					final String[] split = line.split(",");
					orderDeliveryForm.setOrderId(Long.parseLong(split[0]));
					orderDeliveryForm.setShippingCode(split[1]);
					orderDeliveryForm
							.setShippingDate(
									LocalDate.parse(split[2], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					orderDeliveryForm
							.setDeliveryDate(
									LocalDate.parse(split[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					orderDeliveryForm.setDeliveryTimezone(split[4]);
					orderDeliveryForm.setUploadStatus(true);
				} catch (Exception e) {
					orderDeliveryForm.setUploadStatus(false);
				}
				orderDeliveries.add(orderDeliveryForm);
			}
			return orderDeliveries;
		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}
	}

}
