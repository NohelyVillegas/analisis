package com.banquito.analisis.controller.mapper;

import com.banquito.analisis.controller.dto.ConsultaBuroDTO;
import com.banquito.analisis.model.ConsultasBuro;
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