package com.sdi.repository;

import com.sdi.domain.ClientCertification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientCertification entity.
 */
@Repository
public interface ClientCertificationRepository extends JpaRepository<ClientCertification, Long> {
    default Optional<ClientCertification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClientCertification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClientCertification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select clientCertification from ClientCertification clientCertification left join fetch clientCertification.client left join fetch clientCertification.certif",
        countQuery = "select count(clientCertification) from ClientCertification clientCertification"
    )
    Page<ClientCertification> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select clientCertification from ClientCertification clientCertification left join fetch clientCertification.client left join fetch clientCertification.certif"
    )
    List<ClientCertification> findAllWithToOneRelationships();

    @Query(
        "select clientCertification from ClientCertification clientCertification left join fetch clientCertification.client left join fetch clientCertification.certif where clientCertification.id =:id"
    )
    Optional<ClientCertification> findOneWithToOneRelationships(@Param("id") Long id);
}
