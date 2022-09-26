package com.example.demo.service;

import com.example.demo.models.Url;
import org.springframework.expression.spel.ast.OpAnd;

import java.util.Optional;

public interface UrlService {

    Optional<Url> getUrlByUrlId(int id);
    Url getUrlByHashedUrl(String originalUrl);

    Url registerUrl(Url url);
}
