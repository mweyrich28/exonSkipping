package org.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

public class Gene {
    private final String geneId;
    private final int start;
    private final int end;
    private ArrayList<Transcript> transcripts;
    private HashSet<Intron> introns;
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
        this.introns = new HashSet<>();
    }

    public String getGeneId() {
        return geneId;
    }

    public void invertTranscripts() {
        for (int i = 0; i < transcripts.size(); i++) {
            Transcript currTranscript = transcripts.get(i);
            currTranscript.reversCdsList();
            for (int j = 0; j < currTranscript.getCdsList().size(); j++) {
                currTranscript.getCdsList().get(j).setPos(j);
            }
        }
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
            ArrayList<CodingDnaSequence> cdsList = transcript.getCdsList();
            for (int i = 0; i < transcript.getCdsList().size() - 1; i++) {
                int intronStart = transcript.getCdsList().get(i).getEnd() + 1;
                int intronEnd = transcript.getCdsList().get(i + 1).getStart() - 1;
                Intron intron = new Intron(intronStart, intronEnd);
                introns.add(intron);
            }
        }
    }

    public HashSet<Intron> getIntrons() {
        return introns;
    }

    public void getEvents() {
        // for every intron check every transcript
        for (Intron intron : introns) {
            // start:end
            HashSet<String> SV_INTRON = new HashSet<>();
            HashSet<String> WT_INTRON = new HashSet<>();
            // ids
            HashSet<String> SV_PROTS = new HashSet<>();
            HashSet<String> WT_PROTS = new HashSet<>();

            int intronStart = intron.getStart();
            int intronEnd = intron.getEnd();

            boolean atLeastOneWT = false;

            for (Transcript transcript: transcripts) {
                // get relevant HashMaps and check if transcript has cds starting or ending at i_s i_e
                HashMap<Integer, CodingDnaSequence> cdsEnds = transcript.getCdsEndIndices();
                HashMap<Integer, CodingDnaSequence> cdsStarts = transcript.getCdsStartIndices();
                boolean hasCdsInFront = cdsEnds.containsKey(intronStart - 1);
                boolean hasCdsBehind = cdsStarts.containsKey(intronEnd + 1);

                if (hasCdsInFront && hasCdsBehind) {
                    // add pos of intron
                    SV_INTRON.add(intronStart + ":" + intronEnd);

                    // get offset of cds in front and behind
                    CodingDnaSequence cdsFront = cdsEnds.get(intronStart - 1);
                    CodingDnaSequence cdsBehind = cdsStarts.get(intronEnd + 1);

                    // get offset / look if there are cds in between cdsFront and cdsBehind
                    int offset = cdsBehind.getPos() - cdsFront.getPos();

                    if (offset != 1) {
                        // set flag that we discovered at least one WT
                        atLeastOneWT = true;
                        ArrayList<CodingDnaSequence> cdsList = transcript.getCdsList();

                        // add all introns of WT
                        for (int i = cdsFront.getPos() ; i < cdsBehind.getPos(); i++) {
                            int wtIntronStart = cdsList.get(i).getEnd() + 1;
                            int wtIntronEnd = cdsList.get(i+1).getStart();
                            WT_INTRON.add(wtIntronStart + ":" + wtIntronEnd);
                        }
                    }
                }
            }
            if (atLeastOneWT) {
                System.out.println();
            }
        }
    }

    public int getNprots() {
        int numProts = 0
;
        for (Transcript transcript : transcripts) {
            numProts += transcript.getCdsList().size();
        }
        return numProts;
    }

    public int getNtrans() {
        return transcripts.size();
    }

    public char getStrand() {
        return strand;
    }
}
