package com.sdi.service.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;

public class ClientDTO implements Serializable {

    private String name;
    private String mainContactName;
    private String mainContactEmail;
    private String mainContactPhoneNumber;
    private String address;
    private String countryname;
    private String type;
    private String sizeName;
    private Integer currentCustomersNumber;
    private BigInteger currentCardHolderNumber;
    private Integer currentBruncheNumber;
    private LocalDate createDate;
    // Dans ClientDTO.java
    private CountryDTO country;

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public ClientDTO(
        String name,
        String mainContactName,
        String mainContactEmail,
        String mainContactPhoneNumber,
        String address,
        CountryDTO country,
        String type,
        String sizeName,
        Integer currentCustomersNumber,
        BigInteger currentCardHolderNumber,
        Integer currentBruncheNumber,
        LocalDate createDate
    ) {
        this.name = name;
        this.mainContactName = mainContactName;
        this.mainContactEmail = mainContactEmail;
        this.mainContactPhoneNumber = mainContactPhoneNumber;
        this.address = address;
        this.country = country;
        this.type = type;
        this.sizeName = sizeName;
        this.currentCustomersNumber = currentCustomersNumber;
        this.currentCardHolderNumber = currentCardHolderNumber;
        this.currentBruncheNumber = currentBruncheNumber;
        this.createDate = createDate;
    }

    public ClientDTO() {}

    public ClientDTO(
        String name,
        String mainContactName,
        String mainContactEmail,
        String mainContactPhoneNumber,
        String address,
        String countryname,
        String type,
        String sizeName,
        Integer currentCustomersNumber,
        BigInteger currentCardHolderNumber,
        Integer currentBruncheNumber,
        LocalDate createDate
    ) {
        this.name = name;
        this.mainContactName = mainContactName;
        this.mainContactEmail = mainContactEmail;
        this.mainContactPhoneNumber = mainContactPhoneNumber;
        this.address = address;
        this.countryname = countryname;
        this.type = type;
        this.sizeName = sizeName;
        this.currentCustomersNumber = currentCustomersNumber;
        this.currentCardHolderNumber = currentCardHolderNumber;
        this.currentBruncheNumber = currentBruncheNumber;
        this.createDate = createDate;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainContactName() {
        return mainContactName;
    }

    public void setMainContactName(String mainContactName) {
        this.mainContactName = mainContactName;
    }

    public String getMainContactEmail() {
        return mainContactEmail;
    }

    public void setMainContactEmail(String mainContactEmail) {
        this.mainContactEmail = mainContactEmail;
    }

    public String getMainContactPhoneNumber() {
        return mainContactPhoneNumber;
    }

    public void setMainContactPhoneNumber(String mainContactPhoneNumber) {
        this.mainContactPhoneNumber = mainContactPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public Integer getCurrentCustomersNumber() {
        return currentCustomersNumber;
    }

    public void setCurrentCustomersNumber(Integer currentCustomersNumber) {
        this.currentCustomersNumber = currentCustomersNumber;
    }

    public BigInteger getCurrentCardHolderNumber() {
        return currentCardHolderNumber;
    }

    public void setCurrentCardHolderNumber(BigInteger currentCardHolderNumber) {
        this.currentCardHolderNumber = currentCardHolderNumber;
    }

    public Integer getCurrentBruncheNumber() {
        return currentBruncheNumber;
    }

    public void setCurrentBruncheNumber(Integer currentBruncheNumber) {
        this.currentBruncheNumber = currentBruncheNumber;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
