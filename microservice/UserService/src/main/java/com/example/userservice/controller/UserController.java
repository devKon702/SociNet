package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<?> getUserInfo(){
        UserDto userDto = userService.getUserInfo();
        return Helper.returnSuccessResponse("Get user information success", userDto);
    }

    @GetMapping("")
    public ResponseEntity<?> getUserInfos(@RequestParam(defaultValue = "") String name, Pageable pageable){
        List<UserDto> usersDto = userService.getUsersByName(name, pageable);
        return Helper.returnSuccessResponse("Get user list success", usersDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserInfoById(@PathVariable Long id) throws Exception{
        UserDto userDto = userService.getUserInfoById(id);
        return Helper.returnSuccessResponse("Get user information success", userDto);
    }

    @PutMapping("me")
    public ResponseEntity<?> updateUserInfo(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String phone,
                                            @RequestParam(required = false) String school,
                                            @RequestParam(required = false) String address,
                                            @RequestParam(required = false) Boolean isMale,
                                            @RequestParam(required = false) MultipartFile avatar) throws Exception{
        UserDto userDto = userService.updateUserInfo(name, phone, school, address, isMale, avatar);
        return Helper.returnSuccessResponse("Update user information success", userDto);
    }
}
