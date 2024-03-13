package isuruygor.demo.controllers;


import isuruygor.demo.entities.Comment;
import isuruygor.demo.entities.User;
import isuruygor.demo.payloads.CommentPayloadDTO;
import isuruygor.demo.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {


    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public Comment findByid(@PathVariable long id) {
        return commentService.findById(id);
    }


    @PostMapping("/new/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment saveNewComment(@AuthenticationPrincipal User user, @PathVariable long id, @RequestBody CommentPayloadDTO body) {
        return commentService.saveNewComment(user, id, body);
    }


    @DeleteMapping("/{id}")
    public void deleteComment(@AuthenticationPrincipal User user, @PathVariable long id) {
        commentService.deleteComment(user, id);
    }

    @PutMapping("/update/{id}")
    public Comment updateComment(@AuthenticationPrincipal User user, @PathVariable long id, @RequestBody CommentPayloadDTO body) {
        return commentService.updateComment(user, id, body);
    }

}
