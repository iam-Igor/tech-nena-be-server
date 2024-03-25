package isuruygor.demo.controllers;

import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.payloads.UserPayloadDTO;
import isuruygor.demo.payloads.UserUpdateDTO;
import isuruygor.demo.responses.UserResponse;
import isuruygor.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/me")
    public UserResponse getUser(
            @AuthenticationPrincipal User currentUser
            ) {
        User user = userService.findById(currentUser.getId());
        System.out.println("we ARE RECEIVING THE USER: " + currentUser.getEmail());

        return new UserResponse(user.getAvatarUrl(),
                user.getEmail(),
                user.getId(),
                user.getLastname(),
                user.getName(),
                user.getRole(),
                user.getUsername());
    }

    @PatchMapping("/profile/me")
    public UserResponse updateUser(
            @RequestBody
            @Validated UserUpdateDTO body,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser
            ) {
        if(bindingResult.hasErrors()) {
            throw new BadRequestException("Errore nel body della richiesta");
        } else {
            return userService.updateUser(currentUser, body);
        }
    }
}
