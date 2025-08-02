package com.sdi.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.sdi.domain.Client} entity. This class is used
 * in {@link com.sdi.web.rest.ClientResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter code;

    private StringFilter mainContactName;

    private StringFilter mainContactEmail;

    private BigIntegerFilter currentCardHolderNumber; // ✅ corrigé

    private IntegerFilter currentBruncheNumber;

    private IntegerFilter currentCustomersNumber;

    private StringFilter mainContactPhoneNumber;

    private StringFilter url;

    private StringFilter address;

    private LocalDateFilter createDate;

    private LocalDateFilter updateDate;

    private LongFilter productDeployementId;

    private LongFilter sizeId;

    private LongFilter clientTypeId;

    private LongFilter countryId;

    private LongFilter certifId;

    private Boolean distinct;

    public ClientCriteria() {}

    public ClientCriteria(ClientCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.mainContactName = other.optionalMainContactName().map(StringFilter::copy).orElse(null);
        this.mainContactEmail = other.optionalMainContactEmail().map(StringFilter::copy).orElse(null);
        this.currentCardHolderNumber = other.optionalCurrentCardHolderNumber().map(BigIntegerFilter::copy).orElse(null); // ✅
        this.currentBruncheNumber = other.optionalCurrentBruncheNumber().map(IntegerFilter::copy).orElse(null);
        this.currentCustomersNumber = other.optionalCurrentCustomersNumber().map(IntegerFilter::copy).orElse(null);
        this.mainContactPhoneNumber = other.optionalMainContactPhoneNumber().map(StringFilter::copy).orElse(null);
        this.url = other.optionalUrl().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.createDate = other.optionalCreateDate().map(LocalDateFilter::copy).orElse(null);
        this.updateDate = other.optionalUpdateDate().map(LocalDateFilter::copy).orElse(null);
        this.productDeployementId = other.optionalProductDeployementId().map(LongFilter::copy).orElse(null);
        this.sizeId = other.optionalSizeId().map(LongFilter::copy).orElse(null);
        this.clientTypeId = other.optionalClientTypeId().map(LongFilter::copy).orElse(null);
        this.countryId = other.optionalCountryId().map(LongFilter::copy).orElse(null);
        this.certifId = other.optionalCertifId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ClientCriteria copy() {
        return new ClientCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getMainContactName() {
        return mainContactName;
    }

    public Optional<StringFilter> optionalMainContactName() {
        return Optional.ofNullable(mainContactName);
    }

    public StringFilter mainContactName() {
        if (mainContactName == null) {
            setMainContactName(new StringFilter());
        }
        return mainContactName;
    }

    public void setMainContactName(StringFilter mainContactName) {
        this.mainContactName = mainContactName;
    }

    public StringFilter getMainContactEmail() {
        return mainContactEmail;
    }

    public Optional<StringFilter> optionalMainContactEmail() {
        return Optional.ofNullable(mainContactEmail);
    }

    public StringFilter mainContactEmail() {
        if (mainContactEmail == null) {
            setMainContactEmail(new StringFilter());
        }
        return mainContactEmail;
    }

    public void setMainContactEmail(StringFilter mainContactEmail) {
        this.mainContactEmail = mainContactEmail;
    }

    // ✅ Partie corrigée pour BigIntegerFilter
    public BigIntegerFilter getCurrentCardHolderNumber() {
        return currentCardHolderNumber;
    }

    public Optional<BigIntegerFilter> optionalCurrentCardHolderNumber() {
        return Optional.ofNullable(currentCardHolderNumber);
    }

    public BigIntegerFilter currentCardHolderNumber() {
        if (currentCardHolderNumber == null) {
            setCurrentCardHolderNumber(new BigIntegerFilter());
        }
        return currentCardHolderNumber;
    }

    public void setCurrentCardHolderNumber(BigIntegerFilter currentCardHolderNumber) {
        this.currentCardHolderNumber = currentCardHolderNumber;
    }

    public IntegerFilter getCurrentBruncheNumber() {
        return currentBruncheNumber;
    }

    public Optional<IntegerFilter> optionalCurrentBruncheNumber() {
        return Optional.ofNullable(currentBruncheNumber);
    }

    public IntegerFilter currentBruncheNumber() {
        if (currentBruncheNumber == null) {
            setCurrentBruncheNumber(new IntegerFilter());
        }
        return currentBruncheNumber;
    }

    public void setCurrentBruncheNumber(IntegerFilter currentBruncheNumber) {
        this.currentBruncheNumber = currentBruncheNumber;
    }

    public IntegerFilter getCurrentCustomersNumber() {
        return currentCustomersNumber;
    }

    public Optional<IntegerFilter> optionalCurrentCustomersNumber() {
        return Optional.ofNullable(currentCustomersNumber);
    }

    public IntegerFilter currentCustomersNumber() {
        if (currentCustomersNumber == null) {
            setCurrentCustomersNumber(new IntegerFilter());
        }
        return currentCustomersNumber;
    }

    public void setCurrentCustomersNumber(IntegerFilter currentCustomersNumber) {
        this.currentCustomersNumber = currentCustomersNumber;
    }

    public StringFilter getMainContactPhoneNumber() {
        return mainContactPhoneNumber;
    }

    public Optional<StringFilter> optionalMainContactPhoneNumber() {
        return Optional.ofNullable(mainContactPhoneNumber);
    }

    public StringFilter mainContactPhoneNumber() {
        if (mainContactPhoneNumber == null) {
            setMainContactPhoneNumber(new StringFilter());
        }
        return mainContactPhoneNumber;
    }

    public void setMainContactPhoneNumber(StringFilter mainContactPhoneNumber) {
        this.mainContactPhoneNumber = mainContactPhoneNumber;
    }

    public StringFilter getUrl() {
        return url;
    }

    public Optional<StringFilter> optionalUrl() {
        return Optional.ofNullable(url);
    }

    public StringFilter url() {
        if (url == null) {
            setUrl(new StringFilter());
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public LocalDateFilter getCreateDate() {
        return createDate;
    }

    public Optional<LocalDateFilter> optionalCreateDate() {
        return Optional.ofNullable(createDate);
    }

    public LocalDateFilter createDate() {
        if (createDate == null) {
            setCreateDate(new LocalDateFilter());
        }
        return createDate;
    }

    public void setCreateDate(LocalDateFilter createDate) {
        this.createDate = createDate;
    }

    public LocalDateFilter getUpdateDate() {
        return updateDate;
    }

    public Optional<LocalDateFilter> optionalUpdateDate() {
        return Optional.ofNullable(updateDate);
    }

    public LocalDateFilter updateDate() {
        if (updateDate == null) {
            setUpdateDate(new LocalDateFilter());
        }
        return updateDate;
    }

    public void setUpdateDate(LocalDateFilter updateDate) {
        this.updateDate = updateDate;
    }

    public LongFilter getProductDeployementId() {
        return productDeployementId;
    }

    public Optional<LongFilter> optionalProductDeployementId() {
        return Optional.ofNullable(productDeployementId);
    }

    public LongFilter productDeployementId() {
        if (productDeployementId == null) {
            setProductDeployementId(new LongFilter());
        }
        return productDeployementId;
    }

    public void setProductDeployementId(LongFilter productDeployementId) {
        this.productDeployementId = productDeployementId;
    }

    public LongFilter getSizeId() {
        return sizeId;
    }

    public Optional<LongFilter> optionalSizeId() {
        return Optional.ofNullable(sizeId);
    }

    public LongFilter sizeId() {
        if (sizeId == null) {
            setSizeId(new LongFilter());
        }
        return sizeId;
    }

    public void setSizeId(LongFilter sizeId) {
        this.sizeId = sizeId;
    }

    public LongFilter getClientTypeId() {
        return clientTypeId;
    }

    public Optional<LongFilter> optionalClientTypeId() {
        return Optional.ofNullable(clientTypeId);
    }

    public LongFilter clientTypeId() {
        if (clientTypeId == null) {
            setClientTypeId(new LongFilter());
        }
        return clientTypeId;
    }

    public void setClientTypeId(LongFilter clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public LongFilter getCountryId() {
        return countryId;
    }

    public Optional<LongFilter> optionalCountryId() {
        return Optional.ofNullable(countryId);
    }

    public LongFilter countryId() {
        if (countryId == null) {
            setCountryId(new LongFilter());
        }
        return countryId;
    }

    public void setCountryId(LongFilter countryId) {
        this.countryId = countryId;
    }

    public LongFilter getCertifId() {
        return certifId;
    }

    public Optional<LongFilter> optionalCertifId() {
        return Optional.ofNullable(certifId);
    }

    public LongFilter certifId() {
        if (certifId == null) {
            setCertifId(new LongFilter());
        }
        return certifId;
    }

    public void setCertifId(LongFilter certifId) {
        this.certifId = certifId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClientCriteria that = (ClientCriteria) o;
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(code, that.code)
            && Objects.equals(mainContactName, that.mainContactName)
            && Objects.equals(mainContactEmail, that.mainContactEmail)
            && Objects.equals(currentCardHolderNumber, that.currentCardHolderNumber)
            && Objects.equals(currentBruncheNumber, that.currentBruncheNumber)
            && Objects.equals(currentCustomersNumber, that.currentCustomersNumber)
            && Objects.equals(mainContactPhoneNumber, that.mainContactPhoneNumber)
            && Objects.equals(url, that.url)
            && Objects.equals(address, that.address)
            && Objects.equals(createDate, that.createDate)
            && Objects.equals(updateDate, that.updateDate)
            && Objects.equals(productDeployementId, that.productDeployementId)
            && Objects.equals(sizeId, that.sizeId)
            && Objects.equals(clientTypeId, that.clientTypeId)
            && Objects.equals(countryId, that.countryId)
            && Objects.equals(certifId, that.certifId)
            && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id, name, code, mainContactName, mainContactEmail,
            currentCardHolderNumber, currentBruncheNumber, currentCustomersNumber,
            mainContactPhoneNumber, url, address, createDate, updateDate,
            productDeployementId, sizeId, clientTypeId, countryId, certifId, distinct
        );
    }

    @Override
    public String toString() {
        return "ClientCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalMainContactName().map(f -> "mainContactName=" + f + ", ").orElse("") +
            optionalMainContactEmail().map(f -> "mainContactEmail=" + f + ", ").orElse("") +
            optionalCurrentCardHolderNumber().map(f -> "currentCardHolderNumber=" + f + ", ").orElse("") +
            optionalCurrentBruncheNumber().map(f -> "currentBruncheNumber=" + f + ", ").orElse("") +
            optionalCurrentCustomersNumber().map(f -> "currentCustomersNumber=" + f + ", ").orElse("") +
            optionalMainContactPhoneNumber().map(f -> "mainContactPhoneNumber=" + f + ", ").orElse("") +
            optionalUrl().map(f -> "url=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalCreateDate().map(f -> "createDate=" + f + ", ").orElse("") +
            optionalUpdateDate().map(f -> "updateDate=" + f + ", ").orElse("") +
            optionalProductDeployementId().map(f -> "productDeployementId=" + f + ", ").orElse("") +
            optionalSizeId().map(f -> "sizeId=" + f + ", ").orElse("") +
            optionalClientTypeId().map(f -> "clientTypeId=" + f + ", ").orElse("") +
            optionalCountryId().map(f -> "countryId=" + f + ", ").orElse("") +
            optionalCertifId().map(f -> "certifId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}";
    }
}
