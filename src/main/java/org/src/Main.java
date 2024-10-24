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
            parser.addArgument("-gtf").required(true).help("Path to Gene Transfer Format File");
            parser.addArgument("-out").required(true).help("Specify Output File Name");
            Namespace ns = parser.parseArgs(args);
            String gtfPath = ns.getString("gtf");
            String outPath = ns.getString("out");

            Genome g = new Genome();
            // NOTE: potentially create own gtfReader class that returns Genomes based on task
            g.readGTFCDS(gtfPath);
            ArrayList<String> events = g.generateESSE();
            FileUtils.writeFile(outPath, events);
        }
        // print usage entry if not all required args were provided
        catch (ArgumentParserException e) {
            System.out.println(
            "exonSkipping.jar\n" +
                    "Identifies ES-SE events of protein coding transcripts\n" +
                    "    Usage\n" +
                    "        -gtf <gtfPath.gtf> [required]\n" +
                    "        -out <destinationPath.tsv> [required]\n"
            );
            System.exit(1);
        }
    }
}
