package isuruygor.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import isuruygor.demo.entities.Post;
import isuruygor.demo.entities.PostCategory;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.exceptions.UnauthorizedException;
import isuruygor.demo.payloads.PostPayloadDTO;
import isuruygor.demo.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    // metoto per eliminare un post, prima si fa controllo se il post è presente nella lista dei post
    //dell'utente
    public void findByIdAndDelete(User user, long id) {

        User found = userService.findById(user.getId());
        Post foundPost = this.findByid(id);

        if (found.getPostList().contains(foundPost)) {
            postRepo.delete(foundPost);
        } else {
            throw new UnauthorizedException("Il post da eliminare non corrisponde all'utente corrente.");
        }
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


    // solo per admin metodo patch che approva il post

    public Post approvePost(long id) {
        Post found = this.findByid(id);
        found.setApproved(true);
        return postRepo.save(found);
    }


    public Post findByIdAndUpdatePost(User user, long postId, PostPayloadDTO body) {

        User Found = userService.findById(user.getId());
        Post foundPost = this.findByid(postId);

        if (foundPost.getUser().getId() == Found.getId()) {
            foundPost.setPostTags(body.tags());
            foundPost.setPostDate(LocalDateTime.now());
            foundPost.setContent(body.content());
            foundPost.setTitle(body.title());
            String category = body.category().toUpperCase();
            switch (category) {
                case "TECH":
                    foundPost.setCategory(PostCategory.TECH);
                    break;

                case "TUTORIAL":
                    foundPost.setCategory(PostCategory.TUTORIAL);
                    break;
                case "SCIENCE":
                    foundPost.setCategory(PostCategory.SCIENCE);
                    break;
                case "PROGRAMMING":
                    foundPost.setCategory(PostCategory.PROGRAMMING);
                    break;
                case "WEB":
                    foundPost.setCategory(PostCategory.WEB);
                    break;

                default:
                    throw new BadRequestException("Errore nella sintassi della categoria");
            }

            return postRepo.save(foundPost);

        } else {
            throw new UnauthorizedException("Il post da modificare non corrisponde all'utente corrente.");
        }

    }


    // patch per upload immagine post
    public String uploadPostImage(MultipartFile file, long postId, User user) throws IOException {

        String url = (String) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");

        User Found = userService.findById(user.getId());
        Post foundPost = this.findByid(postId);

        if (foundPost.getUser().getId() == Found.getId()) {
            foundPost.setPostImage(url);
            return url;
        } else {
            throw new UnauthorizedException("Il post da modificare non corrisponde all'utente corrente.");
        }

    }


}
