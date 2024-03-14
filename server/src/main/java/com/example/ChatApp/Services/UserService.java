package com.example.ChatApp.Services;

import java.util.Date;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;*/
/*import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;*/
import org.springframework.stereotype.Service;

import com.example.ChatApp.Models.Users;
import com.example.ChatApp.Repositories.UsersRepository;
import com.example.ChatApp.dto.IdDto;
import com.example.ChatApp.dto.SignInDto;
import com.example.ChatApp.dto.SignUpDto;

@Service
public class UserService
{
	@Autowired
    private UsersRepository usersRepository;
	/*
	 * @Autowired private PasswordEncoder passwordEncoder;
	 */

	/*
	 * public Optional<Users> getUserByTag(String Tag){ return
	 * usersRepository.findByTag(Tag);
	 * 
	 * }
	 */
    public Users signup(SignUpDto signUpRequest){
    	if(!usersRepository.findTag(signUpRequest.Tag).isEmpty()) {
    		return null;
    	}
        Users users=new Users();
        users.Email = signUpRequest.Email;
        users.Display_name = signUpRequest.Display_Name;
        users.Password = signUpRequest.Password;
        users.Birth = signUpRequest.Birth;
        users.Created_day = new Date();
        users.Tag = signUpRequest.Tag;
        
        return usersRepository.save(users);
    }
    public Optional<IdDto> signin(SignInDto signInDto) {
    	System.out.println(signInDto.Account_name);
    	Optional<IdDto> user_id = usersRepository.authLogin(signInDto.Account_name, signInDto.Password);
    	return user_id;
    }
}

