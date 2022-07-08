package com.example.suzumechat.testutil.random.pattern;

import java.util.Random;

import lombok.val;

public class RandomInteger {
   private Random random = new Random();
   
   public Integer nextInt() {
       return nextInt(100000);
   }

   public Integer nextInt(Integer max) {
    return between(1, max);
   }

   public Integer between(Integer min, Integer max) {
        val range = max - min + 1;
        return random.nextInt(range) + min;
   }
}
