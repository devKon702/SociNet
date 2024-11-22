package com.example.authservice.service;

import com.example.authservice.dto.UserDto;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AccountDetail;
import com.example.authservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final FirebaseStorageService storageService;
    private final long MAX_IMAGE_SIZE = 3 * 1024 * 1024;
    public UserDto getUserInfo(){
        AccountDetail accountDetail = Helper.getAccountDetail();
        return new UserDto(accountDetail.getUser());
    }

    public List<UserDto> getUserInfos(){
        List<User> users = userRepo.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        users.forEach(user -> usersDto.add(new UserDto(user)));
        return usersDto;
    }

    public UserDto getUserInfoById(Long id) throws Exception {
        Optional<User> user = userRepo.findById(id);
        if(user.isPresent()){
            return new UserDto(user.get());
        } else{
            throw new Exception("USER NOT FOUND");
        }
    }

    public List<UserDto> getUsersByName(String name, Pageable pageable){
        List<User> users = userRepo.findByNameContaining(name, pageable);
        List<UserDto> userDto = new ArrayList<>();
        users.forEach(user -> {
            if(user.getId() != Helper.getUserId()) userDto.add(new UserDto(user));
        });
        return userDto;
    }

    public UserDto updateUserInfo(String name,
                                  String phone,
                                  String school,
                                  String address,
                                  Boolean isMale,
                                  MultipartFile avatar) throws Exception{
        AccountDetail accountDetail = Helper.getAccountDetail();
        User user = accountDetail.getUser();
        if(name != null) user.setName(name);
        if(phone != null) user.setPhone(phone);
        if(school != null) user.setSchool(school);
        if(address != null) user.setAddress(address);
        if(isMale != null) user.setMale(isMale);
        // Save avatar to firebase
        if(avatar != null){
            if(!avatar.getContentType().startsWith("image")) throw new Exception("UNSUPPORTED FILE");
            if(avatar.getSize() > MAX_IMAGE_SIZE) throw new Exception("OVERSIZE IMAGE");
            String avatarUrl = storageService.upload("images", avatar);
            user.setAvatarUrl(avatarUrl);
        }
        return new UserDto(userRepo.save(user));
    }


}
