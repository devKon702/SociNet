package com.example.socinet.controller;

import com.example.socinet.dto.FriendInvitationDto;
import com.example.socinet.dto.UserDto;
import com.example.socinet.service.FriendService;
import com.example.socinet.util.Helper;
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
}
