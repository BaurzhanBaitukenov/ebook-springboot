package com.example.ebookspring.repository;

import com.example.ebookspring.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

//    @Query("SELECT I FROM Like I WHERE I.user.id=:userId AND I.twit.id=:twitId")
//    public Like isLikeExist(@Param("userId") Long userId, @Param("twitId") Long twitId);
//
//    @Query("select I from Like I where I.twit.id=:twitId")
//    public List<Like> findByTwitId(@Param("twitId") Long twitId);

    @Query("SELECT l From Like l Where l.user.id=:userId AND l.twit.id=:twitId")
    public Like isLikeExist(@Param("userId") Long userId, @Param("twitId") Long twitId);

    @Query("SELECT l From Like l Where l.twit.id=:twitId")
    public List<Like> findByTwitId(@Param("twitId") Long twitId);

}
