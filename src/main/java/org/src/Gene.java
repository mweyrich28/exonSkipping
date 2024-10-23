package org.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Gene {
    private final String geneId;
    private final int start;
    private final int end;
    private ArrayList<Transcript> transcripts;
    private HashSet<Intron> introns;
    private final String geneName;
    private final String chr;
    private final char strand;

    private int nTrans = 0; // 0 per default

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

    public void addTranscript(Transcript transcript){
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

            // min max skip exons
            int minSkippedExons = Integer.MAX_VALUE;
            int maxSkippedExons = Integer.MIN_VALUE;

            // min max skip bases
            int minSkippedBases = Integer.MAX_VALUE;
            int maxSkippedBases = Integer.MIN_VALUE;

            int intronStart = intron.getStart();
            int intronEnd = intron.getEnd();

            boolean atLeastOneWT = false;

            for (Transcript currTranscript: transcripts) {
                // get relevant HashMaps and check if currTranscript has cds starting or ending at i_s i_e
                HashMap<Integer, CodingDnaSequence> cdsEnds = currTranscript.getCdsEndIndices();
                HashMap<Integer, CodingDnaSequence> cdsStarts = currTranscript.getCdsStartIndices();
                boolean hasCdsInFront = cdsEnds.containsKey(intronStart - 1);
                boolean hasCdsBehind = cdsStarts.containsKey(intronEnd + 1);

                if (hasCdsInFront && hasCdsBehind) {
                    // add pos of intron
                    SV_INTRON.add(intronStart + ":" + (intronEnd + 1));

                    // get offset of cds in front and behind
                    CodingDnaSequence cdsFront = cdsEnds.get(intronStart - 1);
                    CodingDnaSequence cdsBehind = cdsStarts.get(intronEnd + 1);

                    // get offset / look if there are cds in between cdsFront and cdsBehind
                    int offset = cdsBehind.getPos() - cdsFront.getPos();


                    if (offset != 1) {
                        // set flag that we discovered at least one WT
                        atLeastOneWT = true;
                        ArrayList<CodingDnaSequence> cdsList = currTranscript.getCdsList();

                        // since we are in a WT, update exon stats
                        int skippedExons = offset - 1;
                        // update maxSkippedExons
                        if (skippedExons > maxSkippedExons) {
                            maxSkippedExons = skippedExons;
                        }
                        // update minSkippedExons
                        if (skippedExons < minSkippedExons) {
                            minSkippedExons = skippedExons;
                        }

                        // add all introns of WT to WT_INTRON and all cdsids/prot_ids to WT_prots
                        int skippedBases = 0;
                        for (int i = cdsFront.getPos() ; i < cdsBehind.getPos(); i++) {
                            int wtIntronStart = cdsList.get(i).getEnd() + 1;
                            int wtIntronEnd = cdsList.get(i+1).getStart();
                            WT_INTRON.add(wtIntronStart + ":" + wtIntronEnd);

                            // like this i add many ids twice but that's fine :)
                            WT_PROTS.add(cdsFront.getId());
                            WT_PROTS.add(cdsBehind.getId());

                            if (i > cdsFront.getPos() && i < cdsBehind.getPos()) {
                                skippedBases += cdsList.get(i).getEnd() - cdsList.get(i).getStart() + 1;
                            }
                        }

                        // update max min bases

                        if (skippedBases >= maxSkippedBases) {
                            maxSkippedBases = skippedBases;
                        }

                        if (skippedBases <= minSkippedBases) {
                            minSkippedBases = skippedBases;
                        }
                    }
                    // if offset == 1 that means that we are in a SV currTranscript → add currTranscript id to SV_prots
                    else {
                        SV_PROTS.add(cdsFront.getId());
                    }
                }
            }
            if (atLeastOneWT) {
                // get all relevant row data
                String geneId = this.geneId;
                String symbol = this.geneName;
                String chr = this.chr;
                char strand = this.strand;
                int nprots = this.transcripts.size();
                int ntrans = this.nTrans;
                String SVentry = String.join("|", SV_INTRON);
                String WTentry = String.join("|", WT_INTRON);
                String SVprotsEntry = String.join("|", SV_PROTS);
                String WTprotsEntry = String.join("|", WT_PROTS);
                int minSkippedExonsEntry = minSkippedExons;
                int maxSkippedExonsEntry = maxSkippedExons;
                int minSkippedBasesEntry = minSkippedBases;
                int maxSkippedBasesEntry = maxSkippedBases;
                // System.out.print(geneId + "\t");
                // System.out.print(symbol + "\t");
                // System.out.print(chr + "\t");
                // System.out.print(strand + "\t");
                // System.out.print(nprots + "\t");
                // System.out.print(ntrans + "\t");
                // System.out.print(SVentry + "\t");
                // System.out.print(WTentry + "\t");
                // System.out.print(SVprotsEntry + "\t");
                // System.out.print(WTprotsEntry + "\t");
                // System.out.print(minSkippedExonsEntry + "\t");
                // System.out.print(maxSkippedExonsEntry + "\t");
                // System.out.print(minSkippedBasesEntry + "\t"); // TODO
                // System.out.print(maxSkippedBasesEntry); // TODO
                // System.out.println();
            }
        }
    }

    public void incnTrans() {
        this.nTrans++;
    }

    public char getStrand() {
        return strand;
    }
}
