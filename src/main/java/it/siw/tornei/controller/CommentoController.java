package it.siw.tornei.controller;

import it.siw.tornei.service.CommentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/commenti")
public class CommentoController {

    @Autowired
    private CommentoService commentoService;

    @PostMapping("/partita/{partitaId}/nuovo")
    public String nuovo(@PathVariable Long partitaId,
            @RequestParam String testo,
            @RequestParam(required = false) Integer voto, // opzionale: 1-5
            Principal principal) {
        try {
            commentoService.creaCommento(partitaId, testo, voto, principal.getName());
        } catch (IllegalArgumentException e) {
            return "redirect:/partite/" + partitaId + "?error=gia_esistente";
        }
        return "redirect:/partite/" + partitaId;
    }

    @PostMapping("/{id}/modifica")
    public String modifica(@PathVariable Long id,
            @RequestParam Long partitaId,
            @RequestParam String testo,
            @RequestParam(required = false) Integer voto, // opzionale
            Principal principal) {
        try {
            commentoService.modificaCommento(id, testo, voto, principal.getName());
        } catch (AccessDeniedException e) {
            return "redirect:/partite/" + partitaId + "?error=accesso";
        }
        return "redirect:/partite/" + partitaId;
    }

    @PostMapping("/{id}/elimina")
    public String elimina(@PathVariable Long id,
            @RequestParam Long partitaId,
            Principal principal) {
        commentoService.elimina(id, principal.getName());
        return "redirect:/partite/" + partitaId;
    }
}
