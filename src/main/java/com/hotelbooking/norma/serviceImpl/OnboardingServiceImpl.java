package com.hotelbooking.norma.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
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
import com.hotelbooking.norma.dto.response.AuthResponse;

import com.hotelbooking.norma.email.EmailService;
import com.hotelbooking.norma.email.dto.SendEmailRequest;
import com.hotelbooking.norma.entity.OTP;
import com.hotelbooking.norma.entity.Role;
import com.hotelbooking.norma.entity.User;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.repository.OtpRepository;
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
    private final OtpRepository otpRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    
    @Override
    public ResponseModel register(RegisterUserRequestDto dto) {
        String email = dto.getEmail();
        String name = dto.getName();

        Optional<User> userExists = userRepository.findByEmail(dto.getEmail());
        if (userExists.isPresent()) {
            User user = userExists.get();

                if(!user.isEmailVerified()){
                    resendOtp(email);
                }
                throw new GlobalRequestException(String.format(Message.ALREADY_EXISTS, "Email"), HttpStatus.BAD_REQUEST);
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

        String otpToken = otpService.generateOtp();
        otpService.saveOtp(newUser.getUserCode(), otpToken);

        Map<String, String> placeholders = Map.of(
            "username", name,
            "otpcode", otpToken,
            "account_name", "Norma Hotel"
        );

        SendEmailRequest emailRequest = new SendEmailRequest(
                email,
                "Welcome to Norma Hotel - OTP Inside!",
                "registration",
                placeholders);

        // Send email
        emailService.sendTemplatedEmail(emailRequest);
        log.info("Registration welcome mail processing for: {}",email);
        return new ResponseModel(HttpStatus.CREATED.value(), String.format("User Registered Successfully, check email for OTP code for validation."), newUser);
            
    }

    @Override
    public ResponseModel login(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        if (!user.isEmailVerified()) {
                resendOtp(user.getEmail());

                // Return custom response indicating email not verified
                return new ResponseModel(
                    HttpStatus.UNAUTHORIZED.value(),
                    String.format("Email is not verified. OTP has been resent. Please verify your account."));
        }

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        AuthResponse authResponse = new AuthResponse(user.getId(), user.getUserCode(), user.getEmail(), jwt);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_VALIDATE, "User"), authResponse);
    }

    @Override
    public ResponseModel verifyOtp(OtpTokenValidatorDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));
        OtpValidationResult result = otpService.validateOtp(user.getUserCode(), dto.getOtpCode());
        
        if(result.isSuccess()){
            user.setEmailVerified(true);
            userRepository.save(user);
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_VALIDATE, "OTP"), null);
        }else {
            log.error("OTP validation failed for: {}", user.getEmail());
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), result.getMessage(), null);
        }
    }

    @Override
    public ResponseModel resendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND)); 
        String otpToken = otpService.generateOtp();
        otpService.saveOtp(user.getUserCode(), otpToken);
        String name = user.getName();

        Map<String, String> placeholders = Map.of(
                "username", name,
                "otpcode", otpToken,
                "account_name", "Norma Hotel"
            );

        SendEmailRequest emailRequest = new SendEmailRequest(
                email,
                "Norma Hotel - OTP!",
                "resendOtp",
                placeholders);
        emailService.sendTemplatedEmail(emailRequest);
        log.info("Triggered email sending for: {}", user.getEmail());

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_SENT, "OTP"), null);     
    }

    @Override
    public ResponseModel changePassword(UpdatePasswordDto dto) {
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

    @Override
    public ResponseModel forgotPassword(String email) {
        try{
            User user = userRepository.findByEmail(email).orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND)); 
            String name = user.getName();
            String otpToken = otpService.requestOtp(user.getUserCode());
            Map<String, String> placeholders = Map.of(
                "username", name,
                "otpcode", otpToken,
                "account_name", "Norma Hotel"
            );

            SendEmailRequest emailRequest = new SendEmailRequest(
                email,
                "Norma Hotel - OTP!",
                "resendOtp",
                placeholders);
            emailService.sendTemplatedEmail(emailRequest);
            log.info("Forgot password mail processing for: {}", user.getEmail());
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_SENT, "OTP"), null);
        } catch (GlobalRequestException e) {
            log.error(String.format(Message.OPERATION_FAILURE, e.getMessage()));
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch(Exception e){
            log.error(String.format(Message.OPERATION_FAILURE, e.getMessage()));
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @Override
    public ResponseModel resetPassword(String email, String otpCode, String newPassword) {
        try{
            User user = userRepository.findByEmail(email).orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));
            OTP otpToken = otpRepository.findByUserCodeAndOtpCode(user.getUserCode(), otpCode)
            .orElseThrow(() -> new GlobalRequestException("Invalid or expired OTP", HttpStatus.BAD_REQUEST));
            
            if (otpToken.isUsed()) {
                throw new GlobalRequestException("OTP has already been used", HttpStatus.BAD_REQUEST);
            }

            if (otpToken.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new GlobalRequestException("OTP has expired", HttpStatus.BAD_REQUEST);
            }
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            otpToken.setUsed(true);
            otpRepository.save(otpToken);

            log.info("Reset password processing for: {}", user.getEmail());
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "User Password"), null);
        } catch (GlobalRequestException e) {
            log.error(String.format(Message.OPERATION_FAILURE, e.getMessage()));
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch(Exception e){
            log.error(String.format(Message.OPERATION_FAILURE, e.getMessage()));
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

}
