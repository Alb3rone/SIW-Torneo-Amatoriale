package it.siw.tornei.controller;

import it.siw.tornei.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired private TorneoService torneoService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tornei", torneoService.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

}
