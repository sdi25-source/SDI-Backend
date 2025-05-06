package com.sdi.repository;

import com.sdi.domain.CustomisationLevel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomisationLevel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomisationLevelRepository extends JpaRepository<CustomisationLevel, Long> {}
