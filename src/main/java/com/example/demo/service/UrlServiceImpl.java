package com.example.demo.service;

import com.example.demo.models.Url;
import com.example.demo.reposirtory.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlServiceImpl implements  UrlService{
    @Autowired
    UrlRepository urlRepository;
    @Override
    public Optional<Url> getUrlByUrlId(int id) {
        return urlRepository.findById(id);
    }

    @Override
    public Url getUrlByHashedUrl(String hashedUrl) {
        return urlRepository.findByHashedUrl(hashedUrl);
    }

    @Override
    public Url registerUrl(Url url) {
        return urlRepository.save(url);
    }
}
