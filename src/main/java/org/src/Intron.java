package org.src;

public class Intron {
    private final int start;
    private final int end;

    public Intron(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }
}
