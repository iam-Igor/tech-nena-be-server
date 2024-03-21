package isuruygor.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {


    @Id
    @GeneratedValue
    private long id;

    private String title;

    @Column(length = 1500)
    private String content;

    private LocalDateTime postDate;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private Set<String> postTags;

    private String postImage;

    private Boolean approved;

    @Enumerated(EnumType.STRING)
    private PostType state = PostType.PENDING;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
