package isuruygor.demo.repositories;

import isuruygor.demo.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {


    @Query(name = "SELECT p FROM Post p WHERE p.category= :category")
    List<Post> getPostsByCategory(@Param("category") String category);
}
