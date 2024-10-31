#!/usr/bin/sh
for file in "$@"; do
    echo "$file"
    # cut -f1 "$file" | sort | uniq | wc -l
    grep -P "CDS\t" "$file" | cut -f9 | cut -f1 -d";" | sort | uniq | wc -l
    # cat "$file" | wc -l
done
