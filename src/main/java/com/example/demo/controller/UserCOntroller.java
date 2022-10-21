package com.example.demo.controller;

import com.example.demo.models.Message;
import com.example.demo.models.Url;
import com.example.demo.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
//import com.nimbusds.jose.util.StandardCharset;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@Slf4j
public class UserCOntroller {

    @Autowired
    UrlService urlService;
    @Autowired
    JavaMailSender javaMailSender;
    @GetMapping("/")
    public  String greeting() {
        return "hello user wellcome";
    }

    public static boolean isValid(String url){
        try{
            new URL(url).toURI();
            return true;
        }catch(Exception exception){
            log.error("error {}", exception.getMessage());
            return false;
        }
    }
    @GetMapping("/shortUrl/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletRequest request , HttpServletResponse response) throws IOException {
        Map<String, String> messages = new HashMap<>();
            String  link ="https://vshortly.herokuapp.com" + request.getServletPath();
//            log.info("link  {}", link);
//            log.info("link {}", link);
            Url currentUrl = urlService.getUrlByHashedUrl(link);
            if(currentUrl == null){
                messages.put("message", currentUrl.getHashedUrl());
                new ObjectMapper().writeValue(response.getOutputStream(), messages);
                log.info("urlll ");
            }else{
                log.info("urllll {}", currentUrl.getOriginalUrl());
                response.sendRedirect(currentUrl.getOriginalUrl());
            }
    }

    @PostMapping("/url")
    public  Url processUrl(@RequestBody Url url, HttpServletResponse response, HttpServletRequest request) throws IOException{
        Map<String, String> messages = new HashMap<>();
        try{
            if(url.getOriginalUrl() == null || url.getOriginalUrl() == "" || UserCOntroller.isValid(url.getOriginalUrl()) == false){
                response.setStatus(400);
                messages.put("message", "invalid url or it is  empty");
                new ObjectMapper().writeValue(response.getOutputStream(), messages);
                return null;
            }else{
                String link = "https://vshortly.herokuapp.com/shortUrl/";
                LocalDateTime time = LocalDateTime.now();
//                UUID uuid

                url.setHashedUrl( link.concat(Hashing.goodFastHash(1).hashString(url.getOriginalUrl().substring(url.getOriginalUrl().length()-2).concat(time.toString()), StandardCharsets.UTF_8).toString()).substring(8));
                response.setStatus(200);
                return  urlService.registerUrl(url);
            }
        }catch(Exception exception){
            log.info("Error ", exception.getMessage());
            return null;
        }
    }
    @PostMapping("/send/{email}")
    public String sendMessage(@PathVariable String email, @RequestBody Message message) throws MessagingException, UnsupportedEncodingException {
        log.info("email {}", email);
        try{
            String[] inputs = { email, message.getName(), message.getMessage() };
            for( int i = 0; i < inputs.length; i++){
                if(inputs[i] == null || inputs[i] == ""){
                    return "Invalid inputs please fill out all the fields";
                }
            }

            String to = email;
            String from = "uwavalens2003@gmail.com";
            String content = message.getMessage();
            MimeMessage message1 = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message1);
            mimeMessageHelper.setFrom(from, "valens niyonsenga");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setSubject("Idea on shortly");
            javaMailSender.send(message1);
        }catch(Exception exception){
            log.error("error {}", exception.getMessage());
        }

        return "message sent successfully";
    }

}
