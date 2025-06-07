package com.sdi.repository;

import com.sdi.domain.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Country entity.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
  //  default Optional<Country> findOneWithEagerRelationships(Long id) {
    //    return this.findOneWithToOneRelationships(id);
  //  }

    default Page<Country> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select country from Country country left join fetch country.region",
        countQuery = "select count(country) from Country country"
    )
    Page<Country> findAllWithToOneRelationships(Pageable pageable);

    @Query("select country from Country country left join fetch country.region")
    List<Country> findAllWithToOneRelationships();

    @Query("select country from Country country left join fetch country.region where country.id =:id")
    Optional<Country> findOneWithToOneRelationships(@Param("id") Long id);




    @Query(value = "SELECT c.id, c.countryname, c.countrycode, c.country_flagcode, c.country_flag, c.notes, c.crea_date, c.update_date, c.region_id, " +
        "r.id as region_id, r.name as region_name " +
        "FROM country c LEFT JOIN region r ON c.region_id = r.id", nativeQuery = true)
    List<Object[]> findAllWithEagerRelationships();

    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.region LEFT JOIN FETCH c.clients")
    List<Country> findAllWithEagerRelationshipsAndRegion();

    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.region LEFT JOIN FETCH c.clients WHERE c.id = ?1")
    Optional<Country> findOneWithEagerRelationships(Long id);
}
