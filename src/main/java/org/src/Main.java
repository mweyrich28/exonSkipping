package org.src;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.src.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        // add argparser
        ArgumentParser parser = ArgumentParsers.newFor("ExonSkipping").build().defaultHelp(true).description("Usage:\n\t-gtf <path-to-gtf>\n\t-out <path-to-out-tsv>");
        try {
            parser.addArgument("-gtf").required(true).help("Path to Gene Transfer Format File.");
            parser.addArgument("-o").required(true).help("Specify Output File Name.");
            Namespace ns = parser.parseArgs(args);
            String gtfPath = ns.getString("gtf");
            String outPath = ns.getString("o");

            Genome genome = new Genome();
            // NOTE: potentially create own gtfReader class that returns Genomes based on task
            // genome.readGTFCDS("/home/malte/projects/gobi/exonSkipping/gtfFiles/gencode.v10.annotation.gtf");
            // genome.readGTFCDS("/home/malte/projects/gobi/exonSkipping/gtfFiles/Homo_sapiens.GRCh38.86.gtf");
            // genome.readGTFCDS("/home/malte/projects/gobi/exonSkipping/gtfFiles/example_in.gtf");

            genome.readGTFCDS(gtfPath);
            ArrayList<String> events = genome.generateESSE();
            FileUtils.writeFile(outPath, events);
        }
        // print usage entry if not all required args were provided
        catch (ArgumentParserException e) {
            System.out.println(
                    """
                    USAGE: exonSkipping.jar
                    Identifies ES-SE events of protein coding transcripts for a given gtf file.
                        Usage
                            -gtf <gtfPath.gtf> [required]
                            -o <destinationPath.tsv> [required]
                        Help
                            -h display help for parameters.
                    """
            );
            System.exit(1);
        }
    }
}
