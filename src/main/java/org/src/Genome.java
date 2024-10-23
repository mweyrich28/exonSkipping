package org.src;

import org.src.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Genome {
    private String name;
    private String version;
    private ArrayList<Gene> proteinCodingGenes;

    public Genome() {
        this.proteinCodingGenes = new ArrayList<>();
    }

    public Genome(String name, String version) {
        this.name = name;
        this.version = version;
        this.proteinCodingGenes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<Gene> getProteinCodingGenes() {
        return proteinCodingGenes;
    }

    public void generateESSE() {
        for (Gene gene : proteinCodingGenes) {
            if (gene.getStrand() == '-') {
               gene.invertTranscripts();
            }
            gene.generateIntrons();
            gene.getEvents();
        }
    }

    public String parseAttributes(String[] attributeEntries, String attributeName) {
        for (int i = 0; i < attributeEntries.length; i++) {
            String trimmedEntry = attributeEntries[i].trim();

            int posSpace = trimmedEntry.indexOf(' ');
            String attributeKey = trimmedEntry.substring(0, posSpace);
            String attributeVal = trimmedEntry.substring(posSpace + 2, trimmedEntry.length() - 1);
            if (attributeKey.equals(attributeName)) {
                return attributeVal;
            }
        }
        return null;
    }

    public void readGTFCDS(String pathToGtf) throws IOException {
        // get lines of gtf
        ArrayList<String> lines = FileUtils.readLines(new File(pathToGtf));

        // determine gtf type
        boolean isGenecode = lines.get(0).startsWith("##");

        // sanity check vars
        Gene currGene = null;
        int cdsCounter = 0;
        int nTrans = 0;
        String lastTranscriptId = null;

        for (int i = 0; i < lines.size() - 1; i++) {
            String currLine = lines.get(i);

            // skip potential comments
            if (currLine.startsWith("#")) {
                continue;
            }

            // extract main components (line split by \t)
            String[] mainComponents = currLine.split("\t");
            // split attributes again at ";"
            String[] attributeEntries = mainComponents[mainComponents.length - 1].split(";");

            // get newGeneId of current line
            String newGeneId = parseAttributes(attributeEntries, "gene_id");

            // check if we hit a new gene
            if (currGene == null || !newGeneId.equals(currGene.getGeneId())) {
                // update gene and continue with next gtf line
                int geneStart = Integer.parseInt(mainComponents[3]);
                int geneEnd = Integer.parseInt(mainComponents[4]);
                String geneName = parseAttributes(attributeEntries, "gene_name");
                String chr = mainComponents[0];
                char strand = mainComponents[6].charAt(0);
                currGene = new Gene(newGeneId, geneStart, geneEnd, geneName, chr, strand);

                continue;
            }

            // only add cds to current transcript
            String transcriptId = parseAttributes(attributeEntries,"transcript_id");
            if (mainComponents[2].equals("CDS")) {
                String cdsIdKey = isGenecode ? "ccdsid" : "protein_id";
                String cdsId = parseAttributes(attributeEntries, cdsIdKey);
                // check if we are in a new transcript
                if(currGene.getTranscripts().isEmpty()) { // if gene transcripts are empty, just add new transcript

                    // add gene to genome (based on if it is p coding or not)
                    this.proteinCodingGenes.add(currGene);

                    cdsCounter = 0; // reset cdsCounter

                    // add new transcript to current gene
                    Transcript transcript = new Transcript(transcriptId, mainComponents[2]);
                    currGene.addTranscript(transcript);

                    // add cds to current transcript
                    currGene.getLastTranscript().addCds(
                            cdsId,
                            Integer.parseInt(mainComponents[3]),
                            Integer.parseInt(mainComponents[4]),
                            cdsCounter
                    );
                    cdsCounter++;
                }
                // else check if we are still in the same transcript
                else if (transcriptId.equals(currGene.getLastTranscript().getTranscriptId())) {
                    currGene.getLastTranscript().addCds(
                            cdsId,
                            Integer.parseInt(mainComponents[3]),
                            Integer.parseInt(mainComponents[4]),
                            cdsCounter
                    );
                    cdsCounter++;
                }
                // else we add a new transcript
                else {
                    cdsCounter = 0; // reset cdsCounter
                    // add new transcript to current gene
                    currGene.addTranscript(new Transcript(transcriptId, mainComponents[2]));
                    // add cds to current transcript
                    currGene.getLastTranscript().addCds(
                            cdsId,
                            Integer.parseInt(mainComponents[3]),
                            Integer.parseInt(mainComponents[4]),
                            cdsCounter
                    );
                    cdsCounter++;
                }
            }

            // here we increment the ntrans count, we currently don't need to save exons.
            // by only having a count, we save space
            if (mainComponents[2].equals("CDS") || mainComponents[2].equals("exon")) {
                if (!(transcriptId.equals(lastTranscriptId))) {
                    currGene.incnTrans();
                    lastTranscriptId = transcriptId;
                }
            }
        }
    }
}
