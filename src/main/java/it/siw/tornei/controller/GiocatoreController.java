package it.siw.tornei.controller;

import it.siw.tornei.model.Giocatore;
import it.siw.tornei.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/giocatori")
public class GiocatoreController {

    @Autowired private GiocatoreService giocatoreService;
    @Autowired private SquadraService squadraService;

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("giocatori", giocatoreService.findAll());
        return "giocatori/lista";
    }

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        model.addAttribute("giocatore", giocatoreService.findById(id).orElseThrow());
        return "giocatori/dettaglio";
    }

    @GetMapping("/admin/nuovo")
    public String formNuovo(Model model) {
        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("ruoli", Giocatore.Ruolo.values());
        return "giocatori/form";
    }

    @PostMapping("/admin/salva")
    public String salva(@Valid @ModelAttribute("giocatore") Giocatore g,
                        BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("squadre", squadraService.findAll());
            model.addAttribute("ruoli", Giocatore.Ruolo.values());
            return "giocatori/form";
        }
        Giocatore salvato = giocatoreService.save(g);
        return "redirect:/giocatori/" + salvato.getId();
    }

    @GetMapping("/admin/modifica/{id}")
    public String formModifica(@PathVariable Long id, Model model) {
        model.addAttribute("giocatore", giocatoreService.findById(id).orElseThrow());
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("ruoli", Giocatore.Ruolo.values());
        return "giocatori/form";
    }

    @PostMapping("/admin/elimina/{id}")
    public String elimina(@PathVariable Long id) {
        giocatoreService.deleteById(id);
        return "redirect:/giocatori";
    }
}
