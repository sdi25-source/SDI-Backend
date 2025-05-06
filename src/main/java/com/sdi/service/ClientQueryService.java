package com.sdi.service;

import com.sdi.domain.*; // for static metamodels
import com.sdi.domain.Client;
import com.sdi.repository.ClientRepository;
import com.sdi.service.criteria.ClientCriteria;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Client} entities in the database.
 * The main input is a {@link ClientCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Client} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientQueryService extends QueryService<Client> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientQueryService.class);

    private final ClientRepository clientRepository;

    public ClientQueryService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Return a {@link List} of {@link Client} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Client> findByCriteria(ClientCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Client> specification = createSpecification(criteria);
        return clientRepository.findAll(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClientCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Client> specification = createSpecification(criteria);
        return clientRepository.count(specification);
    }

    /**
     * Function to convert {@link ClientCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Client> createSpecification(ClientCriteria criteria) {
        Specification<Client> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Client_.id));
            }
            if (criteria.getClientLogo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClientLogo(), Client_.clientLogo));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Client_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Client_.code));
            }
            if (criteria.getMainContactName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMainContactName(), Client_.mainContactName));
            }
            if (criteria.getMainContactEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMainContactEmail(), Client_.mainContactEmail));
            }
            if (criteria.getCurrentCardHolderNumber() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getCurrentCardHolderNumber(), Client_.currentCardHolderNumber)
                );
            }
            if (criteria.getCurrentBruncheNumber() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getCurrentBruncheNumber(), Client_.currentBruncheNumber)
                );
            }
            if (criteria.getCurrentCustomersNumber() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getCurrentCustomersNumber(), Client_.currentCustomersNumber)
                );
            }
            if (criteria.getMainContactPhoneNumber() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getMainContactPhoneNumber(), Client_.mainContactPhoneNumber)
                );
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Client_.url));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Client_.address));
            }
            if (criteria.getCreateDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateDate(), Client_.createDate));
            }
            if (criteria.getUpdateDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdateDate(), Client_.updateDate));
            }
            if (criteria.getProductDeployementId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProductDeployementId(), root ->
                        root.join(Client_.productDeployements, JoinType.LEFT).get(ProductDeployement_.id)
                    )
                );
            }
            if (criteria.getCountryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCountryId(), root -> root.join(Client_.country, JoinType.LEFT).get(Country_.id))
                );
            }
            if (criteria.getSizeId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSizeId(), root -> root.join(Client_.size, JoinType.LEFT).get(ClientSize_.id))
                );
            }
            if (criteria.getClientTypeId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getClientTypeId(), root -> root.join(Client_.clientType, JoinType.LEFT).get(ClientType_.id))
                );
            }
            if (criteria.getCertifId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCertifId(), root ->
                        root.join(Client_.certifs, JoinType.LEFT).get(ClientCertification_.id)
                    )
                );
            }
        }
        return specification;
    }
}
