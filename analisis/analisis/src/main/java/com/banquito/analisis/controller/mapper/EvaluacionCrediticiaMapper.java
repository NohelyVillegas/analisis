package com.espe.analisis.crediticio.controller.mapper;

import com.espe.analisis.crediticio.controller.dto.EvaluacionCrediticiaDTO;
import com.espe.analisis.crediticio.model.EvaluacionCrediticia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EvaluacionCrediticiaMapper {
    
    EvaluacionCrediticiaDTO toDTO(EvaluacionCrediticia model);
    
    EvaluacionCrediticia toModel(EvaluacionCrediticiaDTO dto);
}