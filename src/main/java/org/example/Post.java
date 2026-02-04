package org.example;

public record Post(  String title,
        String content,
        Integer replies,
        Integer images,
        String hash)
{

}
