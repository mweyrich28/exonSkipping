
package org.src;
public class Cds {
    private final String id;
    private final int start;
    private final int end;
    private final int pos;

    public Cds(String id, int start, int end, int pos) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getPos() {
        return pos;
    }
}
