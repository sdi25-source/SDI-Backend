package com.sdi.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.sdi.domain.Region} entity.
 */
public class RegionDTO implements Serializable {

    private Long id;

    private String name;

    private String code;

    private LocalDate creaDate;

    private LocalDate updateDate;

    private String notes;

    // Default constructor
    public RegionDTO() {
    }

    // Constructor for mapping from Region entity
    public RegionDTO(Long id, String name, String code, LocalDate creaDate, LocalDate updateDate, String notes) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.creaDate = creaDate;
        this.updateDate = updateDate;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegionDTO regionDTO = (RegionDTO) o;
        return Objects.equals(id, regionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RegionDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", creaDate=" + creaDate +
            ", updateDate=" + updateDate +
            ", notes='" + notes + '\'' +
            '}';
    }
}
