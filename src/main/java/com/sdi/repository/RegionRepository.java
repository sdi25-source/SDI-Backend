package com.sdi.repository;

import com.sdi.domain.Region;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    @Query("SELECT r FROM Region r")
    List<Region> findAllWithEagerNotes();
}
