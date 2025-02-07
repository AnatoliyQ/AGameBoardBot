package com.sennikov.avoboardgame.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.stream.Collectors;


@Controller
@Slf4j
public class WebController {
    
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        log.info("All headers: {}", Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        headerName -> headerName,
                        request::getHeader
                )));
                
        String userAgent = request.getHeader("User-Agent");
        log.info("User-Agent: {}", userAgent);
        
        String queryString = request.getQueryString();
        log.info("Query string: {}", queryString);
        
        if (userAgent != null && userAgent.contains("TelegramWebApp")) {
            log.info("Application opened in Telegram WebApp");
        } else {
            log.warn("Application opened outside Telegram WebApp");
        }

        String initData = request.getParameter("tgWebAppData");
        if (initData != null) {
            log.info("Telegram WebApp initData present: {}", initData);
        } else {
            log.warn("No Telegram WebApp initData");
        }

        String startParam = request.getParameter("tgWebAppStartParam");
        if (startParam != null) {
            log.info("Telegram WebApp startParam: {}", startParam);
        }

        return "redirect:/index.html";
    }
} 