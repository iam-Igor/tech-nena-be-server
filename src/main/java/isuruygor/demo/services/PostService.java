package isuruygor.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import isuruygor.demo.entities.*;
import isuruygor.demo.exceptions.BadRequestException;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.exceptions.UnauthorizedException;
import isuruygor.demo.payloads.PostPayloadDTO;
import isuruygor.demo.repositories.PostRepo;
import isuruygor.demo.responses.NewPostCreatedResponse;
import isuruygor.demo.responses.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PostService {


    @Autowired
    PostRepo postRepo;

    @Autowired
    UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    public static List<String> getAllCategories() {
        List<String> newList = new ArrayList<>();

        for (PostCategory p : PostCategory.values()) {
            String formattedCategory = p.toString().substring(0, 1).toUpperCase() + p.toString().substring(1).toLowerCase();
            newList.add(formattedCategory);
        }

        return newList;
    }

    // method that converts post list response
    private PostResponse sendPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostDate(),
                post.getCategory(),
                post.getPostTags(),
                post.getPostImage(),
                post.getApproved(),
                post.getState(),
                post.getUser().getUsername(),
                post.getUser().getName(),
                post.getUser().getLastname(),
                post.getUser().getAvatarUrl(),
                post.getUser().getId()
        );
    }

    // metodo usato in home per caricare tutti i post PUBBLICO
    public Page<PostResponse> getPost(int page, int size, String orderBy) {
        if (size >= 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        // TODO: modify entity(boolean approved?)
        // List<Post> postlist = postRepo.findAll(pageable).stream().filter(post -> post.isApproved()).toList();
        List<PostResponse> postlist = postRepo.findAll().stream().filter(post -> post.getState() == PostType.APPROVED).map(this::sendPostResponse
        ).toList();

        return new PageImpl<PostResponse>(postlist, pageable, postlist.size());
    }

    // gets post list based on parameters
    // ADMIN endpoint
    public Page<PostResponse> getAllPosts(int page, int size, String orderBy, User currentUser, String state) {
        // validates user authorities
        User user = userService.findById(currentUser.getId());
        if(user.getRole() != Role.ADMIN) throw new UnauthorizedException("Only Admins have access!");

        // sends all the posts
        if(size >= 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy).descending());
        List<PostResponse> postlist = new ArrayList<>();

        // gets all the posts
        if(Objects.equals(state, "all"))
            postlist = postRepo.findAll(pageable).stream()
                    .map(this::sendPostResponse).toList();

        // gets all the approved posts
        if(Objects.equals(state, "approved"))
            postlist = postRepo.findAll(pageable).stream()
                    .filter(post -> post.getState() == PostType.APPROVED)
                    .map(this::sendPostResponse).toList();

        // gets all the not approved posts
        if(Objects.equals(state, "notApproved"))
            postlist = postRepo.findAll(pageable).stream()
                    .filter(post -> post.getState() == PostType.NOT_APPROVED)
                    .map(this::sendPostResponse).toList();

        // gets all the pending posts
        if(Objects.equals(state, "pending"))
            postlist = postRepo.findAll(pageable).stream()
                    .filter(post -> post.getState() == PostType.PENDING)
                    .map(this::sendPostResponse).toList();

        // gets all the hide posts
        // ? there might be no use case
        if(Objects.equals(state, "hide"))
            postlist = postRepo.findAll(pageable).stream()
                    .filter(post -> post.getState() == PostType.HIDE)
                    .map(this::sendPostResponse).toList();

        return new PageImpl<PostResponse>(postlist, pageable, postlist.size());
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


    // solo per admin metodo patch che approva il post

    // creazione nuovo post, default data a now e approved a false
    public NewPostCreatedResponse saveNewPost(User user, PostPayloadDTO body) {

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

        postRepo.save(newPost);
        return new NewPostCreatedResponse(newPost.getId());
    }


    //ottieni lista commenti per un post

    public Post approvePost(long id) {
        Post found = this.findByid(id);
        found.setApproved(true);
        return postRepo.save(found);
    }


    //ottieni lista di post by categpry

    public List<Post> findByCategory(String category) {
        return postRepo.getPostsByCategory(category.toUpperCase());
    }

    public List<Comment> getCommentsListForPost(long postId) {

        Post found = this.findByid(postId);

        return found.getComments();
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
            postRepo.save(foundPost);
            return url;
        } else {
            throw new UnauthorizedException("Il post da modificare non corrisponde all'utente corrente.");
        }

    }


}
