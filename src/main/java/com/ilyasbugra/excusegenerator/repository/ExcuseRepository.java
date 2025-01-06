package com.ilyasbugra.excusegenerator.repository;

import com.ilyasbugra.excusegenerator.model.Excuse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {

    List<Excuse> findByCategoryContainingIgnoreCase(String category);
}
