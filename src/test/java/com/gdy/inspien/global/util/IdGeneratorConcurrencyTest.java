package com.gdy.inspien.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorConcurrencyTest {

    @Test
    @DisplayName("동시에 ID 생성을 요청해도 중복이 없어야 한다")
    void testConcurrencyWithStartingGun() throws InterruptedException {
        // Given
        IdGenerator idGenerator = new IdGenerator();
        int threadCount = 1000;
        int totalExpectedIds = threadCount;

        //Set<String> generatedIds = new HashSet<>();
        Set<String> generatedIds = ConcurrentHashMap.newKeySet();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    String id = idGenerator.generate();
                    generatedIds.add(id);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        // Then
        assertThat(generatedIds.size()).isEqualTo(totalExpectedIds);
        System.out.println("동시 시작 테스트 완료 - 생성된 ID 개수: " + generatedIds.size());
    }
}