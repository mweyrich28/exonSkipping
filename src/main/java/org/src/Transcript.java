package org.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Transcript {
    private final String transcript_id;
    private final HashMap<Integer, CodingDnaSequence> cdsEndIndices;
    private final HashMap<Integer, CodingDnaSequence> cdsStartIndices;
    private final ArrayList<CodingDnaSequence> cdsList;

    public Transcript(String transcript_id) {
        this.transcript_id = transcript_id;
        this.cdsEndIndices = new HashMap<>();
        this.cdsStartIndices = new HashMap<>();
        this.cdsList = new ArrayList<>();
    }

    public void addCds(String cdsid, int start, int end, int pos) {
        CodingDnaSequence cds = new CodingDnaSequence(cdsid, start, end, pos);

        // easily check if transcript has a cds ending at I_s / starting at I_e
        cdsEndIndices.put(end, cds);
        cdsStartIndices.put(start, cds);
        cdsList.add(cds);
    }

    public String getTranscript_id() {
        return transcript_id;
    }

    public ArrayList<CodingDnaSequence> getCdsList() {
        return this.cdsList;
    }

    public HashMap<Integer, CodingDnaSequence> getCdsEndIndices() {
        return cdsEndIndices;
    }

    public HashMap<Integer, CodingDnaSequence> getCdsStartIndices() {
        return cdsStartIndices;
    }

    public void reversCdsList() {
        Collections.reverse(this.cdsList);
    }

}
