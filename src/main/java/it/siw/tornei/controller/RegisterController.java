package it.siw.tornei.controller;

import it.siw.tornei.model.Credentials;
import it.siw.tornei.model.Utente;
import it.siw.tornei.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired private CredentialsService credentialsService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("credentials", new Credentials());
        model.addAttribute("utente", new Utente());
        return "auth/register";
    }

    @PostMapping
    public String registra(@Valid @ModelAttribute("credentials") Credentials c,
                            BindingResult crBr,
                            @Valid @ModelAttribute("utente") Utente u,
                            BindingResult utBr) {
        if (crBr.hasErrors() || utBr.hasErrors()) return "auth/register";
        if (credentialsService.existsByUsername(c.getUsername())) {
            crBr.rejectValue("username", "duplicato", "Username gia' usato");
            return "auth/register";
        }
        c.setUtente(u);
        c.setRole(Credentials.USER_ROLE);
        credentialsService.save(c);
        return "redirect:/login";
    }
}
