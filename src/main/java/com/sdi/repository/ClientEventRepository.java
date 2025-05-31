package com.sdi.repository;

import com.sdi.domain.ClientEvent;
import java.util.List;
import java.util.Optional;

import com.sdi.service.dto.ClientEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientEvent entity.
 */
@Repository
public interface ClientEventRepository extends JpaRepository<ClientEvent, Long> {
    default Optional<ClientEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClientEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClientEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select clientEvent from ClientEvent clientEvent left join fetch clientEvent.client left join fetch clientEvent.clientEventType",
        countQuery = "select count(clientEvent) from ClientEvent clientEvent"
    )
    Page<ClientEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select clientEvent from ClientEvent clientEvent left join fetch clientEvent.client left join fetch clientEvent.clientEventType")
    List<ClientEvent> findAllWithToOneRelationships();

    @Query(
        "select clientEvent from ClientEvent clientEvent left join fetch clientEvent.client left join fetch clientEvent.clientEventType where clientEvent.id =:id"
    )
    Optional<ClientEvent> findOneWithToOneRelationships(@Param("id") Long id);


    @Query("SELECT new com.sdi.service.dto.ClientEventDTO(" +
        "ce.event, ce.eventDate, ce.description) " +
        "FROM ClientEvent ce " +
        "WHERE ce.client.id = :clientId")
    List<ClientEventDTO> findEventsByClientId(@Param("clientId") Long clientId);
}
