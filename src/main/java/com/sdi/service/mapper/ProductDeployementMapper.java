package com.sdi.service.mapper;

import com.sdi.repository.projection.ProductDeployementSummaryProjection;
import com.sdi.service.dto.ProductDeployementSummaryDTO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDeployementMapper {
    ProductDeployementSummaryDTO toDto(ProductDeployementSummaryProjection projection);
    List<ProductDeployementSummaryDTO> toDto(List<ProductDeployementSummaryProjection> projections);
}
