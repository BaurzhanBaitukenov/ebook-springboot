package com.example.ebookspring.controller;

import com.example.ebookspring.dto.LikeDto;
import com.example.ebookspring.exception.TwitException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.mapper.LikeDtoMapper;
import com.example.ebookspring.model.Like;
import com.example.ebookspring.model.User;
import com.example.ebookspring.service.LikeService;
import com.example.ebookspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communication")
public class LikeController {

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @PostMapping("/{twitId}/likes")
    public ResponseEntity<LikeDto> likeTwit(@PathVariable Long twitId,
                                            @RequestHeader("Authorization") String jwt) throws UserException, TwitException {

        User user = userService.findUserProfileByJwt(jwt);
        Like like = likeService.likeTwit(twitId, user);

        LikeDto likeDto = LikeDtoMapper.toLikeDto(like, user);

        return new ResponseEntity<LikeDto>(likeDto, HttpStatus.CREATED);
    }


    @PostMapping("/twit/{twitId}")
    public ResponseEntity<List<LikeDto>> getAllLikes(@PathVariable Long twitId,
                                                     @RequestHeader("Authorization") String jwt) throws UserException, TwitException {

        User user = userService.findUserProfileByJwt(jwt);
        List<Like> likes = likeService.getAllLikes(twitId);

        List<LikeDto> likeDtos = LikeDtoMapper.toLikeDtos(likes, user);

        return new ResponseEntity<>(likeDtos, HttpStatus.CREATED);
    }
}
