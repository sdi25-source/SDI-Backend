package com.sdi.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.sdi.domain.Country} entity.
 */
public class CountryDTO implements Serializable {

    private Long id;

    private String countryname;

    private String countrycode;

    private String countryFlagcode;

    private String countryFlag;

    private String notes;

    private LocalDate creaDate;

    private LocalDate updateDate;

    // Constructor for mapping from Country entity
    public CountryDTO() {
    }

    public CountryDTO(Long id, String countryname, String countrycode, String countryFlagcode,
                      String countryFlag, String notes, LocalDate creaDate, LocalDate updateDate) {
        this.id = id;
        this.countryname = countryname;
        this.countrycode = countrycode;
        this.countryFlagcode = countryFlagcode;
        this.countryFlag = countryFlag;
        this.notes = notes;
        this.creaDate = creaDate;
        this.updateDate = updateDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCountryFlagcode() {
        return countryFlagcode;
    }

    public void setCountryFlagcode(String countryFlagcode) {
        this.countryFlagcode = countryFlagcode;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreaDate() {
        return creaDate;
    }

    public void setCreaDate(LocalDate creaDate) {
        this.creaDate = creaDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CountryDTO countryDTO = (CountryDTO) o;
        return Objects.equals(id, countryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CountryDTO{" +
            "id=" + id +
            ", countryname='" + countryname + '\'' +
            ", countrycode='" + countrycode + '\'' +
            ", countryFlagcode='" + countryFlagcode + '\'' +
            ", countryFlag='" + countryFlag + '\'' +
            ", notes='" + notes + '\'' +
            ", creaDate=" + creaDate +
            ", updateDate=" + updateDate +
            '}';
    }
}
