package isuruygor.demo.services;

import com.cloudinary.Cloudinary;
import isuruygor.demo.entities.Post;
import isuruygor.demo.entities.PostCategory;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.payloads.PostPayloadDTO;
import isuruygor.demo.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {


    @Autowired
    PostRepo postRepo;


    @Autowired
    UserService userService;

    @Autowired
    private Cloudinary cloudinary;


    // metodo usato in home per caricare tutti i post PUBBLICO
    public Page<Post> getPost(int page, int size, String orderBy) {
        if (size >= 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        List<Post> postlist = postRepo.findAll(pageable).stream().filter(post -> post.isApproved()).toList();
        return postRepo.findAll((Pageable) postlist);
    }


    public Post findByid(long id) {
        return postRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }


    // creazione nuovo post, default data a now e approved a false
    public Post saveNewPost(User user, PostPayloadDTO body) {

        User found = userService.findById(user.getId());

        Post newPost = new Post();

        newPost.setPostDate(LocalDateTime.now());
        newPost.setContent(body.content());
        newPost.setTitle(body.title());
        newPost.setApproved(false);
        newPost.setPostTags(body.tags());
        newPost.setUser(found);
        String category = body.category().toUpperCase();
        switch (category) {
            case "TECH":
                newPost.setCategory(PostCategory.TECH);
                break;

            case "TUTORIAL":
                newPost.setCategory(PostCategory.TUTORIAL);
                break;
            case "SCIENCE":
                newPost.setCategory(PostCategory.SCIENCE);
                break;
            case "PROGRAMMING":
                newPost.setCategory(PostCategory.PROGRAMMING);
                break;
            case "WEB":
                newPost.setCategory(PostCategory.WEB);
                break;

            default:
                throw new BadRequestException("Errore nella sintassi della categoria");
        }

        return postRepo.save(newPost);

    }


    public Post approvePost(long id) {
        Post found = this.findByid(id);
        found.setApproved(true);
        return postRepo.save(found);
    }

}
