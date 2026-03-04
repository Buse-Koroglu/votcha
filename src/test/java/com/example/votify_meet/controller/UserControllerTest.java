package com.example.votify_meet.controller;

import com.example.votify_meet.users.api.controller.UsersController;
import com.example.votify_meet.users.api.dto.UpdateUsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersRequestDto;
import com.example.votify_meet.users.api.dto.UsersResponseDto;
import com.example.votify_meet.users.api.mapper.UsersMapper;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.users.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private UsersService usersService;
    @MockitoBean
    private UsersMapper usersMapper;

    @Test
    void createUser_returns201_whenValidRequest() throws Exception{
        UsersRequestDto request = new UsersRequestDto("Jack","London","jack@gmail.com","145*9o");
        UsersResponseDto response = new UsersResponseDto("1","Jack","London","jack@gmail.com",null,null);

        when(usersMapper.toEntity(any(UsersRequestDto.class))).thenReturn(new Users());
        when(usersService.createUser(any(Users.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("London"))
                .andExpect(jsonPath("$.email").value("jack@gmail.com"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

    }

    @Test
    void createUser_returns400_whenInvalidRequest() throws Exception{
        UsersRequestDto request = new UsersRequestDto("Jack","","jack@gmail.com","");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_returns200_whenUserExist() throws Exception{
        UsersResponseDto response = new UsersResponseDto("1","Jack","London","jack@gmail.com",null,null);
        when(usersService.getUser("1")).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Jack"));
    }

    @Test
    void getUser_returns404_whenUserNotExist() throws  Exception{
       when(usersService.getUser("2")).thenThrow(new UserNotFoundException("User Not Found"));

       mockMvc.perform(get("/api/users/{id}","2"))
               .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_returns200_whenUserDeleted() throws Exception{
        UsersResponseDto response = new UsersResponseDto("1","Jack","London","jack@gmail.com",null,null);
        when(usersService.deleteUser("1")).thenReturn(response);

        mockMvc.perform(delete("/api/users/{id}","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deleteUser_returns404_whenUserNotFound() throws Exception{
        when(usersService.deleteUser("1")).thenThrow(new UserNotFoundException("User Not Found"));

        mockMvc.perform(delete("/api/users/{id}","1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchUser_returns200_whenUserPatched() throws Exception{
        UpdateUsersRequestDto request = new UpdateUsersRequestDto("Jack","London","jack@gmail.com","145*9o");
        UsersResponseDto response = new UsersResponseDto("1","Jack","London","jack@gmail.com",null,null);

        when(usersService.patchUser("1",request)).thenReturn(response);
        mockMvc.perform(patch("/api/users/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("London"))
                .andExpect(jsonPath("$.email").value("jack@gmail.com"))
                .andExpect(jsonPath("$.createdAt").value(nullValue()))
                .andExpect(jsonPath("$.updatedAt").value(nullValue()));

    }

    @Test
    void patchUser_returns400_whenInvalidRequest() throws Exception{
        UpdateUsersRequestDto request = new UpdateUsersRequestDto("","","","");

        mockMvc.perform(patch("/api/users/{id}","1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
