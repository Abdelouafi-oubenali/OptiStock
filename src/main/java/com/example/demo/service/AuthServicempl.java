package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServicempl implements AuthService{

        @Autowired
        private UserRepository userRepository;

        public RuntimeException login(String email, String password) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.getPassword().equals(password);
                return new Appendable(true , "user" + user.get)
            }
            return false;
        }

    public RuntimeException register(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return new Appendable(false , RuntimeException("Email déjà utilisé !"));
        }

         if(userRepository.save(user) != null)
         {
            return new Appendable(true , RuntimeException("User CReate avec sucic"));

         };
    }

}
