package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.model.Tax;

public interface TaxRepoistory extends JpaRepository<Tax, Long>, JpaSpecificationExecutor<Tax> {}
