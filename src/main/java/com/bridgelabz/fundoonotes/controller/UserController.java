package com.bridgelabz.fundoonotes.controller;

import com.bridgelabz.fundoonotes.dto.ForgotPasswordDTO;
import com.bridgelabz.fundoonotes.dto.LoginDTO;
import com.bridgelabz.fundoonotes.dto.RegistrationDTO;
import com.bridgelabz.fundoonotes.dto.ResetPasswordDTO;
import com.bridgelabz.fundoonotes.exception.UserException;
import com.bridgelabz.fundoonotes.response.Response;
import com.bridgelabz.fundoonotes.service.IUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class UserController {

    @Autowired
    IUserService iUserService;

    @ApiOperation("For registration")
    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid RegistrationDTO registrationDto) throws UserException {
        if (iUserService.registration(registrationDto))
            return new ResponseEntity<>(new Response(200,"user register successful"), HttpStatus.OK);
        return new ResponseEntity<>(new Response(400, "user register unsuccessful"), HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("For login")
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid LoginDTO loginDTO) throws UserException {
        String token = iUserService.login(loginDTO);
        if(token!=null) {
            return new ResponseEntity<>(new Response(200, "User login successful", token), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(400, "User login unsuccessful"), HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<Response> userVerification(@PathVariable("token") String token) throws UserException {

        if (iUserService.verify(token))
            return new ResponseEntity<>(new Response(200,"User verified successfully"), HttpStatus.OK);

        return new ResponseEntity<>(new Response(400,"User verification failed"), HttpStatus.NOT_ACCEPTABLE);
    }
    @PostMapping("/forgot/password")
    public ResponseEntity<Response> forgotPassword(@RequestBody @Valid ForgotPasswordDTO emailId) {

        Response response= iUserService.forgetPassword(emailId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/reset/password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPassword,
                                                  @RequestHeader String token) throws UserException {

        if (iUserService.resetPassword(resetPassword, token))
            return new ResponseEntity<>(new Response(200,"User password reset successful"), HttpStatus.OK);

        return new ResponseEntity<>(new Response(400,"User password reset unsuccessful"), HttpStatus.NOT_ACCEPTABLE);
    }

}
