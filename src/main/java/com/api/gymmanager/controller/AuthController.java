    package com.api.gymmanager.controller;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import com.api.gymmanager.dto.Auth.request.LoginRequest;
    import com.api.gymmanager.dto.Auth.request.RegisterRequest;
    import com.api.gymmanager.dto.Auth.response.LoginResponse;
    import com.api.gymmanager.service.AuthService;
    

    import org.springframework.web.bind.annotation.RequestBody;
    import lombok.RequiredArgsConstructor;

    @RestController 
    @RequestMapping ("/auth")
    @RequiredArgsConstructor 
    public class AuthController {

        private final AuthService authService;

        @PostMapping (value = "login")
        public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto){
            return ResponseEntity.ok(authService.login(dto));}

        @PostMapping(value = "register")
        public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest dto){
            return ResponseEntity.ok(authService.register(dto));}
    }