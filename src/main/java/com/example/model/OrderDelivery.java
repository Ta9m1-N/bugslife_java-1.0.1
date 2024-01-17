package com.example.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.form.OrderDeliveryForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_deliveries")
public class OrderDelivery extends TimeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "shipping_code")
	private String shippingCode;

	@Column(name = "shipping_date")
	private LocalDate shippingDate;

	@Column(name = "delivery_date")
	private LocalDate deliveryDate;

	@Column(name = "delivery_timezone")
	private String deliveryTimezone;

	@Column(name = "order_id", insertable = false, updatable = false)
	private Long orderId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public void set(OrderDeliveryForm orderDeliveryForm) {
		shippingCode = orderDeliveryForm.getShippingCode();
		shippingDate = orderDeliveryForm.getShippingDate();
		deliveryDate = orderDeliveryForm.getDeliveryDate();
		deliveryTimezone = orderDeliveryForm.getDeliveryTimezone();
	}
}
