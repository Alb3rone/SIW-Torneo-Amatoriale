package it.siw.tornei.controller;

import it.siw.tornei.model.Partita;
import it.siw.tornei.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/partite")
public class PartitaController {

    @Autowired private PartitaService partitaService;
    @Autowired private TorneoService torneoService;
    @Autowired private SquadraService squadraService;
    @Autowired private ArbitroService arbitroService;
    @Autowired private CommentoService commentoService;

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("partite", partitaService.findAll());
        return "partite/lista";
    }

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model, Principal principal) {
        Partita p = partitaService.findByIdWithCommenti(id);
        model.addAttribute("partita", p);
        model.addAttribute("commenti", commentoService.findByPartita(id));
        model.addAttribute("username", principal != null ? principal.getName() : null);
        return "partite/dettaglio";
    }

    @GetMapping("/admin/nuova")
    public String nuova(Model model) {
        model.addAttribute("partita", new Partita());
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("arbitri", arbitroService.findAll());
        return "partite/form";
    }

    @PostMapping("/admin/salva")
    public String salva(@Valid @ModelAttribute("partita") Partita p, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("tornei", torneoService.findAll());
            model.addAttribute("squadre", squadraService.findAll());
            model.addAttribute("arbitri", arbitroService.findAll());
            return "partite/form";
        }
        Partita salvata = partitaService.save(p);
        return "redirect:/partite/" + salvata.getId();
    }

    @GetMapping("/admin/modifica/{id}")
    public String modifica(@PathVariable Long id, Model model) {
        model.addAttribute("partita", partitaService.findById(id).orElseThrow());
        model.addAttribute("tornei", torneoService.findAll());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("arbitri", arbitroService.findAll());
        return "partite/form";
    }

    @PostMapping("/admin/risultato/{id}")
    public String registraRisultato(@PathVariable Long id,
                                     @RequestParam int goalsHome,
                                     @RequestParam int goalsAway) {
        partitaService.registraRisultato(id, goalsHome, goalsAway);
        return "redirect:/partite/" + id;
    }

    @PostMapping("/admin/elimina/{id}")
    public String elimina(@PathVariable Long id) {
        partitaService.deleteById(id);
        return "redirect:/partite";
    }
}
