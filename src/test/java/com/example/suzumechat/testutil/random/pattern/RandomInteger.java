package com.example.suzumechat.testutil.random.pattern;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.val;

public class RandomInteger {

   private final Random random;

   public RandomInteger(Random random) {
       this.random = random;
   }
   
   public Integer nextInt() {
       return nextInt(100000);
   }

   public Integer nextInt(int max) {
        return between(1, max);
   }

   public Integer between(int min, int max) {
        val range = max - min + 1;
        return random.nextInt(range) + min;
   }
}
