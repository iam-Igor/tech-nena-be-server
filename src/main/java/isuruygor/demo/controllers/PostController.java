package isuruygor.demo.controllers;


import isuruygor.demo.entities.Comment;
import isuruygor.demo.entities.Post;
import isuruygor.demo.entities.User;
import isuruygor.demo.payloads.PostPayloadDTO;
import isuruygor.demo.responses.PostResponse;
import isuruygor.demo.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping("/{id}")
    public Post findById(@PathVariable long id) {
        return postService.findByid(id);
    }

    @GetMapping("/approved")
    public Page<PostResponse> getAllApprovedPosts(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "id") String order) {
        return postService.getPost(page, size, order);
    }

    @GetMapping("/pending")
    public Page<PostResponse> getPendingPosts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "id") String order,
                                          @AuthenticationPrincipal User currentUser) {
        return postService.getPendingPosts(page, size, order, currentUser);

    }

    @GetMapping("/all")
    public Page<PostResponse> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "id") String order,
                                          @AuthenticationPrincipal User currentUser) {
        return postService.getAllPosts(page, size, order, currentUser);

    }


    @GetMapping("/comments/{id}")
    public List<Comment> getCommentsListForPost(@PathVariable long id) {
        return postService.getCommentsListForPost(id);
    }


    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return postService.getAllCategories();
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id, @AuthenticationPrincipal User user) {
        postService.findByIdAndDelete(user, id);
    }


    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Post saveNewPost(@AuthenticationPrincipal User user, @RequestBody @Validated PostPayloadDTO body) {
        return postService.saveNewPost(user, body);
    }


    @PatchMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Post approvePost(@PathVariable long id) {

        return postService.approvePost(id);
    }


    @PutMapping("/update/{id}")
    public Post updatePost(@AuthenticationPrincipal User user, @PathVariable long id, @RequestBody @Validated PostPayloadDTO body) {
        return postService.findByIdAndUpdatePost(user, id, body);
    }


    @PatchMapping("/upload/{id}")
    public String uploadPostImage(@RequestParam("image") MultipartFile file, @PathVariable long id, @AuthenticationPrincipal User user) throws IOException {
        return postService.uploadPostImage(file, id, user);
    }
}
