Melissa Welsh - mwelsh2@u.rochester.edu
Caden Dowd - cdowd4@u.rochester.edu

Important: RandomVariables and files are case sensitive. Values are not. Both
BIF and XMLBIF files work. Project uses all code given by Professor Ferguson.

To test Enumeration Inferences (Run from MyBNInferencer class):
    java MyBNInferencer [ filename ] [ Query Variable ] [ Evidence Variables and Values]
- As seen in project description examples
- Examples: 
    java MyBNInferencer aima-alarm.xml B J true M true
    java MyBNInferencer aima-wet-grass.xml R S true

To test Sampling Inferences (Run from MyBNApproxInferencer class):
    java MyBNApproxInferencer [ Sample Size ] [ Method ] [ filename ] [ QueryVariable ] [ Evidence Variable and Values ]
- Method can be left blank (in which case all 3 will run), or can be specified
to "rsi" (Rejection Sampling), "lwi" (Likelihood Weighted Sampling) or "gsi" 
(Gibbs Sampling). Method is not case sensitive.
- Examples:
    java MyBNApproxInferencer 1000 aima-alarm.xml B J true M true
    java MyBNApproxInferencer 1000 rsi aima-alarm.xml B J true M true
    java MyBNApproxInferencer 1000 lwi aima-alarm.xml B J true M true
    java -MyBNApproxInferencer 1000 gsi aima-alarm.xml B J true M true
    
