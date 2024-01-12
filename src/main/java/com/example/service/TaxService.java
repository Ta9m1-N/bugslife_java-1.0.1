package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.repository.TaxRepoistory;
import com.example.model.Tax;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaxService {

	@Autowired
	private TaxRepoistory taxRepoistory;

	public List<Tax> findAll() {
		return taxRepoistory.findAll();
	}

	public Optional<Tax> findOne(Long id) {
		return taxRepoistory.findById(id);
	}

	@Transactional(readOnly = false)
	public Tax save(Tax entity) {
		return taxRepoistory.save(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Tax entity) {
		taxRepoistory.delete(entity);
	}
}
