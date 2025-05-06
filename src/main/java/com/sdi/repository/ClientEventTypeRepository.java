package com.sdi.repository;

import com.sdi.domain.ClientEventType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientEventType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientEventTypeRepository extends JpaRepository<ClientEventType, Long> {}
