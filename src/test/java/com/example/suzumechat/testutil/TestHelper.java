package com.example.suzumechat.testutil;

import java.util.Objects;

public class TestHelper {
    
   /**
    * If the given field is null, then return defaultItem.
    * With `Optional`, You can implement factory class functionality without this method.
    * But `Optional` makes client code messier and somewhat confusing.
    */
   public static <T> T getOrDefault(T field, T defaultItem) {
       return Objects.isNull(field) ? defaultItem : field;
   }
}
