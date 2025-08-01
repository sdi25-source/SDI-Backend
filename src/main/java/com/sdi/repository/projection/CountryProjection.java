package com.sdi.repository.projection;

public interface CountryProjection {
    Long getId();
    String getCountryname();
    String getCountrycode();
    String getCountryFlagcode();
    String getCountryFlag();
    Long getRegionId();
    String getRegionName();
}
