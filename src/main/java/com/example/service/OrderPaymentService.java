package com.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.OrderPayment;
import com.example.repository.OrderPaymentRepository;

@Service
@Transactional(readOnly = true)
public class OrderPaymentService {

	@Autowired
	private OrderPaymentRepository orderPaymentRepository;

	@Transactional(readOnly = false)
	public OrderPayment save(OrderPayment orderPayment) {
		return orderPaymentRepository.save(orderPayment);
	}

	@Transactional(readOnly = false)
	public List<String[]> importCSV(MultipartFile file) throws RuntimeException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String line = br.readLine();
			List<String[]> orderPaymentsData = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				final String[] split = line.split(",");
				orderPaymentsData.add(split);
			}
			return orderPaymentsData;
		} catch (IOException e) {
			throw new RuntimeException("ファイルが読み込めません", e);
		}
	}
}
