package com.sdi.repository;

import com.sdi.domain.ComponentType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ComponentType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentType, Long> {}
