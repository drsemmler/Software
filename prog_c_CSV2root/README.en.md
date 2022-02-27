CSV2root
========
CSV2root converts one or several tables in the .csv format into a root tree. The program
"Root" is developed by the CERN and is widely in use for data analysis in particle and
nuclear physics.

Version:	0.2
Usage:	CSV2root is operated over the command line. Use following syntax:
	./CSV2root -o output.root [options] file1.csv [more options] [file2.csv] ...
	This call creates the file output.root and writes a tree for each given .csv file
	into it. The first line of the .csv file contains the names of the columns The
	data type is chosen automatically in the way so that the storage needed is as
	less as possible without loss of data.
	A complete manual is given at the man-page.
Tips:	After the execution if the installer it is recommend to place the binary
	"CSV2root" in your "/bin" folder. Then the program is callable from any path.
	If you place the file "CSV2root.1" in your "/usr/share/man/man1" folder, you can
	access the help at any time with the command man CSV2root.
Limits:	At the moment the program chooses always the 64-Bit type for float variables.
