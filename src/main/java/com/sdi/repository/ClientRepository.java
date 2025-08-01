package com.sdi.repository;

import com.sdi.domain.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    default Optional<Client> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Client> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Client> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select client from Client client left join fetch client.size left join fetch client.clientType left join fetch client.country",
        countQuery = "select count(client) from Client client"
    )
    Page<Client> findAllWithToOneRelationships(Pageable pageable);

    @Query("select client from Client client left join fetch client.size left join fetch client.clientType left join fetch client.country")
    List<Client> findAllWithToOneRelationships();

    @Query(
        "select client from Client client left join fetch client.size left join fetch client.clientType left join fetch client.country where client.id =:id"
    )
    Optional<Client> findOneWithToOneRelationships(@Param("id") Long id);


    @Query("SELECT c FROM Client c " +
        "LEFT JOIN FETCH c.clientType " +
        "LEFT JOIN FETCH c.size " +
        "WHERE c.id = :id")
    Optional<Client> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.clientType")
    List<Client> findAllWithClientType();
}
