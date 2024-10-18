package org.src;

import java.util.ArrayList;
import java.util.HashMap;

public class Transcript {
    private final String transcript_id;
    private final HashMap<Integer, Cds> cdsEndIndices;
    private final HashMap<Integer, Cds> cdsStartIndices;
    private final char strand;

    public Transcript(String transcript_id, char strand) {
        this.transcript_id = transcript_id;
        this.cdsEndIndices = new HashMap<>();
        this.cdsStartIndices = new HashMap<>();
        this.strand = strand;
    }

    public void addCds(String cdsid, int start, int end, int pos) {
        Cds cds = new Cds(cdsid, start, end, pos);
        cdsEndIndices.put(end, cds);
        cdsStartIndices.put(start, cds);
    }

    public String getTranscript_id() {
        return transcript_id;
    }
}
