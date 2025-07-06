package com.espe.analisis.crediticio.exception;

public class CreditEvaluationException extends RuntimeException {
    
    private final String evaluationStep;
    private final String reason;
    
    public CreditEvaluationException(String evaluationStep, String reason) {
        super();
        this.evaluationStep = evaluationStep;
        this.reason = reason;
    }
    
    @Override
    public String getMessage() {
        return "Error en la evaluación crediticia en el paso: " + this.evaluationStep + ". Razón: " + reason;
    }
}