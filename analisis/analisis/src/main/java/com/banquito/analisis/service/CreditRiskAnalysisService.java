package com.espe.analisis.crediticio.service;

import com.espe.analisis.crediticio.client.BureauCreditClient;
import com.espe.analisis.crediticio.client.dto.BureauConsultaRequest;
import com.espe.analisis.crediticio.client.dto.BureauConsultaResponse;
import com.espe.analisis.crediticio.controller.dto.EvaluacionCrediticiaDTO;
import com.espe.analisis.crediticio.controller.dto.RevisionAnalistaDTO;
import com.espe.analisis.crediticio.controller.dto.SolicitudAnalisisDTO;
import com.espe.analisis.crediticio.controller.mapper.EvaluacionCrediticiaMapper;
import com.espe.analisis.crediticio.model.*;
import com.espe.analisis.crediticio.exception.BureauServiceException;
import com.espe.analisis.crediticio.exception.CreditEvaluationException;
import com.espe.analisis.crediticio.exception.NotFoundException;
import com.espe.analisis.crediticio.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class CreditRiskAnalysisService {
    
    private final ConsultasBuroRepository consultasBuroRepository;
    private final InformesBuroRepository informesBuroRepository;
    private final EvaluacionCrediticiaRepository evaluacionCrediticiaRepository;
    private final ObservacionAnalistaRepository observacionAnalistaRepository;
    private final BureauCreditClient bureauCreditClient;
    private final EvaluacionCrediticiaMapper evaluacionMapper;
    
    private static final BigDecimal FACTOR_CAPACIDAD_PAGO = new BigDecimal("0.30");
    private static final BigDecimal SCORE_APROBACION_AUTOMATICA = new BigDecimal("750");
    private static final BigDecimal SCORE_REVISION_MANUAL = new BigDecimal("600");
    
    public CreditRiskAnalysisService(ConsultasBuroRepository consultasBuroRepository,
                                   InformesBuroRepository informesBuroRepository,
                                   EvaluacionCrediticiaRepository evaluacionCrediticiaRepository,
                                   ObservacionAnalistaRepository observacionAnalistaRepository,
                                   BureauCreditClient bureauCreditClient,
                                   EvaluacionCrediticiaMapper evaluacionMapper) {
        this.consultasBuroRepository = consultasBuroRepository;
        this.informesBuroRepository = informesBuroRepository;
        this.evaluacionCrediticiaRepository = evaluacionCrediticiaRepository;
        this.observacionAnalistaRepository = observacionAnalistaRepository;
        this.bureauCreditClient = bureauCreditClient;
        this.evaluacionMapper = evaluacionMapper;
    }
    
    @Retryable(value = {BureauServiceException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ConsultasBuro consultarBuro(Long idSolicitud) {
        log.info("Iniciando consulta al buró para solicitud: {}", idSolicitud);
        
        // Verificar si ya existe una consulta pendiente o completada
        var consultaExistente = consultasBuroRepository.findByIdSolicitud(idSolicitud);
        if (consultaExistente.isPresent() && 
            consultaExistente.get().getEstadoConsulta() == EstadoConsulta.COMPLETADA) {
            log.info("Ya existe una consulta completada para la solicitud: {}", idSolicitud);
            return consultaExistente.get();
        }
        
        ConsultasBuro consulta = consultaExistente.orElse(new ConsultasBuro());
        consulta.setIdSolicitud(idSolicitud);
        consulta.setEstadoConsulta(EstadoConsulta.REINTENTANDO);
        consulta.setFechaConsulta(LocalDateTime.now());
        
        try {
            BureauConsultaRequest request = new BureauConsultaRequest();
            request.setIdSolicitud(idSolicitud);
            
            BureauConsultaResponse response = bureauCreditClient.consultarBuro(request);
            
            if (response.isExitoso()) {
                consulta.setScoreExterno(response.getScoreExterno());
                consulta.setCuentasActivas(response.getCuentasActivas());
                consulta.setCuentasMorosas(response.getCuentasMorosas());
                consulta.setMontoMorosoTotal(response.getMontoTotalAdeudado());
                consulta.setDiasMoraPromedio(response.getDiasMoraPromedio());
                consulta.setFechaPrimeraMora(response.getFechaPrimeraMora());
                consulta.setEstadoConsulta(EstadoConsulta.COMPLETADA);
                
                log.info("Consulta al buró exitosa para solicitud: {}", idSolicitud);
            } else {
                consulta.setEstadoConsulta(EstadoConsulta.FALLIDA);
                throw new BureauServiceException("Consulta Buró", response.getMensaje());
            }
            
        } catch (Exception e) {
            log.error("Error al consultar el buró para solicitud: {}", idSolicitud, e);
            consulta.setEstadoConsulta(EstadoConsulta.FALLIDA);
            throw new BureauServiceException("Consulta Buró", e.getMessage());
        }
        
        return consultasBuroRepository.save(consulta);
    }
    
    public InformesBuro procesarInformeBuro(Long idConsultaBuro) {
        log.info("Procesando informe del buró para consulta: {}", idConsultaBuro);
        
        ConsultasBuro consulta = consultasBuroRepository.findById(idConsultaBuro)
            .orElseThrow(() -> new NotFoundException(idConsultaBuro.toString(), "ConsultasBuro"));
        
        if (consulta.getEstadoConsulta() != EstadoConsulta.COMPLETADA) {
            throw new CreditEvaluationException("Informe Buró", "La consulta no está completada");
        }
        
        var informeExistente = informesBuroRepository.findByIdConsultaBuro(idConsultaBuro);
        if (informeExistente.isPresent()) {
            log.info("Ya existe un informe para la consulta: {}", idConsultaBuro);
            return informeExistente.get();
        }
        
        InformesBuro informe = new InformesBuro();
        informe.setIdConsultaBuro(idConsultaBuro);
        informe.setScore(consulta.getScoreExterno());
        informe.setMontoTotalAdeudado(consulta.getMontoMorosoTotal());
        informe.setNumeroDeudasImpagas(consulta.getCuentasMorosas());
        informe.setJsonRespuestaCompleta("{}"); // Simulated JSON response
        
        log.info("Informe del buró procesado exitosamente para consulta: {}", idConsultaBuro);
        
        return informesBuroRepository.save(informe);
    }
    
    public EvaluacionCrediticiaDTO evaluarAutomaticamente(SolicitudAnalisisDTO solicitud) {
        log.info("Iniciando evaluación automática para solicitud: {}", solicitud.getIdSolicitud());
        
        // Módulo 1: Consulta Buró
        ConsultasBuro consulta = consultarBuro(solicitud.getIdSolicitud());
        
        // Módulo 2: Informe del Buró
        InformesBuro informe = procesarInformeBuro(consulta.getIdConsulta());
        
        // Módulo 3: Evaluación Interna
        EvaluacionCrediticia evaluacion = calcularEvaluacionInterna(solicitud, informe);
        
        log.info("Evaluación automática completada para solicitud: {}", solicitud.getIdSolicitud());
        
        return evaluacionMapper.toDTO(evaluacion);
    }
    
    private EvaluacionCrediticia calcularEvaluacionInterna(SolicitudAnalisisDTO solicitud, InformesBuro informe) {
        log.info("Calculando evaluación interna para solicitud: {}", solicitud.getIdSolicitud());
        
        // Calcular capacidad de pago
        BigDecimal capacidadPago = (solicitud.getIngresos().subtract(solicitud.getEgresos()))
            .multiply(FACTOR_CAPACIDAD_PAGO);
        
        EvaluacionCrediticia evaluacion = new EvaluacionCrediticia();
        evaluacion.setIdSolicitud(solicitud.getIdSolicitud());
        evaluacion.setIdInformeBuro(informe.getIdInformeBuro());
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setScoreInterno(informe.getScore());
        
        StringBuilder observaciones = new StringBuilder();
        
        // Verificar capacidad de pago
        if (capacidadPago.compareTo(solicitud.getCuotaMensual()) < 0) {
            evaluacion.setResultadoAutomatico(ResultadoAutomatico.RECHAZADO);
            observaciones.append("Capacidad de pago insuficiente. ");
            observaciones.append("Capacidad: ").append(capacidadPago);
            observaciones.append(", Cuota requerida: ").append(solicitud.getCuotaMensual());
        } else {
            // Aplicar reglas de score
            BigDecimal score = informe.getScore();
            Integer deudasMorosas = informe.getNumeroDeudasImpagas();
            
            if (score.compareTo(SCORE_APROBACION_AUTOMATICA) > 0 && 
                (deudasMorosas == null || deudasMorosas == 0)) {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.APROBADO);
                observaciones.append("Score excelente y sin deudas morosas. Aprobación automática.");
            } else if (score.compareTo(SCORE_REVISION_MANUAL) >= 0 && 
                       score.compareTo(SCORE_APROBACION_AUTOMATICA) <= 0) {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.REVISION_MANUAL);
                observaciones.append("Score intermedio. Requiere revisión manual.");
            } else {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.RECHAZADO);
                observaciones.append("Score bajo o deudas morosas detectadas. Rechazo automático.");
            }
        }
        
        evaluacion.setObservacionesMotorReglas(observaciones.toString());
        
        log.info("Evaluación interna calculada. Resultado: {} para solicitud: {}", 
                evaluacion.getResultadoAutomatico(), solicitud.getIdSolicitud());
        
        return evaluacionCrediticiaRepository.save(evaluacion);
    }
    
    public EvaluacionCrediticiaDTO procesarRevisionAnalista(RevisionAnalistaDTO revision) {
        log.info("Procesando revisión del analista para evaluación: {}", revision.getIdEvaluacion());
        
        EvaluacionCrediticia evaluacion = evaluacionCrediticiaRepository.findById(revision.getIdEvaluacion())
            .orElseThrow(() -> new NotFoundException(revision.getIdEvaluacion().toString(), "EvaluacionCrediticia"));
        
        // Actualizar evaluación con decisión del analista
        evaluacion.setDecisionFinalAnalista(revision.getDecisionFinal());
        evaluacion.setJustificacionAnalista(revision.getJustificacion());
        
        evaluacion = evaluacionCrediticiaRepository.save(evaluacion);
        
        // Crear observación del analista
        ObservacionAnalista observacion = new ObservacionAnalista();
        observacion.setIdEvaluacion(revision.getIdEvaluacion());
        observacion.setIdUsuario(revision.getIdUsuario());
        observacion.setDecisionManual(DecisionManual.valueOf(revision.getDecisionFinal().name()));
        observacion.setJustificacion(revision.getJustificacion());
        observacion.setFechaHora(LocalDateTime.now());
        
        observacionAnalistaRepository.save(observacion);
        
        log.info("Revisión del analista procesada exitosamente para evaluación: {}", revision.getIdEvaluacion());
        
        return evaluacionMapper.toDTO(evaluacion);
    }
    
    @Transactional(readOnly = true)
    public EvaluacionCrediticiaDTO obtenerEvaluacionPorSolicitud(Long idSolicitud) {
        log.info("Obteniendo evaluación para solicitud: {}", idSolicitud);
        
        EvaluacionCrediticia evaluacion = evaluacionCrediticiaRepository.findByIdSolicitud(idSolicitud)
            .orElseThrow(() -> new NotFoundException(idSolicitud.toString(), "EvaluacionCrediticia"));
        
        return evaluacionMapper.toDTO(evaluacion);
    }
}