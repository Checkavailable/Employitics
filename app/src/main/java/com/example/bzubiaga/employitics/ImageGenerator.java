package com.example.bzubiaga.employitics;

import java.util.Random;

/**
 * Created by bzubiaga on 12/15/15.
 */
public class ImageGenerator {

    String[] imageArray = new String[]{"bat_128px.png","bear_128px.png","bee_128px.png","bird_128px.png","bug_128px.png","butterfly_128px.png","camel_128px.png",
    "cat_128px.png", "cheetah_128px.png", "chicken_128px.png", "coala_128px.png", "cow_128px.png", "crocodile_128px.png", "dinosaur_128px.png", "dog_128px.png",
    "dolphin_128px.png", "dove_128px.png", "duck_128px.png", "eagle_128px.png", "elephant_128px.png", "fish_128px.png", "flamingo_128px.png",
    "fox_128px.png", "frog_128px.png", "giraffe_128px.png" , "gorilla_128px.png", "horse_128px.png", "kangoroo_128px.png", "leopard_128px.png", "lion_128px.png",
    "monkey_128px.png", "mouse_128px.png", "panda_128px.png", "parrot_128px.png" , "penguin_128px.png", "shark_128px.png", "sheep_128px.png", "snake_128px.png",
    "spider_128px.png", "squirrel_128px.png", "star_fish_128px.png", "tiger_128px.png", "turtle_128px.png", "wolf_128px.png", "zebra_128px.png"};

    public ImageGenerator() {}

    public String randomImage() {
        String randomImage = "";
        Random rand = new Random();
        int  n = rand.nextInt((imageArray.length));
        return imageArray[n];
    }

}
