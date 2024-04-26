package com.example.ebookspring.controller;

import com.example.ebookspring.config.JwtProvider;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Cart;
import com.example.ebookspring.model.User;
import com.example.ebookspring.model.Varification;
import com.example.ebookspring.repository.UserRepository;
import com.example.ebookspring.request.LoginRequest;
import com.example.ebookspring.response.AuthResponse;
import com.example.ebookspring.service.CartService;
import com.example.ebookspring.service.CustomUserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.CredentialException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private CustomUserServiceImplementation customUserService;
    private CartService cartService;

    private static final String GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID";

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          CustomUserServiceImplementation customUserService, JwtProvider jwtProvider, CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserService = customUserService;
        this.jwtProvider = jwtProvider;
        this.cartService = cartService;
    }

//    @PostMapping("/signin/google")
//    public ResponseEntity<AuthResponse> googleLogin(@RequestBody LoginWithGooleRequest req) throws GeneralSecurityException, IOException {
//
//        User user = validateGoogleIdToken(req);
//
//        String email = user.getEmail();
//        User existingUser = userRepository.findByEmail(email);
//
//        if (existingUser == null) {
//
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setImage(user.getImage());
//            newUser.setFullName(user.getFullName());
//            newUser.setLogin_with_google(true);
//            newUser.setPassword(user.getPassword());
//            newUser.setVerification(new Varification());
//
//            userRepository.save(newUser);
//        }
//
////	        System.out.println("email ---- "+ existingUser.getEmail()+" jwt - ");
//
//        Authentication authentication =  new UsernamePasswordAuthenticationToken(email, user.getPassword());
//
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String token = jwtProvider.generateToken(authentication);
//
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setStatus(true);
//        authResponse.setJwt(token);
//
////	        System.out.println("email ---- "+ existingUser.getEmail()+" jwt - "+token);
//
//        return new ResponseEntity<>(authResponse, HttpStatus.OK);
//    }
//
//
//    private User validateGoogleIdToken(LoginWithGooleRequest req) throws GeneralSecurityException, IOException {
//        HttpTransport transport = new NetHttpTransport();
//        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                .setAudience(Collections.singletonList(req.getClientId()))
//                .build();
//
//        GoogleIdToken token = verifier.verify(req.getCredential());
//        if (req.getCredential() != null) {
//
//            Payload payload = token.getPayload();
//            String userId = payload.getSubject();
//
//            System.out.println("User ID: " + userId);
//
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
//
//            User user=new User();
//            user.setImage(pictureUrl);
//            user.setEmail(email);
//            user.setPassword(userId);
//
//            System.out.println("image url - -  "+pictureUrl);
//
//            return user;
//
//        } else {
//            throw new CredentialException("invalid id token...");
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {

        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String birthDate = user.getBirthDate();
        String role=user.getRole();

        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {
            throw new UserException("Email is Already Used With Another Account");
        }


        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setFirstName(firstName);
        createdUser.setLastName(lastName);
        createdUser.setBirthDate(birthDate);
        createdUser.setVerification(new Varification());
        createdUser.setRole(role);

        User savedUser = userRepository.save(createdUser);
        Cart cart = cartService.createCart(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Success");
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signin Success");
        authResponse.setStatus(true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }


    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username...");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Username or Password...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
