package it.siw.tornei.controller;

import it.siw.tornei.dto.RigaClassificaDTO;
import it.siw.tornei.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint REST consumato dal modulo React per visualizzare la classifica del torneo.
 */
@RestController
@RequestMapping("/rest/tornei")
public class ClassificaRestController {

    @Autowired private TorneoService torneoService;

    @GetMapping("/{id}/classifica")
    public List<RigaClassificaDTO> classifica(@PathVariable Long id) {
        return torneoService.calcolaClassifica(id);
    }
}
