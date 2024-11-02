#!/usr/bin/sh
for file in "$@"; do
    echo "$file"
    # count uniq genes in ES-SE results
    # tail -n +2 "$file" | cut -f1 | sort | uniq | wc -l
    
    # count protein protein coding genes in gtf
    # grep -P "CDS\t" "$file" | cut -f9 | cut -f1 -d";" | sort | uniq | wc -l

    # count total amoutn of ES-SE in results
    # tail -n +2 "$file" | wc -l

    # check for occurences of x in all gtf files
    # cut -f9 "$file" | cut -f1 -d";" | grep -P "ENSG00000271425" | wc -l

    # check max exons skipped of gene id x
    x="ENSG00000283186"
    echo "$x"
    grep -P "$x" "$file" | cut -f12 | sort -r -n | head -n1
done



# 
