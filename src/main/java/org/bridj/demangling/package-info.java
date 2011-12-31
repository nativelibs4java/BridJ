/**
BridJ symbols demanglers.
<p>
BridJ adheres to a "reverse burder of proof" philosophy : rather than asking binding makers (including JNAerator) to write/generate the exact expected symbols in {@link org.bridj.ann.Symbol} annotations, it parses the mangled symbols and sees how / if they match binding signatures. Manual specification of exact symbols is still possible in case of ambiguity or when demangling fails.
<p>
As a consequence, BridJ needs symbols demanglers, which have to parse symbols and spit out (partial) methods and types signatures.<br>
It currently has demanglers for : 
<ul>
<li>Microsoft Visual C++ (2008, 2010)
</li><li>GCC 4.x (which mangling scheme is fortunately shared by Intel's C++ compiler) : its mangling scheme does not include the return type of functions / methods, which is infortunate (would have allowed dynamic languages to call C++ without explicit bindings, otherwise :-( )
</li>
</ul>
*/
package org.bridj.demangling;
