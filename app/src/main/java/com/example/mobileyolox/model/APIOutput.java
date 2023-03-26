package com.example.mobileyolox.model;

import java.util.List;

public class APIOutput {
    String time_elapsed;
    List<Integer> boxes;
    List<Integer> scores;
    List<Integer> classes;

    public APIOutput(String time_elapsed, List<Integer> boxes, List<Integer> scores, List<Integer> classes) {
        this.time_elapsed = time_elapsed;
        this.boxes = boxes;
        this.scores = scores;
        this.classes = classes;
    }

    public String getTime_elapsed() {
        return time_elapsed;
    }

    public void setTime_elapsed(String time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public List<Integer> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Integer> boxes) {
        this.boxes = boxes;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public List<Integer> getClasses() {
        return classes;
    }

    public void setClasses(List<Integer> classes) {
        this.classes = classes;
    }
}
