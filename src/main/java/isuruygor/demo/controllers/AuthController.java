package isuruygor.demo.controllers;

import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.payloads.LoginDTO;
import isuruygor.demo.payloads.UserPayloadDTO;
import isuruygor.demo.responses.GeneralResponse;
import isuruygor.demo.responses.TokenResponse;
import isuruygor.demo.services.AuthService;
import isuruygor.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse login(@RequestBody @Validated LoginDTO payload) {
        String accessToken = authService.authenticateUser(payload);
        return new TokenResponse(accessToken);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GeneralResponse saveUser(@RequestBody(required = false) @Validated UserPayloadDTO body, BindingResult bindingResult) {
        if (body == null) {
            throw new BadRequestException("Il corpo della richiesta non può essere vuoto");
        }
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Errore nel body della richiesta");
        } else {
            User newuser = authService.saveUser(body);
            return new GeneralResponse(newuser.getId());
        }
    }

}
