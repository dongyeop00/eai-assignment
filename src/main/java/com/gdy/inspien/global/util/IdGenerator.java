package com.gdy.inspien.global.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);

    // A000, A999, B001 ... Z999 까지만 가능 26 X 1000 = 26,000개까지만 가능
    public String generate(){
        int current = counter.getAndIncrement();

        char prefix = (char) ('A' + (current / 1000) % 26);
        int suffix = current % 1000;

        return String.format("%c%03d", prefix, suffix);
    }

    public void initCounterFromMaxId(String maxId) {
        if (maxId == null || maxId.length() < 4) {
            counter.set(0);
            return;
        }

        try {
            char prefix = maxId.charAt(0);
            int suffix = Integer.parseInt(maxId.substring(1));
            
            // (알파벳 순서 * 1000) + 숫자 + 1 (다음 번호)
            int nextValue = ((prefix - 'A') * 1000) + suffix + 1;
            counter.set(nextValue);
        } catch (Exception e) {
            counter.set(0);
        }
    }

    public void reset(){
        counter.set(0);
    }
}