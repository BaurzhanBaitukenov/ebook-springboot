package com.example.ebookspring.controller;

import com.example.ebookspring.dto.LikeDto;
import com.example.ebookspring.exception.LikeException;
import com.example.ebookspring.exception.TwitException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.mapper.LikeDtoMapper;
import com.example.ebookspring.model.Like;
import com.example.ebookspring.model.User;
import com.example.ebookspring.service.LikeService;
import com.example.ebookspring.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name="Like-Unlike Twit")
public class LikeController {

    private UserService userService;
    private LikeService likeService;

    public LikeController(UserService userService,LikeService likeService) {
        this.userService=userService;
        this.likeService=likeService;
    }

    @PostMapping("/{twitId}/likes")
    public ResponseEntity<LikeDto>likeTwit(
            @PathVariable Long twitId,
            @RequestHeader("Authorization") String jwt) throws UserException, TwitException{

        User user=userService.findUserProfileByJwt(jwt);
        Like like =likeService.likeTwit(twitId, user);

        LikeDto likeDto=LikeDtoMapper.toLikeDto(like,user);

        return new ResponseEntity<>(likeDto,HttpStatus.CREATED);
    }
    @DeleteMapping("/{twitId}/unlike")
    public ResponseEntity<LikeDto>unlikeTwit(
            @PathVariable Long twitId,
            @RequestHeader("Authorization") String jwt) throws UserException, TwitException, LikeException {

        User user=userService.findUserProfileByJwt(jwt);
        Like like =likeService.unlikeTwit(twitId, user);


        LikeDto likeDto=LikeDtoMapper.toLikeDto(like,user);
        return new ResponseEntity<>(likeDto,HttpStatus.CREATED);
    }

    @GetMapping("/twit/{twitId}")
    public ResponseEntity<List<LikeDto>>getAllLike(
            @PathVariable Long twitId,@RequestHeader("Authorization") String jwt) throws UserException, TwitException{
        User user=userService.findUserProfileByJwt(jwt);

        List<Like> likes =likeService.getAllLikes(twitId);

        List<LikeDto> likeDtos=LikeDtoMapper.toLikeDtos(likes,user);

        return new ResponseEntity<>(likeDtos,HttpStatus.CREATED);
    }


}
