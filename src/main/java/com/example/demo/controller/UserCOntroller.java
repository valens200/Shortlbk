package com.example.demo.controller;

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

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
@Slf4j
public class UserCOntroller {
    @Autowired
     JavaMailSender javaMailSender;

    @Autowired
    UrlService urlService;
    @GetMapping("/")

    public  String greeting() {
        return "hello valens";
    }


    @PostMapping("/sendMeEmail/{email}")

    public String sendEMail(@PathVariable String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> messages = new HashMap<>();

        if(email == null || email == ""){
            messages.put("message ", "please enter an email ");
            new ObjectMapper().writeValue(response.getOutputStream(), messages);
            return "invalid inputs";
        }else{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("uwavalens2003@gmail.com");
            message.setTo(email);
            message.setText("<html><body><h1>" +
                    "hello world</h1></body></html>");
            message.setSubject("getting started with springboot");
            javaMailSender.send(message);
            return "message sent successfull";
        }
    }
    @GetMapping("/shortUrl/{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletRequest request , HttpServletResponse response) throws IOException {
        log.info("url {}", request.getServletPath());
        Map<String, String> messages = new HashMap<>();
            String  link ="http://localhost:8080" + request.getServletPath();
            log.info("link {}", link);
            Url currentUrl = urlService.getUrlByHashedUrl(link);
            if(currentUrl == null){
                messages.put("message", "Invalid url");
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
            if(url.getOriginalUrl() == null || url.getOriginalUrl() == ""){
                response.setStatus(400);
                messages.put("message", "invalid input please Url can not be empty");
                new ObjectMapper().writeValue(response.getOutputStream(), messages);
                return null;
            }else{
                String link = "https://bitly-backend.herokuapp.com/";
                LocalDateTime time = LocalDateTime.now();
                url.setHashedUrl( link.concat(Hashing.murmur3_32().hashString(url.getOriginalUrl().concat(time.toString()), StandardCharsets.UTF_8).toString()));
                response.setStatus(200);
                return  urlService.registerUrl(url);
            }
        }catch(Exception exception){
            log.info("Error ", exception.getMessage());
            return null;
        }
    }
}
