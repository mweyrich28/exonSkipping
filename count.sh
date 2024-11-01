#!/usr/bin/sh
for file in "$@"; do
    echo "$file"
    # count uniq genes in ES-SE results
    # tail -n +2 "$file" | cut -f1 | sort | uniq | wc -l
    
    # count protein protein coding genes in gtf
    # grep -P "CDS\t" "$file" | cut -f9 | cut -f1 -d";" | sort | uniq | wc -l

    # count total amoutn of ES-SE in results
    # tail -n +2 "$file" | wc -l

    # check for occurences of ENSG00000155657.25 in all gtf files
    cut -f9 "$file" | cut -f1 -d";" | grep -P "ENSG00000155657" | wc -l
done
