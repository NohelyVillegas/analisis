package com.espe.analisis.crediticio.controller.mapper;

import com.espe.analisis.crediticio.controller.dto.ConsultaBuroDTO;
import com.espe.analisis.crediticio.model.ConsultasBuro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ConsultaBuroMapper {
    
    ConsultaBuroDTO toDTO(ConsultasBuro model);
    
    ConsultasBuro toModel(ConsultaBuroDTO dto);
}