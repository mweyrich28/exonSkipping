package org.src;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ArgumentParserException, IOException {
        // add argparser
        ArgumentParser parser = ArgumentParsers.newFor("ExonSkipping").build().defaultHelp(true).description("Usage:\n\t-gtf <path-to-gtf>\n\t-out <path-to-out-tsv>");
        parser.addArgument("-gtf").help("Path to Gene Transfer Format File");
        parser.addArgument("-out").help("Specify Output File Name");

        Namespace ns = parser.parseArgs(args);
        String gtfPath = ns.getString("gtf");
        String outPath = ns.getString("out");

        String[] paths = {
            "/home/malte/projects/gobi/exonSkipping/gtfFiles/Homo_sapiens.GRCh37.67.gtf",
            "/home/malte/projects/gobi/exonSkipping/gtfFiles/gencode.v10.annotation.gtf",
            "/home/malte/projects/gobi/exonSkipping/gtfFiles/example_in.gtf",
            "/home/malte/projects/gobi/exonSkipping/gtfFiles/Homo_sapiens.GRCh38.86.gtf"
        };

        Genome g = new Genome();
        // NOTE: potentially create own gtfReader class that returns Genomes based on task
        g.readGTFCDS(paths[3]);
        g.generateESSE();
        System.out.println();
    }
}
