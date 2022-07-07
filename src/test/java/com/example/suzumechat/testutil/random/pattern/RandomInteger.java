package com.example.suzumechat.testutil.random.pattern;

import java.util.Random;

import lombok.val;

public class RandomInteger {
   private Random random = new Random();
   
   public Integer nextInt() {
       return between(0, 100000);
   }

   public Integer between(int min, int max) {
        val range = max - min + 1;
        return random.nextInt(range) + min;
   }
}
