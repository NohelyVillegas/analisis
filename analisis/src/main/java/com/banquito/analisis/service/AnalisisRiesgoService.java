package com.banquito.analisis.service;

import com.banquito.analisis.client.ClienteBuroCrediticio;
import com.banquito.analisis.client.OriginacionClient;
import com.banquito.analisis.client.dto.ConsultasBuroRequest;
import com.banquito.analisis.client.dto.ConsultasBuroResponse;
import com.banquito.analisis.client.dto.SolicitudResumenDTO;
import com.banquito.analisis.controller.dto.EvaluacionCrediticiaDTO;
import com.banquito.analisis.controller.dto.RevisionAnalistaDTO;
import com.banquito.analisis.controller.dto.SolicitudAnalisisDTO;
import com.banquito.analisis.controller.mapper.EvaluacionCrediticiaMapper;
import com.banquito.analisis.model.*;
import com.banquito.analisis.model.Enums.EstadoConsulta;
import com.banquito.analisis.model.Enums.DecisionManual;
import com.banquito.analisis.model.Enums.ResultadoAutomatico;
import com.banquito.analisis.exception.ServicioBuroException;
import com.banquito.analisis.exception.EvaluacionCrediticiaExcepcion;
import com.banquito.analisis.exception.NotFoundException;
import com.banquito.analisis.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class AnalisisRiesgoService {
    
    private final ConsultasBuroRepository consultasBuroRepository;
    private final InformesBuroRepository informesBuroRepository;
    private final EvaluacionCrediticiaRepository evaluacionCrediticiaRepository;
    private final ObservacionAnalistaRepository observacionAnalistaRepository;
    private final ClienteBuroCrediticio clienteBuroCrediticio;
    private final EvaluacionCrediticiaMapper evaluacionMapper;
    private final OriginacionClient originacionClient;
    
    private static final BigDecimal FACTOR_CAPACIDAD_PAGO = new BigDecimal("0.30");
    private static final BigDecimal SCORE_APROBACION_AUTOMATICA = new BigDecimal("750");
    private static final BigDecimal SCORE_REVISION_MANUAL = new BigDecimal("600");
    
    public AnalisisRiesgoService(ConsultasBuroRepository consultasBuroRepository,
                                   InformesBuroRepository informesBuroRepository,
                                   EvaluacionCrediticiaRepository evaluacionCrediticiaRepository,
                                   ObservacionAnalistaRepository observacionAnalistaRepository,
                                   ClienteBuroCrediticio clienteBuroCrediticio,
                                   EvaluacionCrediticiaMapper evaluacionMapper,
                                   OriginacionClient originacionClient) {
        this.consultasBuroRepository = consultasBuroRepository;
        this.informesBuroRepository = informesBuroRepository;
        this.evaluacionCrediticiaRepository = evaluacionCrediticiaRepository;
        this.observacionAnalistaRepository = observacionAnalistaRepository;
        this.clienteBuroCrediticio = clienteBuroCrediticio;
        this.evaluacionMapper = evaluacionMapper;
        this.originacionClient = originacionClient;
    }
    
    @Retryable(value = {ServicioBuroException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
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
            ConsultasBuroRequest request = new ConsultasBuroRequest();
            request.setIdSolicitud(idSolicitud);
            
            ConsultasBuroResponse response = clienteBuroCrediticio.consultarBuro(request);
            
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
                throw new ServicioBuroException("Consulta Buró", response.getMensaje());
            }
            
        } catch (Exception e) {
            log.error("Error al consultar el buró para solicitud: {}", idSolicitud, e);
            consulta.setEstadoConsulta(EstadoConsulta.FALLIDA);
            throw new ServicioBuroException("Consulta Buró", e.getMessage());
        }
        
        return consultasBuroRepository.save(consulta);
    }
    
    public InformesBuro procesarInformeBuro(Long idConsultaBuro) {
        log.info("Procesando informe del buró para consulta: {}", idConsultaBuro);
        
        ConsultasBuro consulta = consultasBuroRepository.findById(idConsultaBuro)
            .orElseThrow(() -> new NotFoundException(idConsultaBuro.toString(), "ConsultasBuro"));
        
        if (consulta.getEstadoConsulta() != EstadoConsulta.COMPLETADA) {
            throw new EvaluacionCrediticiaExcepcion("Informe Buró", "La consulta no está completada");
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
        
        log.info("Informe del buró procesado exitosamente para consulta: {}", idConsultaBuro);
        
        return informesBuroRepository.save(informe);
    }
    
    public EvaluacionCrediticiaDTO evaluarAutomaticamente(SolicitudAnalisisDTO solicitud) {
        log.info("Iniciando evaluación automática para solicitud: {}", solicitud.getIdSolicitud());

        // Obtener la solicitud y el score desde Originación
        SolicitudResumenDTO resumen = originacionClient.obtenerResumenSolicitud(solicitud.getIdSolicitud());
        BigDecimal scoreInterno = resumen.getScoreInterno();

        // Módulo 1: Consulta Buró (si aplica)
        ConsultasBuro consulta = consultarBuro(solicitud.getIdSolicitud());
        InformesBuro informe = procesarInformeBuro(consulta.getIdConsulta());

        // Módulo 2: Evaluación Interna (usa el score traído)
        EvaluacionCrediticia evaluacion = calcularEvaluacionInterna(solicitud, informe, scoreInterno);

        log.info("Evaluación automática completada para solicitud: {}", solicitud.getIdSolicitud());
        return evaluacionMapper.toDTO(evaluacion);
    }

    private EvaluacionCrediticia calcularEvaluacionInterna(SolicitudAnalisisDTO solicitud, InformesBuro informe, BigDecimal scoreInterno) {
        log.info("Calculando evaluación interna para solicitud: {}", solicitud.getIdSolicitud());

        // Capacidad de pago
        BigDecimal capacidadPago = (solicitud.getIngresos().subtract(solicitud.getEgresos()))
            .multiply(FACTOR_CAPACIDAD_PAGO);

        EvaluacionCrediticia evaluacion = new EvaluacionCrediticia();
        evaluacion.setIdSolicitud(solicitud.getIdSolicitud());
        evaluacion.setIdInformeBuro(informe.getIdInformeBuro());
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setScoreInterno(scoreInterno);

        StringBuilder observaciones = new StringBuilder();

        // Reglas de negocio usando el score traído
        if (capacidadPago.compareTo(solicitud.getCuotaMensual()) < 0) {
            evaluacion.setResultadoAutomatico(ResultadoAutomatico.RECHAZADO);
            observaciones.append("Capacidad de pago insuficiente. ");
            evaluacion.setCalificacionCliente("TIPO C");
        } else {
            Integer deudasMorosas = informe.getNumeroDeudasImpagas();
            if (scoreInterno.compareTo(SCORE_APROBACION_AUTOMATICA) > 0 && (deudasMorosas == null || deudasMorosas == 0)) {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.APROBADO);
                observaciones.append("Score excelente y sin deudas morosas. Aprobación automática.");
                evaluacion.setCalificacionCliente("TIPO A");
            } else if (scoreInterno.compareTo(SCORE_REVISION_MANUAL) >= 0 && scoreInterno.compareTo(SCORE_APROBACION_AUTOMATICA) <= 0) {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.REVISION_MANUAL);
                observaciones.append("Score intermedio. Requiere revisión manual.");
                evaluacion.setCalificacionCliente("TIPO B");
            } else {
                evaluacion.setResultadoAutomatico(ResultadoAutomatico.RECHAZADO);
                observaciones.append("Score bajo o deudas morosas detectadas. Rechazo automático.");
                evaluacion.setCalificacionCliente("TIPO C");
            }
        }

        evaluacion.setObservacionesMotorReglas(observaciones.toString());
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