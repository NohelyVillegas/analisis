package com.espe.analisis.crediticio.exception;

public class BureauServiceException extends RuntimeException {
    
    private final String operation;
    private final String details;
    
    public BureauServiceException(String operation, String details) {
        super();
        this.operation = operation;
        this.details = details;
    }
    
    @Override
    public String getMessage() {
        return "Error en el servicio del buró durante la operación: " + this.operation + ". Detalles: " + details;
    }
}