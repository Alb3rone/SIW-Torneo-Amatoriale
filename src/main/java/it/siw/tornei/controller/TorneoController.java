package it.siw.tornei.controller;

import it.siw.tornei.model.Torneo;
import it.siw.tornei.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tornei")
public class TorneoController {

    @Autowired private TorneoService torneoService;
    @Autowired private SquadraService squadraService;
    @Autowired private PartitaService partitaService;

    // Pubblico: elenco
    @GetMapping
    public String listaTornei(Model model) {
        model.addAttribute("tornei", torneoService.findAll());
        return "tornei/lista";
    }

    // Pubblico: dettaglio
    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        Torneo t = torneoService.findByIdWithSquadre(id);
        model.addAttribute("torneo", t);
        model.addAttribute("partite", partitaService.findByTorneo(id));
        return "tornei/dettaglio";
    }

    // Pubblico: classifica (rendering via React lato client)
    @GetMapping("/{id}/classifica")
    public String classifica(@PathVariable Long id, Model model) {
        model.addAttribute("torneo", torneoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo non trovato: " + id)));
        return "tornei/classifica";
    }

    // ---- Admin ----
    @GetMapping("/admin/nuovo")
    public String formNuovo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "tornei/form";
    }

    @PostMapping("/admin/salva")
    public String salva(@Valid @ModelAttribute("torneo") Torneo torneo,
                         BindingResult br) {
        if (br.hasErrors()) return "tornei/form";
        Torneo salvato = torneoService.save(torneo);
        return "redirect:/tornei/" + salvato.getId();
    }

    @GetMapping("/admin/modifica/{id}")
    public String formModifica(@PathVariable Long id, Model model) {
        model.addAttribute("torneo", torneoService.findById(id).orElseThrow());
        model.addAttribute("squadreDisponibili", squadraService.findAll());
        return "tornei/form";
    }

    @PostMapping("/admin/elimina/{id}")
    public String elimina(@PathVariable Long id) {
        torneoService.deleteById(id);
        return "redirect:/tornei";
    }
}
