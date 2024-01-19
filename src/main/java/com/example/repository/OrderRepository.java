package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByStatus(String status);

	List<Order> findByPaymentStatusNot(String paymentStatus);

	@Query(value = "SELECT o.id, o.customer_id, o.discount, o.shipping, o.tax, o.total, o.grand_total, o.status, o.payment_method, o.payment_status, o.paid, o.note, op.name, op.quantity FROM orders o INNER JOIN order_products op ON o.id = op.order_id", nativeQuery = true)
	List<Object[]> findIndex();
}
