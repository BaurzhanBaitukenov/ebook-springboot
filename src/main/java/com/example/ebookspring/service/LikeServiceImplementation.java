package com.example.ebookspring.service;

import com.example.ebookspring.exception.LikeException;
import com.example.ebookspring.exception.TwitException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Like;
import com.example.ebookspring.model.Twit;
import com.example.ebookspring.model.User;
import com.example.ebookspring.repository.LikeRepository;
import com.example.ebookspring.repository.TwitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImplementation implements LikeService {

    private LikeRepository likeRepository;
    private TwitService twitService;
    private TwitRepository twitRepository;

    public LikeServiceImplementation(
            LikeRepository likeRepository,
            TwitService twitService,
            TwitRepository twitRepository) {
        this.likeRepository=likeRepository;
        this.twitService=twitService;
        this.twitRepository=twitRepository;
    }

    @Override
    public Like likeTwit(Long twitId, User user) throws UserException, TwitException {

        Like isLikeExist=likeRepository.isLikeExist(user.getId(), twitId);

        if(isLikeExist!=null) {
            likeRepository.deleteById(isLikeExist.getId());
            return isLikeExist;
        }

        Twit twit=twitService.findById(twitId);
        Like like=new Like();
        like.setTwit(twit);
        like.setUser(user);

        Like savedLike=likeRepository.save(like);


        twit.getLikes().add(savedLike);
        twitRepository.save(twit);

        return savedLike;
    }

    @Override
    public Like unlikeTwit(Long twitId, User user) throws UserException, TwitException, LikeException {
        Like like=likeRepository.findById(twitId).orElseThrow(()->new LikeException("Like Not Found"));

        if(like.getUser().getId().equals(user.getId())) {
            throw new UserException("somthing went wrong...");
        }

        likeRepository.deleteById(like.getId());
        return like;
    }

    @Override
    public List<Like> getAllLikes(Long twitId) throws TwitException {
        Twit twit=twitService.findById(twitId);

        List<Like> likes=likeRepository.findByTwitId(twit.getId());
        return likes;
    }

}
