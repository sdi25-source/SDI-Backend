package com.sdi.repository;

import com.sdi.domain.ClientType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {}
