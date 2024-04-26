package com.example.ebookspring.repository;

import com.example.ebookspring.model.Twit;
import com.example.ebookspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TwitRepository extends JpaRepository<Twit, Long> {

    List<Twit> findAllByIsTwitTrueOrderByCreatedAtDesc();
    List<Twit> findByRetwitUserContainsOrUser_IdAndIsTwitTrueOrderByCreatedAtDesc(User user, Long userId);
    List<Twit> findByLikesContainingOrderByCreatedAtDesc(User user);

    @Query("SELECT t FROM Twit t JOIN t.likes l WHERE l.user.id = :userId")
    List<Twit> findByLikesUser_Id(Long userId);

//    @Query("SELECT t FROM Twit t JOIN t.likes l WHERE l.user.id = :userId")
//    List<Twit> findTwitsByUserIdInLikes(Long userId);

//    List<Twit> findAllByIsTwitTrueOrderByCreatedAtDesc();
//
//    List<Twit> findByRetwitUserContainsOrUser_IdAndIsTwitTrueOrderByCreatedAtDesc(User user, Long userId);
//
//    List<Twit> findByLikesContainingOrderByCreatedAtDesc(User user);
//
//    @Query("select t from Twit t Join t.likes I where I.user.id=:userId")
//    List<Twit> findByLikesUser_Id(Long userId);
}
