package isuruygor.demo.services;

import isuruygor.demo.entities.Comment;
import isuruygor.demo.entities.Post;
import isuruygor.demo.entities.Role;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.exceptions.UnauthorizedException;
import isuruygor.demo.payloads.CommentPayloadDTO;
import isuruygor.demo.repositories.CommentRepo;
import isuruygor.demo.repositories.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepo commentRepo;

    public Comment findById(long id) { return commentRepo.findById(id).orElseThrow(() -> new NotFoundException(id));}

    // create new comment
    public Comment saveNewComment(User user, Long postId, CommentPayloadDTO payload) {
        User found = userService.findById(user.getId());
        Post post = postService.findByid(postId);
        Comment comment = new Comment();
        comment.setUser(found);
        comment.setPost(post);
        comment.setComment(payload.comment());
        comment.setTimeStamp(LocalDateTime.now());

        return commentRepo.save(comment);
    }

    // delete comment
    public void deleteComment(User currentUser, Long commentId) {
        User loggedUser = userService.findById(currentUser.getId());
        Comment comment = this.findById(commentId);

        // only the user has commented or the post author has permissions to delete a comment
        if(loggedUser.getRole() != Role.ADMIN && !comment.getUser().equals(loggedUser))
            throw new UnauthorizedException("User have no permission to delete the comment!");

        commentRepo.delete(comment);
        System.out.println("Comment deleted!");
    }

    // update comment
    public Comment updateComment(User currentUser, Long commentId, CommentPayloadDTO payload) {
        User user = userService.findById(currentUser.getId());
        Comment comment = this.findById(commentId);

        // checks if the user who tries to edit is the one who really commented
        if(!comment.getUser().equals(user))
            throw new UnauthorizedException("User have no permission to modify the comment");

        comment.setComment(payload.comment());
        comment.setEdited(true);
        return commentRepo.save(comment);
    }
}
