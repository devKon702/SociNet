package com.example.userservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomMaker {
    public static List<Long> getRandomIndex(long range){
        List<Long> randomIndexes = new Random().longs(0, range).distinct().limit(5).boxed().collect(Collectors.toList());
        return randomIndexes;
    }
    public static List<?> toRandomList(Class clazz, List<?> list, int limit){
        Collections.shuffle(list);
        return list.stream().limit(limit).collect(Collectors.toList());
    }
}
