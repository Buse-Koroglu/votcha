package com.example.votcha.common.notification;

import com.example.votcha.users.api.dto.UsersRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")private String sender;
    @Value("${spring.mail.backend.url}") private String backendUrl;

    @Async
    public void sendEmail(UsersRequestDto request, String token){
        String verificationLink = backendUrl + "/api/auth/verify?token="+token;
        try{
            String text = """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <title>Account Verification</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f5; margin: 0; padding: 20px;">
                
                <div style="max-width: 600px; margin: auto; background: white; padding: 32px; border-radius: 16px; box-shadow: 0 2px 10px rgba(0,0,0,0.08);">
                    
                    <h1 style="color: #4F46E5; text-align: center;">
                        🚀 Welcome to  Votcha
                    </h1>

                    <p style="font-size: 16px; color: #333;">
                        Hello <strong>%s</strong>,
                    </p>

                    <p style="font-size: 16px; color: #555; line-height: 1.6;">
                        Welcome to the Votcha family! We are excited to have you on board.
                        Your account has been successfully created and is almost ready to use.
                    </p>

                    <p style="font-size: 16px; color: #555; line-height: 1.6;">
                        To activate your account, please verify your email address by clicking the button below.
                    </p>

                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="
                               background-color: #4F46E5;
                               color: white;
                               padding: 14px 28px;
                               text-decoration: none;
                               border-radius: 10px;
                               font-weight: bold;
                               display: inline-block;
                           ">
                            Verify My Account
                        </a>
                    </div>

                    <p style="font-size: 15px; color: #dc2626; line-height: 1.6;">
                        ⚠️ Important: This verification link will remain valid for only 5 minutes.
                        If your account is not verified within this period, it will be automatically deleted for security reasons.
                    </p>

                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;">

                    <p style="font-size: 14px; color: #777; text-align: center;">
                        If you did not create this account, you can safely ignore this email.
                    </p>

                    <p style="font-size: 14px; color: #777; text-align: center;">
                        © 2026 Votcha
                    </p>

                </div>

            </body>
        </html>
        """.formatted(request.firstName(), verificationLink);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(request.email());
            mimeMessageHelper.setSubject("Votcha Team | Email Verification");
            mimeMessageHelper.setText(text, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
