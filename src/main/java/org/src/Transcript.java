package org.src;

import java.util.ArrayList;
import java.util.HashMap;

public class Transcript {
    private final String transcript_id;
    private final HashMap<Integer, Cds> cdsEndIndices;
    private final HashMap<Integer, Cds> cdsStartIndices;
    private final ArrayList<Cds> cdsList;

    public Transcript(String transcript_id) {
        this.transcript_id = transcript_id;
        this.cdsEndIndices = new HashMap<>();
        this.cdsStartIndices = new HashMap<>();
        this.cdsList = new ArrayList<>();
    }

    public void addCds(String cdsid, int start, int end, int pos) {
        Cds cds = new Cds(cdsid, start, end, pos);

        // easily check if transcript has a cds ending at I_s / starting at I_e
        cdsEndIndices.put(end, cds);
        cdsStartIndices.put(start, cds);

        // also store them in a list, useful for checking if there are other cdss in between I_s-I_e → WT
        cdsList.add(cds);
    }

    public String getTranscript_id() {
        return transcript_id;
    }

    public ArrayList<Cds> getCdsList() {
        return this.cdsList;
    }

    public HashMap<Integer, Cds> getCdsEndIndices() {
        return cdsEndIndices;
    }

    public HashMap<Integer, Cds> getCdsStartIndices() {
        return cdsStartIndices;
    }
}
