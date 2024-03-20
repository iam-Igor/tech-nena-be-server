package isuruygor.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import isuruygor.demo.entities.Comment;
import isuruygor.demo.entities.Post;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.payloads.UserPayloadDTO;
import isuruygor.demo.payloads.UserUpdateDTO;
import isuruygor.demo.repositories.UserRepo;
import isuruygor.demo.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Cloudinary cloudinary;


    @Autowired
    private PasswordEncoder bcrypt;


    public Page<User> getUsers(int page, int size, String orderBy) {
        if (size >= 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return userRepo.findAll(pageable);
    }

    public User findById(long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("utente not trovato: " + id));
    }

    public void findByIdAndDelete(long id) {
        User found = this.findById(id);


        userRepo.delete(found);
    }


    public User findByEmail(String email) throws NotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }


    // patch
    public String uploadAvatarImage(MultipartFile file, User user) throws IOException {

        String url = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");

        user.setAvatarUrl(url);
        userRepo.save(user);
        return url;
    }


    // patch per modificare i dati di un utente che arriva tramite auth principal
    public UserResponse updateUser(User currentUser, UserUpdateDTO payload) {
        User user = this.findById(currentUser.getId());

        if(payload.password() != null && !payload.password().isEmpty())
            user.setPassword(bcrypt.encode(payload.password()));

        if(payload.name() != null && !payload.name().isEmpty())
            user.setName(payload.name());

        if(payload.lastname() != null && !payload.lastname().isEmpty())
            user.setLastname(payload.lastname());

        if(payload.email() != null && !payload.email().isEmpty())
            user.setEmail(payload.email());

        if(payload.username() != null && !payload.username().isEmpty())
            user.setUsername(payload.username());

        System.out.println("receive body : " + payload);
        System.out.println("update user " + user);

        userRepo.save(user);
        return new UserResponse(
                user.getAvatarUrl(),
                user.getEmail(),
                user.getId(),
                user.getLastname(),
                user.getName(),
                user.getRole(),
                user.getUsername());
    }


    public List<Comment> getCommentsList(User user) {
        User found = this.findById(user.getId());
        return found.getComments();
    }

    public List<Post> getPostList(User user) {
        User found = this.findById(user.getId());
        return found.getPostList();
    }


}
