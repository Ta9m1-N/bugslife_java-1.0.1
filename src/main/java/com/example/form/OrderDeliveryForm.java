package com.example.form;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeliveryForm {

	private String shippingCode;

	private LocalDate shippingDate;

	private LocalDate deliveryDate;

	private String deliveryTimezone;

	private Long orderId;

	private boolean checked;

	public boolean getChecked() {
		return checked;
	}

	private boolean uploadStatus;
}
