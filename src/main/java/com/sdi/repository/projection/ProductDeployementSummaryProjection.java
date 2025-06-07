package com.sdi.repository.projection;

import java.time.LocalDate;

public interface ProductDeployementSummaryProjection {
    String getRefContract();
    String getProduct();
    String getVersion();
    String getDeployementType();
    LocalDate getStartDeployementDate();
}
