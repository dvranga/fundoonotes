package com.bridgelabz.fundoonotes.service;

import com.bridgelabz.fundoonotes.dto.ForgotPasswordDTO;
import com.bridgelabz.fundoonotes.dto.LoginDTO;
import com.bridgelabz.fundoonotes.dto.RegistrationDTO;
import com.bridgelabz.fundoonotes.dto.ResetPasswordDTO;
import com.bridgelabz.fundoonotes.exception.UserException;
import com.bridgelabz.fundoonotes.model.UserModel;
import com.bridgelabz.fundoonotes.repository.UserRepository;
import com.bridgelabz.fundoonotes.response.EmailObject;
import com.bridgelabz.fundoonotes.response.Response;
import com.bridgelabz.fundoonotes.utility.JwtGenerator;
import com.bridgelabz.fundoonotes.utility.RabbitMQSender;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RabbitMQSender rabbitMQSender;


    private static final String VERIFICATION_URL = "http://localhost:8080/user/verify/";
    private static final String RESETPASSWORD_URL = "http://localhost:8080/user/resetpassword?token=";


    @Override
    public boolean registration(RegistrationDTO registrationDto) {
        System.out.println(registrationDto+" registrationDto");
        Optional<UserModel> isEmailAvailable = userRepository.findByEmail(registrationDto.getEmailId());
        if (isEmailAvailable.isPresent()) {
            return false;
        }
        UserModel userDetails = new UserModel();
        BeanUtils.copyProperties(registrationDto, userDetails);
        userDetails.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        userRepository.save(userDetails);
        String response= getResponse(userDetails.getUserId());
        if (rabbitMQSender.send(new EmailObject(registrationDto.getEmailId(), "Registration Link...", response)))
            return true;
        return false;
    }

    private String getResponse(long userId) {
        String response="\t\t\t\t\t\tThanking you for Registration with us\n\n"+"Click on the below link for the verification\n\n"
                +VERIFICATION_URL + JwtGenerator.createJWT(userId);
        return  response;
    }

    @Override
    public String login(LoginDTO loginDto) throws UserException {
        System.out.println(loginDto+" loginDto");
        Optional<UserModel> userCheck = userRepository.findByEmail(loginDto.getEmailId());
        if (!userCheck.isPresent()) {
            throw new UserException("No user found", UserException.ExceptionType.INVALID_USER);
        }
        if (bCryptPasswordEncoder.matches(loginDto.getPassword(), userCheck.get().getPassword())) {
            String token = JwtGenerator.createJWT(userCheck.get().getUserId());
            userRepository.save(userCheck.get());
            return token;
        }
        throw  new UserException("Incorrect credentials", UserException.ExceptionType.INVALID_CREDENTIALS);
    }

    @Override
    public boolean verify(String token) throws UserException {
        long id = JwtGenerator.decodeJWT(token);
        UserModel userInfo = userRepository.findById(id).get();
        if (id > 0 && userInfo != null) {
            if (!userInfo.isVerify()) {
                userInfo.setVerify(true);
                userRepository.save(userInfo);
                return true;
            }
            throw new UserException("User already verified", UserException.ExceptionType.ALREADY_VERFIED);
        }
        return false;
    }

    @Override
    public Response forgetPassword(ForgotPasswordDTO userMail) {
        UserModel userModel = userRepository.findByEmail(userMail.getEmailId()).get();
        System.out.println(userModel+" forgotPassword emailId");
        if (userModel != null && userModel.isVerify()) {
            String token = JwtGenerator.createJWT(userModel.getUserId());
            String response = RESETPASSWORD_URL + token;
            if (rabbitMQSender.send(new EmailObject(userModel.getEmailId(), "ResetPassword Link...", response)))
                return new Response(HttpStatus.OK.value(), "ResetPassword link Successfully", token);
        }
        return new Response(HttpStatus.OK.value(), "Email sending failed");
    }

    @Override
    public boolean resetPassword(ResetPasswordDTO resetPassword, String token) throws UserException {
        if (resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {
            long id = JwtGenerator.decodeJWT(token);
            UserModel isIdAvailable = userRepository.findById(id).get();
            if (isIdAvailable != null) {
                isIdAvailable.setPassword(bCryptPasswordEncoder.encode((resetPassword.getNewPassword())));
                userRepository.save(isIdAvailable);
                return true;
            }
            throw new UserException("User not exist", UserException.ExceptionType.INVALID_USER);
        }
        return false;
    }
}
