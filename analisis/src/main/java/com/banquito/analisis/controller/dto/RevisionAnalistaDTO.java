package com.banquito.analisis.controller.dto;

import com.banquito.analisis.model.Enums.DecisionFinalAnalista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Solicitud de revisión por analista")
public class RevisionAnalistaDTO {
    
    @NotNull(message = "El ID de evaluación es obligatorio")
    @Schema(description = "ID de la evaluación crediticia", example = "123")
    private Long idEvaluacion;
    
    @NotNull(message = "La decisión final es obligatoria")
    @Schema(description = "Decisión final del analista")
    private DecisionFinalAnalista decisionFinal;
    
    @NotBlank(message = "La justificación es obligatoria")
    @Schema(description = "Justificación de la decisión del analista", example = "Cliente con historial crediticio favorable")
    private String justificacion;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario analista", example = "456")
    private Long idUsuario;
}