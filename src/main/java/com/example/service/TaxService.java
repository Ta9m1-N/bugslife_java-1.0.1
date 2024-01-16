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

	public Long findId(Integer rate, Boolean included, Integer rounding) {
		List<Tax> all = this.findAll();
		for (Tax tax : all) {
			if (tax.getRate().equals(rate) && tax.getIncluded().equals(included)
					&& tax.getRounding().equals(rounding)) {
				return tax.getId();
			}
		}
		return null;
	}

	@Transactional(readOnly = false)
	public Tax save(Tax entity) {
		return taxRepoistory.save(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Tax entity) {
		taxRepoistory.delete(entity);
	}

	@Transactional(readOnly = false)
	public void updateInUse(Long taxId, boolean isIncrement) {
		Tax tax = this.findOne(taxId).get();
		Integer nowInUse = tax.getInUse();
		Integer nextInUse = (isIncrement) ? nowInUse + 1 : nowInUse - 1;
		tax.setInUse(nextInUse);
		this.save(tax);
	}
}
