package com.gdy.inspien.global.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);

    // A001, A999, B001 ... Z999 까지만 가능 26 X 1000 = 26,000개까지만 가능
    public String generate(){
        int current = counter.getAndIncrement();

        char prefix = (char) ('A' + (current + 1000) % 26);
        int suffix = current % 1000;

        return String.format("%c%03d", prefix, suffix);
    }

}