package com.example.userservice.controller;

import com.example.userservice.dto.FriendInvitationDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.FriendService;
import com.example.userservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @GetMapping("invitation")
    ResponseEntity<?> getInvitationList(){
        List<FriendInvitationDto> invitations = friendService.getInvitations();
        return Helper.returnSuccessResponse("Get invitations success", invitations);
    }

    @GetMapping("{userId}")
    ResponseEntity<?> getFriendList(@PathVariable Long userId) throws Exception{
        List<UserDto> friendList = friendService.getFriendList(userId);
        return Helper.returnSuccessResponse("Get friends success", friendList);
    }



    @PostMapping("{userId}")
    ResponseEntity<?> makeFriendInvitation(@PathVariable Long userId) throws Exception{
        FriendInvitationDto friendInvitationDto = friendService.makeFriendInvitation(userId);
        return Helper.returnSuccessResponse("Create invitation success", friendInvitationDto);
    }

    @PutMapping("{id}")
    ResponseEntity<?> responseInvitation(@PathVariable Long id, @RequestParam boolean isAccept) throws Exception{
        FriendInvitationDto friendInvitationDto = friendService.responseFriendInvitation(id, isAccept);
        return Helper.returnSuccessResponse("Response invitation success", friendInvitationDto);
    }

    @GetMapping("check/{userId}")
    ResponseEntity<?> checkIsFriend(@PathVariable Long userId) {
        String status = friendService.checkIsFriend(userId);
        return Helper.returnSuccessResponse("Check friend success", status);
    }

    @GetMapping("suggestion")
    ResponseEntity<?> suggestFriend(@RequestParam(value = "size", defaultValue = "10") int size){
        List<UserDto> suggestionList = friendService.recommendFriend(size);
        return Helper.returnSuccessResponse("Get suggestion success", suggestionList);
    }
}
