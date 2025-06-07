package com.hotelbooking.norma.serviceImpl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.LoginRequestDto;
import com.hotelbooking.norma.dto.Request.RegisterUserRequestDto;
import com.hotelbooking.norma.dto.Request.UpdatePasswordDto;
import com.hotelbooking.norma.dto.otp.OtpTokenValidatorDto;
import com.hotelbooking.norma.dto.otp.OtpValidationResult;
import com.hotelbooking.norma.dto.response.LoginResponseDto;
import com.hotelbooking.norma.email.EmailService;
import com.hotelbooking.norma.email.dto.EmailMessageDto;
import com.hotelbooking.norma.entity.Role;
import com.hotelbooking.norma.entity.User;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.repository.RoleRepository;
import com.hotelbooking.norma.repository.UserRepository;
import com.hotelbooking.norma.security.jwt.JwtUtils;
import com.hotelbooking.norma.service.OnboardingService;
import com.hotelbooking.norma.service.OtpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final OtpService otpService;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    
    @Override
    public ResponseModel registerUser(RegisterUserRequestDto dto) {
        Optional<User> userExists = userRepository.findByEmail(dto.getEmail());

            if (userExists.isPresent()) {
                return new ResponseModel(HttpStatus.CONFLICT.value(),String.format(Message.ALREADY_EXISTS, "User"),null);
            }
            
            User newUser = modelMapper.map(dto, User.class);
            newUser.setUserCode("USER-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
            newUser.setEmail(dto.getEmail());
            newUser.setName(dto.getName());
            newUser.setPassword(encoder.encode(dto.getPassword()));

            Optional<Role> clientRole = this.roleRepository.findById(1L);
            if (clientRole.isEmpty()) {
                return new ResponseModel(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format(Message.INVALID_ID, "Role"),
                    null
                );
            }
    
            Set<Role> roles = new HashSet<>();
            roles.add(clientRole.get());
            newUser.setRoles(roles);
            
            userRepository.save(newUser);

            String OTPToken = otpService.generateOtp();
            otpService.saveOtp(newUser.getUserCode(), OTPToken);

            String subject = "Your OTP Code";
            String body = String.format("Hello %s,\n\nYour OTP code is: %s\n\nThanks,\nTeam", dto.getName(), OTPToken);

            // Send email
            emailService.sendEmail(new EmailMessageDto(newUser.getEmail(), subject, body));

            return new ResponseModel(HttpStatus.CREATED.value(), String.format(Message.SUCCESS_CREATE, "User"), newUser);
            
    }

    @Override
    public ResponseModel loginUser(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        LoginResponseDto responseDto = new LoginResponseDto(user.getId(), user.getEmail(), jwt);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_VALIDATE, "User"), responseDto);
    }

    @Override
    public ResponseModel verifyOtpCode(OtpTokenValidatorDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));
        OtpValidationResult result = otpService.validateOtp(user.getUserCode(), dto.getOtpCode());
        
        if(result.isSuccess()){
            user.setValidated(true);
            userRepository.save(user);

            String subject = "Your Account has been verified";
            String body = String.format("Hello %s,\n\nYour Account has been verified.\n\nThanks,\nTeam", user.getName());

            emailService.sendEmail(new EmailMessageDto(user.getEmail(), subject, body));

            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_VALIDATE, "User"), null);
        }

        return new ResponseModel(HttpStatus.BAD_REQUEST.value(), result.getMessage(), null);
    }

    @Override
    public ResponseModel resendOtpCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND)); 
        String OTPToken = otpService.generateOtp();
        otpService.saveOtp(user.getUserCode(), OTPToken);

        String subject = "Your OTP Code";
        String body = String.format("Hello %s,\n\nYour OTP code is: %s\n\nThanks,\nTeam", user.getName(), OTPToken);

        emailService.sendEmail(new EmailMessageDto(user.getEmail(), subject, body));

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_VALIDATE, "User"), null);     
    }

    @Override
    public ResponseModel updatePassword(UpdatePasswordDto dto) {
        User user = userRepository.findByUserCode(dto.getUserCode()).orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        if (!encoder.matches(oldPassword, user.getPassword())) {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), String.format(Message.INVALID_PASSWORD, "Password"), null);
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "Password"), null);
    }
}
