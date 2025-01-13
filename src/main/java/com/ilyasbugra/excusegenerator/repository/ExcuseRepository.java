package com.ilyasbugra.excusegenerator.repository;

import com.ilyasbugra.excusegenerator.model.Excuse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {

    Page<Excuse> findByCategoryStartingWithIgnoreCase(String category, Pageable pageable);
}
