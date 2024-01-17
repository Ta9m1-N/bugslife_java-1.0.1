package com.example.form;

import java.util.List;

import com.example.model.OrderDelivery;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderShippingData {

	private OrderDeliveryForm[] orderShippingList;

	public OrderShippingData(List<OrderDeliveryForm> orderDeliveriForms) {
		orderShippingList = orderDeliveriForms.toArray(new OrderDeliveryForm[orderDeliveriForms.size()]);
	}
}
