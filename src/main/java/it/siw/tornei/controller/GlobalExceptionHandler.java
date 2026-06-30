package it.siw.tornei.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException e, Model model) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        model.addAttribute("messaggio", e.getMessage());
        return "errore";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("Accesso negato: {}", e.getMessage());
        model.addAttribute("messaggio", "Accesso negato: " + e.getMessage());
        return "errore";
    }

    /**
     * Fallback per qualsiasi altra eccezione non gestita: log dello stack trace
     * e pagina di errore amichevole invece della Whitelabel Error Page.
     * Il server continua a funzionare, solo la richiesta corrente fallisce.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception e, Model model) {
        log.error("Errore non gestito", e);
        model.addAttribute("messaggio",
            "Si e' verificato un errore imprevisto: " + e.getClass().getSimpleName()
            + (e.getMessage() != null ? " - " + e.getMessage() : ""));
        return "errore";
    }
}
