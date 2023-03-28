package com.example.mobileyolox.model;

import java.util.List;

public class APIOutput {
    String time_elapsed;
    List<List<Float>> boxes;
    List<Float> scores;
    List<String> classes;

    public APIOutput(String time_elapsed, List<List<Float>> boxes, List<Float> scores, List<String> classes) {
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

    public List<List<Float>> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<List<Float>> boxes) {
        this.boxes = boxes;
    }

    public List<Float> getScores() {
        return scores;
    }

    public void setScores(List<Float> scores) {
        this.scores = scores;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
}
