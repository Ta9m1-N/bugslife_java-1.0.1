package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.model.OrderDelivery;

public interface OrderDeliveryRepository
		extends JpaRepository<OrderDelivery, Long>, JpaSpecificationExecutor<OrderDelivery> {
	public Optional<OrderDelivery> findByOrderId(Long orderId);
}
