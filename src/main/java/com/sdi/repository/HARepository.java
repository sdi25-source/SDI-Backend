package com.sdi.repository;

import com.sdi.domain.HA;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HA entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HARepository extends JpaRepository<HA, Long> {}
