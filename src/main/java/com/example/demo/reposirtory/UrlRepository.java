package com.example.demo.reposirtory;

import com.example.demo.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Integer> {
     Url findByHashedUrl(String hashedUrl);
     Url findByOriginalUrl(String originalUrl);
}
