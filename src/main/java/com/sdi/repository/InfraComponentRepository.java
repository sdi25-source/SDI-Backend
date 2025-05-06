package com.sdi.repository;

import com.sdi.domain.InfraComponent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InfraComponent entity.
 */
@Repository
public interface InfraComponentRepository extends JpaRepository<InfraComponent, Long> {
    default Optional<InfraComponent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InfraComponent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InfraComponent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select infraComponent from InfraComponent infraComponent left join fetch infraComponent.componentType",
        countQuery = "select count(infraComponent) from InfraComponent infraComponent"
    )
    Page<InfraComponent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select infraComponent from InfraComponent infraComponent left join fetch infraComponent.componentType")
    List<InfraComponent> findAllWithToOneRelationships();

    @Query(
        "select infraComponent from InfraComponent infraComponent left join fetch infraComponent.componentType where infraComponent.id =:id"
    )
    Optional<InfraComponent> findOneWithToOneRelationships(@Param("id") Long id);
}
