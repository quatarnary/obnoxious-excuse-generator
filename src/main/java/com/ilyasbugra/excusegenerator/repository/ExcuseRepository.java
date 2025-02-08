package com.ilyasbugra.excusegenerator.repository;

import com.ilyasbugra.excusegenerator.model.Excuse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {

    default @NonNull Page<Excuse> findAll(@NonNull Pageable pageable) {
        return findAllAndApprovedByIdIsNotNull(pageable);
    }

    Page<Excuse> findAllAndApprovedByIdIsNotNull(Pageable pageable);

    default @NonNull Optional<Excuse> findById(@NonNull Long id) {
        return findByIdAndApprovedByIdIsNotNull(id);
    }

    Optional<Excuse> findByIdAndApprovedByIdIsNotNull(Long id);

    default Page<Excuse> findByCategoryStartingWithIgnoreCase(String category, Pageable pageable) {
        return findByCategoryStartingWithIgnoreCaseAndApprovedByIsNotNull(category, pageable);
    }

    Page<Excuse> findByCategoryStartingWithIgnoreCaseAndApprovedByIsNotNull(String category, Pageable pageable);

    @Query(value = "SELECT * FROM excuses WHERE approved_by IS NOT NULL ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Excuse findRandomExcuse();
}
