package org.src;

import org.src.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Genome {
    private String name;
    private String version;
    private ArrayList<Gene> proteinCodingGenes;

    public Genome() {
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

    public void setProteinCodingGenes(ArrayList<Gene> proteinCodingGenes) {
        this.proteinCodingGenes = proteinCodingGenes;
    }

    public void readGTF(String pathToGtf) throws IOException {
        ArrayList<Gene> proteinCodingGenes = new ArrayList<>();

        // get lines of gtf
        ArrayList<String> lines = FileUtils.readLines(new File(pathToGtf));

        // determine gtf type
        boolean isGenecode = lines.get(0).startsWith("##");

        // sanity check vars
        Gene currGene = null;
        int cdsCounter = 0;

        System.out.println(pathToGtf);
        for (int i = 0; i < lines.size()-1; i++) {
            String currLine = lines.get(i);

            // skip potential comments
            if (currLine.startsWith("#")) {
                continue;
            }

            // extract main components (line split by \t)
            String[] mainComponents = currLine.split("\t");
            // split attributes again at ";"
            String[] attributes = mainComponents[mainComponents.length - 1].split(";");

            // parse attributes into hashmap
            HashMap<String, String> attributeMap = new HashMap<>();
            for(String entry: attributes) {
                String[] entryComponent = entry.trim().split(" ");
                attributeMap.put(entryComponent[0].trim(), entryComponent[1].replaceAll("\"", "").trim());
            }

            // get geneId of current line
            String geneId = attributeMap.get("gene_id");

            // check if we hit a new gene
            if (currGene == null || !geneId.equals(currGene.getGeneId())) {
                // update gene and continue with next gtf line
                currGene = new Gene(geneId, Integer.parseInt(mainComponents[3]), Integer.parseInt(mainComponents[4]));

                continue;
            }

            // only add cds to current transcript
            if (mainComponents[2].equals("CDS")) {
                String cdsIdKey = isGenecode ? "ccdsid" : "protein_id";
                // check if we are in a new transcipt
                if(currGene.getTranscripts().isEmpty()) { // if gene transcripts are empty, just add new transcript

                    // in this case we can add the currGene to Genome.genes
                    // this leads to some duplicated code in the else case but it is easier that way
                    this.proteinCodingGenes.add(currGene);

                    cdsCounter = 0; // reset cdsCounter
                    // add new transcript to current gene
                    currGene.addTranscript(new Transcript(attributeMap.get("transcript_id"), mainComponents[6].charAt(0)));
                    // add cds to current transcript
                    currGene.getLastTranscript().addCds(
                            attributeMap.get(cdsIdKey),
                            Integer.parseInt(mainComponents[3]),
                            Integer.parseInt(mainComponents[4]),
                            cdsCounter
                    );
                    cdsCounter++;
                }
                // else check if we are still in the same transcript
                else if (attributeMap.get("transcript_id").equals(currGene.getLastTranscript().getTranscript_id())) {
                    currGene.getLastTranscript().addCds(
                            attributeMap.get(cdsIdKey),
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
                    currGene.addTranscript(new Transcript(attributeMap.get("transcript_id"), mainComponents[6].charAt(0)));
                    // add cds to current transcript
                    currGene.getLastTranscript().addCds(
                            attributeMap.get(cdsIdKey),
                            Integer.parseInt(mainComponents[3]),
                            Integer.parseInt(mainComponents[4]),
                            cdsCounter
                    );
                    cdsCounter++;

                }
            }
        }
    }
}
