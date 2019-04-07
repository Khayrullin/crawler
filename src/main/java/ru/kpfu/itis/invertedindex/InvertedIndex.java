package ru.kpfu.itis.invertedindex;

import java.util.TreeMap;

public class InvertedIndex {
    private TreeMap<String, Integer[]> map = new TreeMap<>();

    public TreeMap<String, Integer[]> getMap() {
        return map;
    }

    public void setMap(TreeMap<String, Integer[]> map) {
        this.map = map;
    }
}
