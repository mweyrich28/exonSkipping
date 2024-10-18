package org.src;

import java.util.ArrayList;

public class Gene {
    private final String geneId;
    private final int start;
    private final int end;
    private ArrayList<Transcript> transcripts;

    public Gene(String geneId, int start, int end) {
        this.geneId = geneId;
        this.start = start;
        this.end = end;
        this.transcripts = new ArrayList<>();
    }

    public String getGeneId() {
        return geneId;
    }

    public void addTranscript (Transcript transcript){
        transcripts.add(transcript);
    }

    public ArrayList<Transcript> getTranscripts() {
        return transcripts;
    }

    public Transcript getLastTranscript() {
        if (transcripts.size() != 0) {
            return transcripts.get(transcripts.size() - 1);
        }
        return null;
    }
}
