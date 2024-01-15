package com.example.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "taxes")
public class Tax extends TimeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "rate", nullable = false)
	private Integer rate;

	@Column(name = "included", nullable = false)
	private Boolean included;

	@Column(name = "rounding", nullable = false)
	private Integer rounding;

	@Column(name = "in_use", nullable = false)
	private Integer inUse;
}
