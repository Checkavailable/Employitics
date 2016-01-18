package com.example.bzubiaga.employitics;

/**
 * Created by bzubiaga on 12/20/15.
 */


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Dictionary of adjectives and nouns.
 *
 * @author Kohsuke Kawaguchi
 */
public class Dictionary {
    private List<String> nouns = new ArrayList<String>();
    private List<String> adjectives = new ArrayList<String>();

    private final int prime;

    public Dictionary() {
        try {
            load("a.txt", adjectives);
            load("n.txt", nouns);
        } catch (IOException e) {
            throw new Error(e);
        }

        int combo = size();

        int primeCombo = 2;
        while (primeCombo<=combo) {
            int nextPrime = primeCombo+1;
            primeCombo *= nextPrime;
        }
        prime = primeCombo+1;
    }

    /**
     * Total size of the combined words.
     */
    public int size() {
        return nouns.size()*adjectives.size();
    }

    /**
     * Sufficiently big prime that's bigger than {@link #size()}
     */
    public int getPrime() {
        return prime;
    }

    public String word(int i) {
        int a = i%adjectives.size();
        int n = i/adjectives.size();
        String name = adjectives.get(a)+" "+nouns.get(n);
        StringBuffer res = new StringBuffer();
        String[] strArr = name.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }


    private void load(String name, List<String> col) throws IOException {

        AssetManager am = MyApplication.getAppContext() .getAssets();
        InputStream is = am.open(name);

        InputStreamReader inputreader = new InputStreamReader(is);
        BufferedReader r = new BufferedReader(inputreader);

        try {
            String line;
            while ((line=r.readLine())!=null)
                col.add(line);
        } finally {
            r.close();
        }
    }

    static final Dictionary INSTANCE = new Dictionary();
}
