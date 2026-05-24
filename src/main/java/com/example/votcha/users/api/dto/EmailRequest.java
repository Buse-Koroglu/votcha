package com.example.votcha.users.api.dto;

import jakarta.validation.constraints.Email;



public record EmailRequest (@Email String email){}
