package com.example.socinet.controller;

import com.example.socinet.dto.UserDto;
import com.example.socinet.service.UserService;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<?> getUserInfos(){
        List<UserDto> usersDto = userService.getUserInfos();
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
