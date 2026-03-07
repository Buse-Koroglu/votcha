package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Options", description = "Option Management APIs")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }


    @PostMapping("/events/{id}/options")
    @ResponseStatus(HttpStatus.CREATED)
    public OptionResponseDto createOption(@AuthenticationPrincipal Users currentUser,
                                          @Valid @RequestBody OptionRequestDto request,
                                          @PathVariable(name = "id") String eventId){
        return optionService.createOption(request, eventId, currentUser.getId());
    }


    @GetMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OptionResponseDto getOption(@AuthenticationPrincipal Users user, @PathVariable String id){
        return optionService.getUserOption(user, id);
    }

    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OptionResponseDto deleteOption(@AuthenticationPrincipal Users user, @PathVariable String id){
        return optionService.deleteUserOption(user, id);
    }

    @PatchMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OptionResponseDto updateOption(@AuthenticationPrincipal Users user,
                                          @Valid @RequestBody UpdateOptionRequestDto request,
                                          @PathVariable(name = "id") String id){
        return optionService.patchUserOption(user, id, request);
    }
}
