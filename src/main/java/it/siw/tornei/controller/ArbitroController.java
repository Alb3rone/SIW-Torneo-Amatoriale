package it.siw.tornei.controller;

import it.siw.tornei.model.Arbitro;
import it.siw.tornei.service.ArbitroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/arbitri")
public class ArbitroController {

    @Autowired private ArbitroService arbitroService;

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("arbitri", arbitroService.findAll());
        return "arbitri/lista";
    }

    @GetMapping("/admin/nuovo")
    public String nuovo(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        return "arbitri/form";
    }

    @PostMapping("/admin/salva")
    public String salva(@Valid @ModelAttribute("arbitro") Arbitro a, BindingResult br) {
        if (br.hasErrors()) return "arbitri/form";
        try {
            arbitroService.save(a);
        } catch (IllegalArgumentException e) {
            br.rejectValue("codiceArbitrale", "duplicato", e.getMessage());
            return "arbitri/form";
        }
        return "redirect:/arbitri";
    }

    @PostMapping("/admin/elimina/{id}")
    public String elimina(@PathVariable Long id) {
        arbitroService.deleteById(id);
        return "redirect:/arbitri";
    }
}
