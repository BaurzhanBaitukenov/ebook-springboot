package com.example.ebookspring.service;

import com.example.ebookspring.exception.TwitException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Like;
import com.example.ebookspring.model.User;

import java.util.List;

public interface LikeService {

    public Like likeTwit(Long twitId, User user) throws UserException, TwitException;
    public List<Like> getAllLikes(Long twitId) throws TwitException;
}
