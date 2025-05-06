package com.sdi.repository;

import com.sdi.domain.ClientSize;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientSize entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientSizeRepository extends JpaRepository<ClientSize, Long> {}
