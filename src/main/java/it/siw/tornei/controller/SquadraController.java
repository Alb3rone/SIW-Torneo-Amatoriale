package it.siw.tornei.controller;

import it.siw.tornei.model.Squadra;
import it.siw.tornei.service.SquadraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/squadre")
public class SquadraController {

    @Autowired private SquadraService squadraService;

    // ---- Pubblico ----

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("squadre", squadraService.findAll());
        return "squadre/lista";
    }

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        Squadra s = squadraService.findByIdWithGiocatori(id);
        model.addAttribute("squadra", s);
        return "squadre/dettaglio";
    }

    // ---- Admin ----

    @GetMapping("/admin/nuova")
    public String formNuova(Model model) {
        model.addAttribute("squadra", new Squadra());
        return "squadre/form";
    }

    /**
     * Salva una squadra ricevuta dal form admin.
     * Il form ha enctype="multipart/form-data" cosi' Spring riempie il MultipartFile logoFile.
     * Se l'utente non carica un file, logoFile e' vuoto e il service mantiene il logo precedente.
     */
    @PostMapping("/admin/salva")
    public String salva(@Valid @ModelAttribute("squadra") Squadra s,
                         BindingResult br,
                         @RequestParam(value = "logoFile", required = false) MultipartFile logoFile) {
        if (br.hasErrors()) return "squadre/form";
        Squadra salvata = squadraService.salvaConLogo(s, logoFile);
        return "redirect:/squadre/" + salvata.getId();
    }

    @GetMapping("/admin/modifica/{id}")
    public String formModifica(@PathVariable Long id, Model model) {
        model.addAttribute("squadra", squadraService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata: " + id)));
        return "squadre/form";
    }

    @PostMapping("/admin/elimina/{id}")
    public String elimina(@PathVariable Long id) {
        squadraService.deleteById(id);
        return "redirect:/squadre";
    }
}
