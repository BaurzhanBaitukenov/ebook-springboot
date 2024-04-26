package com.example.ebookspring.controller;

import com.example.ebookspring.dto.UserDto;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.mapper.UserDtoMapper;
import com.example.ebookspring.model.User;
import com.example.ebookspring.service.UserService;
import com.example.ebookspring.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfileHandler(@RequestHeader("Authorization") String jwt)
            throws UserException{

        User user=userService.findUserProfileByJwt(jwt);
        user.setPassword(null);
        user.setReq_user(true);
        UserDto userDto=UserDtoMapper.toUserDto(user);
        userDto.setReq_user(true);
        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId,
            @RequestHeader("Authorization") String jwt) throws UserException {

        User reqUser=userService.findUserProfileByJwt(jwt);

        User user = userService.findUserById(userId);

        UserDto userDto = UserDtoMapper.toUserDto(user);
        userDto.setReq_user(UserUtil.isReqUser(reqUser, user));
        userDto.setFollowed(UserUtil.isFollowedByReqUser(reqUser, user));
        return new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED);
    }


    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUserHandler(@RequestParam String query,
                                                           @RequestHeader("Authorization") String jwt)
            throws UserException{

        User reqUser=userService.findUserProfileByJwt(jwt);

        List<User> users=userService.searchUser(query);

//		user.setReq_user(UserUtil.isReqUser(reqUser, user));

        List<UserDto> userDtos=UserDtoMapper.toUserDtos(users);

        return new ResponseEntity<>(userDtos,HttpStatus.ACCEPTED);
    }


    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUserHandler(@RequestBody User req,
                                                     @RequestHeader("Authorization") String jwt)
            throws UserException{

        System.out.println("update user  "+req);
        User user=userService.findUserProfileByJwt(jwt);

        User updatedUser=userService.updateUser(user.getId(), req);
        updatedUser.setPassword(null);
        UserDto userDto=UserDtoMapper.toUserDto(user);
        userDto.setReq_user(true);
        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }


    @PutMapping("/{userId}/follow")
    public ResponseEntity<UserDto> followUserHandler(@PathVariable Long userId, @RequestHeader("Authorization") String jwt)
            throws UserException{

        User user=userService.findUserProfileByJwt(jwt);

        User updatedUser=userService.followUser(userId, user);
        UserDto userDto=UserDtoMapper.toUserDto(updatedUser);
        userDto.setFollowed(UserUtil.isFollowedByReqUser(user, updatedUser));
        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }

}
