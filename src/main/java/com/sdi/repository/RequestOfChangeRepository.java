package com.sdi.repository;

import com.sdi.domain.RequestOfChange;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RequestOfChange entity.
 *
 * When extending this class, extend RequestOfChangeRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface RequestOfChangeRepository extends RequestOfChangeRepositoryWithBagRelationships, JpaRepository<RequestOfChange, Long> {
    default Optional<RequestOfChange> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<RequestOfChange> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<RequestOfChange> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select requestOfChange from RequestOfChange requestOfChange left join fetch requestOfChange.productVersion left join fetch requestOfChange.client",
        countQuery = "select count(requestOfChange) from RequestOfChange requestOfChange"
    )
    Page<RequestOfChange> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select requestOfChange from RequestOfChange requestOfChange left join fetch requestOfChange.productVersion left join fetch requestOfChange.client"
    )
    List<RequestOfChange> findAllWithToOneRelationships();

    @Query(
        "select requestOfChange from RequestOfChange requestOfChange left join fetch requestOfChange.productVersion left join fetch requestOfChange.client where requestOfChange.id =:id"
    )
    Optional<RequestOfChange> findOneWithToOneRelationships(@Param("id") Long id);
}
