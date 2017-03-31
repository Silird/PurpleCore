package ru.Silird.PurpleCore;

import java.util.List;

public class Configuration {
    private List<String> cores;

    private int javaXmx;

    private int javaXms;

    public List<String> getCores() {
        return cores;
    }

    public void setCores(List<String> cores) {
        this.cores = cores;
    }

    public int getJavaXmx() {
        return javaXmx;
    }

    public void setJavaXmx(int javaXmx) {
        this.javaXmx = javaXmx;
    }

    public int getJavaXms() {
        return javaXms;
    }

    public void setJavaXms(int javaXms) {
        this.javaXms = javaXms;
    }
}
