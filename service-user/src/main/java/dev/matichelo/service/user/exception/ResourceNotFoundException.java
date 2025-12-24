package dev.matichelo.service.user.exception;

public class ResourceNotFoundException extends RuntimeException{

    // super es para llamar al constructor de la clase padre
    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
