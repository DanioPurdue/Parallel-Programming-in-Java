## Future and Promises

links for future and promises: https://en.wikipedia.org/wiki/Futures_and_promises

Difference between `RecursiveAction` and `RecursiveTask`

## Streams

Both the existing Java notion of collections and the new notion of streams provide interfaces to a sequence of elements. So what’s the difference? In a nutshell, collections are about data and streams are about computations.

Articles: 

stream

https://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html

method reference: how to use it

https://www.codementor.io/eh3rrera/using-java-8-method-reference-du10866vx

anonymous class:

https://www.baeldung.com/java-anonymous-classes

when should we use method reference:

*Instead of using*  **AN ANONYMOUS CLASS**
*you can use* **A LAMBDA EXPRESSION**
*And if this just calls one method, you can use* **A METHOD REFERENCE**

## Determinism

### Functional Determinism

same input -> same output

### Structrual Determinism

same input -> same comp graph

```
SUM = SUM1 + SUM2
```

Data Race Freedom: functional determinims and structural determinism

"Benign NonDet" : both the values are acceptable, even though you may have different answers everytime.

## Stream

you should move the recursion into the `compute()` function.

https://howtodoinjava.com/java8/stream-max-min-examples/

