# Exon Skipping Splicing Events

## Definition
In einem Gen kann jedes Transkript jeweils mehrere `ES-SE` haben.
Ein `ES-SE` involviert immer jeweils mindestens eine `Splice Variant` (`SV`) und einen
`Wild Type` (`WT`). Beide dieser Begriffe beziehen sich auf Transkripte eines Gens $G$.
Ein `SV` ist ein Transkript $T_{SV}$, welches ein Intron $I$ mit Startposition $I_{S}$ und Endposition
$I_{E}$ besitzt, was gleichzeitig bedeutet, dass es in $T_{SV}$ zwei Exons $A, B$ gibt, die $I$ flankieren. Somit endet $A$ bei $I_{S} - 1 = A_{E}$ und $B$ startet bei $I_{E} + 1 = B_{S}$.
Zudem ist die Position $B_{pos} - A_{pos} = 1$, wobei sich $A_{pos}$ auf die Position von Exon $A$ relativ gesehen
zu allen anderen Exons von $T_{SV}$ bezieht.
Ein `WT` wäre nun ein weiteres Transkript $T_{WT}$ des selben Gens $G$, welches ebenfalls
zwei Exons $C, D$ besitzt mit $C_{pos} < D_{pos}$, wobei $C_{E} = I_{S} - 1$ und $D_{S} = I_{E} + 1$,
jedoch gilt für $C, D$: $D_{pos} - C_{pos} > 1$.
Dies bedeutet, dass die Exons von $T_{WT}$ zwischen $C$ und $D$ in $T_{SV}$ übersprungen wurden.
Es kann pro Event mehrere `SV`'s und `WT`'s geben.
