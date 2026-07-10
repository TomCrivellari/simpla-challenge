package br.com.simplameta.meta_service.exception;

public class FinanceServiceCommunicationException extends RuntimeException {

    public FinanceServiceCommunicationException() {
        super("Could not synchronize goal contribution with finance service");
    }
}
