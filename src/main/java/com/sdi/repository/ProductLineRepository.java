package com.sdi.repository;

import com.sdi.domain.ProductLine;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductLine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {}
