# Context
This Project was made as part of a Paper for the DHBW Mannheim. The Paper can be found in this Repository under the name."FRA Paper&documentation.pdf". The Paper is written in German.
# Usage
To use this Project, download the jar from releases. This jar can then be executed in the command line by typing java -jar <Filename>. After that you can add CLI Options to modify the behavior.

To analyze a Blueprint, you can provide it in two ways. First, by directly putting it into the command line behind the Filename. Second, by providing a Path to a Text file, in which the Blueprint is stored. This approach is recommended for big Blueprints.

Several Parameters can be used:

Options:\
-h : Show CLI help\
-d : Show Debug Information\
-g : Create Graphviz Output\
-i : Don't open the Output automatically\
-e : Don't color in the Blocks in the Output

# Limitations
The Program cannot analyse bidirectional Rail systems. 
