package com.example.bzubiaga.employitics;

import java.util.Random;

/**
 * Created by bzubiaga on 12/15/15.
 */
public class ImageGenerator {

    String[] imageArray = new String[]{"bat_128px","bear_128px","bee_128px","bird_128px","bug_128px","butterfly_128px","camel_128px",
    "cat_128px", "cheetah_128px", "chicken_128px", "coala_128px", "cow_128px", "crocodile_128px", "dinosaur_128px", "dog_128px",
    "dolphin_128px", "dove_128px", "duck_128px", "eagle_128px", "elephant_128px", "fish_128px", "flamingo_128px",
    "fox_128px", "frog_128px", "giraffe_128px" , "gorilla_128px", "horse_128px", "kangoroo_128px", "leopard_128px", "lion_128px",
    "monkey_128px", "mouse_128px", "panda_128px", "parrot_128px" , "penguin_128px", "shark_128px", "sheep_128px", "snake_128px",
    "spider_128px", "squirrel_128px", "star_fish_128px", "tiger_128px", "turtle_128px", "wolf_128px", "zebra_128px"};

    public ImageGenerator() {}

    public String randomImage() {
        String randomImage = "";
        Random rand = new Random();
        int  n = rand.nextInt((imageArray.length));
        return imageArray[n];
    }

}
