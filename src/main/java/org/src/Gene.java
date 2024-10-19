package org.src;

import java.util.ArrayList;

public class Gene {
    private final String geneId;
    private final int start;
    private final int end;
    private ArrayList<Transcript> transcripts;
    private ArrayList<Intron> introns;
    private final String geneName;
    private final String chr;
    private final char strand;

    public Gene(String geneId, int start, int end, String geneName, String chr, char strand) {
        this.geneId = geneId;
        this.geneName = geneName;
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.strand = strand;
        this.transcripts = new ArrayList<>();
        this.introns = new ArrayList<>();
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
        if (!transcripts.isEmpty()) {
            return transcripts.get(transcripts.size() - 1);
        }
        return null;
    }

    public void generateIntrons() {
        for (Transcript transcript : transcripts) {
            for (int i = 0; i < transcript.getCdsList().size() - 1; i++) {
                int intronStart = transcript.getCdsList().get(i).getEnd() + 1;
                int intronEnd = transcript.getCdsList().get(i + 1).getStart() - 1;
                Intron intron = new Intron(intronStart, intronEnd);
                introns.add(intron);
            }
        }
    }

    public int getNprots() {
        int numProts = 0;
        for (Transcript transcript : transcripts) {
            numProts += transcript.getCdsList().size();
        }
        return numProts;
    }

    public int getNtrans() {
        return transcripts.size();
    }
}
