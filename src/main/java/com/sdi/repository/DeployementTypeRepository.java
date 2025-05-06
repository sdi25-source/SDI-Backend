package com.sdi.repository;

import com.sdi.domain.DeployementType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DeployementType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeployementTypeRepository extends JpaRepository<DeployementType, Long> {}
