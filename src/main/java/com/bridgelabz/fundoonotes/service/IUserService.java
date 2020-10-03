package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.ForgotPasswordDTO;
import com.bridgelabz.fundoonotes.dto.LoginDTO;
import com.bridgelabz.fundoonotes.dto.RegistrationDTO;
import com.bridgelabz.fundoonotes.dto.ResetPasswordDTO;
import com.bridgelabz.fundoonotes.exception.UserException;
import com.bridgelabz.fundoonotes.response.Response;

public interface IUserService {

    boolean registration(RegistrationDTO registrationDTO)throws UserException;

    String login(LoginDTO loginDto) throws UserException;

    boolean verify(String token) throws UserException;

    Response forgetPassword(ForgotPasswordDTO emailId);

    boolean resetPassword(ResetPasswordDTO resetPassword, String token) throws UserException;


}
