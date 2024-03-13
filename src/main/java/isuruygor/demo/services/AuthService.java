package isuruygor.demo.services;

import isuruygor.demo.entities.Role;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.exceptions.UnauthorizedException;
import isuruygor.demo.payloads.LoginDTO;
import isuruygor.demo.payloads.UserPayloadDTO;
import isuruygor.demo.repositories.UserRepo;
import isuruygor.demo.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;


    @Autowired
    private UserRepo userRepo;


    //login
    public String authenticateUser(LoginDTO body) {
        User user = userService.findByEmail(body.email());
        if (bcrypt.matches(body.password(), user.getPassword())) {
            return jwtTools.createToken(user);
        } else {
            throw new UnauthorizedException("Credenziali non valide!");
        }
    }


    //register
    public User saveUser(UserPayloadDTO payload) {
        User newUser = new User();
        newUser.setRole(Role.USER);
        newUser.setLastname(payload.lastname());
        newUser.setName(payload.name());
        newUser.setEmail(payload.email());
        newUser.setUsername(payload.username());
        newUser.setPassword(bcrypt.encode(payload.password()));

        if (userRepo.existsByEmail(payload.email())) {
            throw new BadRequestException("L'email " + payload.email() + " è gia presente nel sistema.");
        } else if (userRepo.existsByUsername(payload.username())) {
            throw new BadRequestException("Lo username " + payload.username() + " è gia presente nel sistema.");
        } else {
            return userRepo.save(newUser);
        }
    }


}

