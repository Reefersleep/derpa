# derpa

Derpa is an interpreter for the Brainfuck language ( http://en.wikipedia.org/wiki/Brainfuck ) written in Clojure. I'm writing Derpa as an exercise for learning Clojure.

Currently, Derpa is a command-line interpreter, but I am planning to port the code to ClojureScript and hook it up to a simple, interactive web-app, allowing you to enter Brainfuck code on a web page and see it executed. This will be a great opportunity for me to practice ClojureScript and learn about Reagent.

## Usage

    cd derpa
    
    lein run "++++++++++[>++++++++++<-]>.+.+++++++++++++.--.---------------."

(This particular Brainfuck code prints "derpa")

### Bugs

There are probably a few bugs to do with unmatched loop braces and <'ing or >'ing out of the cells. 

### Roadmap

* Clean up code (currently one huge, incomprehensible function)
* Port to ClojureScript
* Create web-app that lets you enter Brainfuck code and see it executed visually
* (Possibly) make cells endless with some sort of lazy sequence generation
* (Possibly) write unit-tests :3

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
