#!/usr/bin/sh
for file in "$@"; do
    echo "$file"
    cut -f9 "$file" | cut -f1 -d";" | sort | uniq | wc -l
done
