package com.banquito.analisis.controller.mapper;

import com.banquito.analisis.controller.dto.EvaluacionCrediticiaDTO;
import com.banquito.analisis.model.EvaluacionCrediticia;
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