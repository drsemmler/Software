// Code by Diego Semmler (https://www.drsemmler.eu)

// Convert a file in the .csv-format into a root tree.
// The fist line shall contain the column names. Data types are selected automaticly if not otherwise specified.
// TrimWithQuotes of the raw csv-data is done. Quotes ("'`) are respected, and removed.

// Compilie with:
// `root-config --cxx` -g2 -O3  -W -Wall -o CSV2root CSV2root.c -I`root-config --incdir --libs`

// Update V 0.2 (14.11.2011):
// - It is possible to specify datatypes
// - CSV quotes will be removed

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <iostream>
#include "TFile.h"
#include "TTree.h"
#include "ReadCSV.c"

const char* Version = "0.2";

// Function declarations
void help(char* Exec);
Char_t StrToBoolean(const Char_t* Str);
Char_t* PopExtention(Char_t* String);
UShort_t EstimateDataTypeMatrix(Char_t* String);
Char_t *str4concat(const Char_t *str1, const Char_t *str2, const Char_t *str3, const Char_t *str4);
Char_t charFromString(const Char_t *str);

int main(int argc, Char_t **argv) {
Char_t*** CSVdata;
Char_t** FelderD;
Char_t* Datatype;
Char_t* Outfile = NULL;
Char_t* TreeName = NULL;
Char_t* ForcedDatatype = NULL;
UInt_t ForcedDatatypeLen = 0;
const Char_t* root_file_title = "ROOT file"; // set default options
const Char_t* chtitl = "Created from a .csv file";
Char_t Space, Separator = ';', LineSeparator = '\n', ChangeOption = 0;
UChar_t* Data;
int a;
UShort_t Matrix;
ULong64_t Columns, Rows, C, R, i, Compression = 1, Verbosity = 1, bufsize = 65536;
size_t* Addresses;
size_t SpaceTotal = 0;
TTree* tree;
clock_t NextPrint = 0;
clock_t StartTime;

if(sizeof(Char_t) != 1 || sizeof(UChar_t) != 1 || sizeof(Short_t) != 2 || sizeof(UShort_t) != 2 || sizeof(UInt_t) != 4 || sizeof(Int_t) != 4 || sizeof(Long64_t) != 8 || sizeof(ULong64_t) != 8 || sizeof(Double_t) != 8) { // Just in case, missmatching would be bad
	fprintf(stderr, "Size missmatch:\n  size of of Char_t: %ld (1 expected)\n  size of of UShort_t: %ld (2 expected)\n  size of of Int_t: %ld (4 expected)\n  size of ofULong64_t: %ld (8 expected)\n  size of of Double_t: %ld (8 expected)\n", sizeof(Char_t), sizeof(UShort_t), sizeof(Int_t), sizeof(Long64_t), sizeof(Double_t));
	abort();
	}

if(argc == 1) {
	help(argv[0]);
	return 1;
	}

for(a = 1; a < argc; a++) { // Work on global options before opening a file
	if(strcmp(argv[a], "-h") == 0 || strcmp(argv[a], "--help") == 0) {help(argv[0]); return 0;}
	if(argv[a][0] == '-' && a + 1 < argc) {
		if(strcmp(argv[a], "-c") == 0 || strcmp(argv[a], "--compression") == 0) Compression = atol(argv[a + 1]);
			else if(strcmp(argv[a], "-t") == 0 || strcmp(argv[a], "--title") == 0) root_file_title = argv[a + 1];
			else if(strcmp(argv[a], "-o") == 0 || strcmp(argv[a], "--outfile") == 0) Outfile = argv[a + 1];
			else if(strcmp(argv[a], "-v") == 0 || strcmp(argv[a], "--verbosity") == 0) Verbosity = atol(argv[a + 1]);
		a++;
		} else if(Outfile == NULL) { // Outfile has the name of the first infile if not specified otherwise
		Outfile = str4concat(argv[a], "", "", "");
		PopExtention(Outfile);
		Outfile = str4concat(Outfile, ".root", "", "");
		}
	}

if(Verbosity > 0) printf("Opening \"%s\" for output ...\n", Outfile);
TFile* hfile = TFile::Open(Outfile, "RECREATE", root_file_title, Compression); // Open output file
if (!hfile) {
	fprintf(stderr, "ERROR: can't open output file: \"%s\".\n", Outfile);
	return 2;
	}

for(a = 1; a < argc; a++) {
	ChangeOption = 0;
	if(argv[a][0] == '-' && a + 1 < argc) { // Work on options for the next file
		if(strcmp(argv[a], "-c") == 0 || strcmp(argv[a], "--compression") == 0) ;
			else if(strcmp(argv[a], "-t") == 0 || strcmp(argv[a], "--title") == 0) ;
			else if(strcmp(argv[a], "-o") == 0 || strcmp(argv[a], "--outfile") == 0) ;
			else if(strcmp(argv[a], "-v") == 0 || strcmp(argv[a], "--verbosity") == 0) ;
			else if(strcmp(argv[a], "-s") == 0 || strcmp(argv[a], "--separator") == 0) {
				ChangeOption = 1;
				Separator = charFromString(argv[a + 1]);
				}
			else if(strcmp(argv[a], "-l") == 0 || strcmp(argv[a], "--lineseparator") == 0) {
				ChangeOption = 1;
				LineSeparator = charFromString(argv[a + 1]);
				}
			else if(strcmp(argv[a], "-b") == 0 || strcmp(argv[a], "--buffer") == 0) {
				ChangeOption = 1;
				bufsize = atol(argv[a + 1]);
				}
			else if(strcmp(argv[a], "-i") == 0 || strcmp(argv[a], "--treetitle") == 0) {
				ChangeOption = 1;
				chtitl = argv[a + 1];
				}
			else if(strcmp(argv[a], "-n") == 0 || strcmp(argv[a], "--name") == 0) {
				ChangeOption = 1;
				TreeName = argv[a + 1];
				}
			else if(strcmp(argv[a], "-d") == 0 || strcmp(argv[a], "--datatypes") == 0) {
				ChangeOption = 1;
				ForcedDatatype = argv[a + 1];
				ForcedDatatypeLen = strlen(ForcedDatatype);
				}
			else fprintf(stderr, "Warning: The option \"%s\" is not supported and ignored.\n", argv[a]);
		if(a + 2 >= argc && ChangeOption) fprintf(stderr, "Warning: The option \"%s\" is at the end of the argument chain and has no effect.\n", argv[a]);
		a++;
		continue;
		}

	if(Verbosity > 0) printf("Reading \"%s\" ...\n", argv[a]); // Load file
	CSVdata = readCSVfile(argv[a], &Columns, &Rows, Separator, LineSeparator, '#', Verbosity > 0);
	if(CSVdata == NULL) return 12;
	if(Verbosity > 0) printf("CSV table is a %llu x %llu matrix.\n", Columns, Rows); // Allocate enough memory for the following procedure
	Datatype = (Char_t*) calloc(Columns + 1, sizeof(Char_t));
	if(Datatype == 0) return 13;
	Addresses = (size_t*) calloc(Columns, sizeof(size_t));
	if(Addresses == 0) return 14;
	FelderD = (Char_t**) calloc(Columns, sizeof(Char_t*));
	if(FelderD == 0) return 15;

	if(ForcedDatatypeLen > 1 && ForcedDatatypeLen != Columns) fprintf(stderr, "Warning: The number of specified datatypes (%u) does not match the number of columns (%llu) in the file. Surplus datatypes or columns will be ignored.\n", ForcedDatatypeLen, Columns);
	for(C = 0; C < Columns; C++) { // Get data types
		if(Verbosity > 1) printf("Found column \"%s\" with data type ", CSVdata[0][C]);
		if(ForcedDatatypeLen == 0) { // DataTypes not set, estimate it automatically
			Matrix = 0b11111111111;
			for(R = 0; R < Rows; R++) {
				if(R > 0) Matrix &= EstimateDataTypeMatrix(CSVdata[R][C]);
				}
			if(Matrix == 0) {
				Datatype[C] = 'C';
				} else if((Matrix & 0b00000000001) != 0) {
				Datatype[C] = 'O';
				} else if((Matrix & 0b01000000000) != 0) {
				Datatype[C] = 'b';
				} else if((Matrix & 0b10000000000) != 0) {
				Datatype[C] = 'B';
				} else if((Matrix & 0b00010000000) != 0) {
				Datatype[C] = 's';
				} else if((Matrix & 0b00100000000) != 0) {
				Datatype[C] = 'S';
				} else if((Matrix & 0b00000100000) != 0) {
				Datatype[C] = 'i';
				} else if((Matrix & 0b00001000000) != 0) {
				Datatype[C] = 'I';
				} else if((Matrix & 0b00000000010) != 0) {
				Datatype[C] = 'l';
				} else if((Matrix & 0b00000000100) != 0) {
				Datatype[C] = 'L';
				} else {
				Datatype[C] = 'D';
				}
			} else if(ForcedDatatypeLen == 1) { // Manual setting to a specified data type
			Datatype[C] = *ForcedDatatype;
			} else if(C < ForcedDatatypeLen) { // Manual setting by column
			Datatype[C] = ForcedDatatype[C];
			} else { // Not enough columns specified, rest will be void
			Datatype[C] = 'v';
			}
		switch(Datatype[C]) {
			case 'C': Space = 0; if(Verbosity > 1) printf("string.\n"); break;
			case 'O': Space = 1; if(Verbosity > 1) printf("boolean.\n"); break;
			case 'b': Space = 1; if(Verbosity > 1) printf("8 bit integer unsigned.\n"); break;
			case 'B': Space = 1; if(Verbosity > 1) printf("8 bit integer signed.\n"); break;
			case 's': Space = 2; if(Verbosity > 1) printf("16 bit integer unsigned.\n"); break;
			case 'S': Space = 2; if(Verbosity > 1) printf("16 bit integer signed.\n"); break;
			case 'i': Space = 4; if(Verbosity > 1) printf("32 bit integer unsigned.\n"); break;
			case 'I': Space = 4; if(Verbosity > 1) printf("32 bit integer signed.\n"); break;
			case 'l': Space = 8; if(Verbosity > 1) printf("64 bit integer unsigned.\n"); break;
			case 'L': Space = 8; if(Verbosity > 1) printf("64 bit integer signed.\n"); break;
			case 'F': Space = 4; if(Verbosity > 1) printf("32 bit float.\n"); break;
			case 'D': Space = 8; if(Verbosity > 1) printf("64 bit float.\n"); break;
			case 'v': Space = 0; if(Verbosity > 1) printf("void.\n"); break;
			default: Space = 0; Datatype[C] = 'v'; fprintf(stderr, "ERROR: Unknown data type %c. The data type will be changed to void and the column ignored.\n", Datatype[C]);
			}
		Addresses[C] = SpaceTotal;
		FelderD[C] = str4concat(CSVdata[0][C], "/ ", "", "");
		FelderD[C][strlen(CSVdata[0][C]) + 1] = Datatype[C];
		SpaceTotal += Space;
		i = 0;
		while(CSVdata[0][C][i] != 0) { // Convert non Alphanumeric Characters
			if((CSVdata[0][C][i] < 48 || CSVdata[0][C][i] >= 58) && (CSVdata[0][C][i] < 65 || CSVdata[0][C][i] >= 91) && (CSVdata[0][C][i] < 97 || CSVdata[0][C][i] >= 123)) CSVdata[0][C][i] = '_';
			if(i == 256) { // Truncate too long strings. Maybe something went wrong
				CSVdata[0][C][i] = 0;
				fprintf(stderr, "WARNING: The Branch name was truncated after 255 chars. Maybe the separator was set wrong.");
				fprintf(stderr, Verbosity < 2 ? " Try a more verbose option \"-v 2\" for more details.\n" : "\n");
				break;
				}
			i++;
			}
		}
	Datatype[Columns] = 0; // String terminator
	if(Verbosity > 2) printf("Datatype string: \"%s\"\n", Datatype);

	Data = (UChar_t*) malloc(SpaceTotal);
	if(TreeName == NULL) {
		PopExtention(argv[a]); // Argument is a file
		TreeName = argv[a];
		}
	tree = new TTree(TreeName, chtitl); // Make a new tree for each file
	for(C = 0; C < Columns; C++) {
		if(Datatype[C] != 'v') tree->Branch(CSVdata[0][C], Data + Addresses[C], FelderD[C], bufsize);
		}

	StartTime = clock();
	for(R = 1; R < Rows; R++) {
		for(C = 0; C < Columns; C++) {
			Long64_t Value = atol(CSVdata[R][C]);
			switch(Datatype[C]) {
				case 'C' : // string
					tree->GetBranch(CSVdata[0][C])->SetAddress(CSVdata[R][C]);
					break;
				case 'O' : // boolean
					*(Data + Addresses[C]) = StrToBoolean(CSVdata[R][C]);
					break;
				case 'b' : // 8 bit integer unsigned
					*(Data + Addresses[C]) = (UChar_t) (Value & 0xFF);
					break;
				case 'B' : // 8 bit integer signed
					*(Data + Addresses[C]) = (signed Char_t) (Value & 0xFF);
					break;
				case 's' : // 16 bit integer unsigned
					*((UShort_t*) (Data + Addresses[C])) = (UShort_t) (Value & 0xFFFF);
					break;
				case 'S' : // 16 bit integer signed
					*((Short_t*) (Data + Addresses[C])) = (Short_t) (Value & 0xFFFF);
					break;
				case 'i' : // 32 bit integer unsigned
					*((UInt_t*) (Data + Addresses[C])) = (UInt_t) (Value & 0xFFFFFFFF);
					break;
				case 'I' : // 32 bit integer signed
					*((Int_t*) (Data + Addresses[C])) = (Int_t) (Value & 0xFFFFFFFF);
					break;
				case 'l' : // 64 bit integer unsigned
					*((ULong64_t*) (Data + Addresses[C])) = (ULong64_t) (Value & 0xFFFFFFFFFFFFFFFF);
					break;
				case 'L' : // 64 bit integer signed
					*((Long64_t*) (Data + Addresses[C])) = (Long64_t) (Value & 0xFFFFFFFFFFFFFFFF);
					break;
				case 'D' : // 64 bit float
					*((Double_t*) (Data + Addresses[C])) = (Double_t) atof(CSVdata[R][C]);
					break;
				case 'v' : // void
					break;
				default:
					fprintf(stderr, "ERROR: Invalid data type.\n");
					abort();
				}
			}
		tree->Fill();
		if(clock() > NextPrint && Verbosity > 0) {
			NextPrint = clock() + CLOCKS_PER_SEC;
			printf("Working on line %llu of %llu (%llu %%). Time remaining: %llu s     \n\033[1A", R, Rows, 100 * R / Rows, (clock() - StartTime) * (Rows - R) / R / CLOCKS_PER_SEC);
			}
		}
	if(Verbosity > 0) printf("Working on line %llu of %llu (100 %%).                         \n", R, Rows);

	tree->Write(); // Clean up for each file
	delete tree;
	freeCSVfile(CSVdata, Columns);
	free(Datatype);
	free(Addresses);
	for(C = 0; C < Columns; C++) free(FelderD[C]);
	free(FelderD);
	if(Data != NULL) free(Data);
	TreeName = NULL;
	}

if(Verbosity > 2) printf("Cleaning up...\n"); // Final clean up procedure
hfile->Write();
if(Verbosity > 1) hfile->ls();
hfile->Close();
delete hfile;
if(Verbosity > 2) printf("finished.\n");
return 0;
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


void help(char* Exec) {
fprintf(stderr, "CSV2root %s by Diego Semmler (dsemmler ät ikp. physik. tu-darmstadt.de)\n\n", Version);
fprintf(stderr, "Usage: %s [Options] Infile1.csv [Options] [Infile2.csv] ...\n", Exec);
fprintf(stderr, "Global options: -c --compression\n");
fprintf(stderr, "                -t --title\n");
fprintf(stderr, "                -o --outfile\n");
fprintf(stderr, "                -v --verbosity\n");
fprintf(stderr, "File options:   -s --separator\n");
fprintf(stderr, "                -l --lineseparator\n");
fprintf(stderr, "                -b --buffer\n");
fprintf(stderr, "                -t --treetitle\n");
fprintf(stderr, "                -n --name\n");
fprintf(stderr, "                -d --datatypes\n\n");
fprintf(stderr, "Type \"man CSV2root\" for more help\n\n");
}

Char_t StrToBoolean(const Char_t* Str) {
int i = 0, j;
Char_t Lower[6];
while(1) if(Str[i] == 9 || Str[i] == 10 || Str[i] == 13 || Str[i] == 32) i++; else break;
if(Str[i] == 0) return 0;
for(j = 0; j < 6; j++) {
	Lower[i+j] = Str[i+j] | (Str[i+j] >= 65 && Str[i+j] < 90 ? 32 : 0);
	if(Str[i+j] == 0) break;
	}
if(strcmp("t", Lower) == 0) return 1;
if(strcmp("f", Lower) == 0) return 0;
if(strcmp("true", Lower) == 0) return 1;
if(strcmp("false", Lower) == 0) return 0;
if(Str[i] == '+' || Str[i] == '-') i++;
if(Str[i] == 0) return 0;
while(Str[i] != 0) {
	if(Str[i] != 48) return 1;
	i++;
	}
return 0;
}

Char_t* PopExtention(Char_t* String) {
int i = 0, dot = -1;
while(String[i] != 0) {
	i++;
	if(String[i] == '.') dot = i; else if(String[i] == '/' || String[i] == '\\') dot = -1;
	}
if(dot != -1) {
	String[dot] = 0;
	return &String[dot + 1];
	}
return NULL;
}

UShort_t EstimateDataTypeMatrix(Char_t* String) {
UShort_t Matrix = 0b11111111111;
ULong64_t Value = 0;
int i, Len;
Char_t Lower[64], e = 0, dot = 0;

for(Len = 0; Len < 64; Len++) {
	Lower[Len] = String[Len] | (String[Len] >= 65 && String[Len] < 90 ? 32 : 0);
	if(String[Len] == 0) break;
	}
if(Len == 0) return 0b11111111111; // Empty
if(Len == 64) return 0; // Too long, use a string
if(strcmp("t", Lower) == 0) return Matrix;
if(strcmp("f", Lower) == 0) return Matrix;
if(strcmp("true", Lower) == 0) return Matrix;
if(strcmp("false", Lower) == 0) return Matrix;
if(Lower[0] == '-') {
	Matrix = 0b10101011101; // Signed type
	i = 1;
	} else {
	Matrix = 0b11111111111;
	if(Lower[0] == '+') i = 1; else i = 0;
	}
for(; i < Len; i++) {
	if(Lower[i] == 'e') {
		if(e == 0) {
			e = 1;
			Matrix = 0b00000011000; // float type
			if(Lower[i + 1] == '+' || Lower[i + 1] == '-') i++;
			} else return 0; // String
		} else if(Lower[i] == '.') {
		if(dot == 0 && e == 0) {
			dot = 1;
			Matrix = 0b00000011000; // float type
			} else return 0; // String
		} else if(Lower[i] >= 48 && Lower[i] < 58) Value = Value * 10 + String[i] - 48;
		else return 0; //String
	}
if(Value > 1) Matrix &= 0b11111111110;
if(Value > 127) Matrix &= 0b01111111110;
if(Value > 255) Matrix &= 0b00111111110;
if(Value > 32767) Matrix &= 0b00011111110;
if(Value > 65535) Matrix &= 0b00001111110;
if(Value > 2147483647) Matrix &= 0b00000111110;
if(Value > 4294967295) Matrix &= 0b00000011110;
return Matrix;
}

Char_t *str4concat(const Char_t *str1, const Char_t *str2, const Char_t *str3, const Char_t *str4) {
int len1, len2, len3, len4, len;
Char_t *concat;
len1 = strlen(str1);
len2 = strlen(str2);
len3 = strlen(str3);
len4 = strlen(str4);
len = len1 + len2 + len3 + len4;
concat = (Char_t*) malloc(len + 1);
if(concat == NULL) return NULL;
memcpy(concat, str1, len1);
memcpy(concat + len1, str2, len2);
memcpy(concat + len1 + len2, str3, len3);
memcpy(concat + len1 + len2 + len3, str4, len4 + 1);
return concat;
}

Char_t charFromString(const Char_t *str) {
Char_t RetVal;
if(str == NULL) return 0;
if(*str != '\\') return *str;
str++;
switch(*str) {
	case 0: return '\\';
	case 't': return 9;
	case 'n': return 10;
	case 'v': return 11;
	case 'f': return 12;
	case 'r': return 13;
	case 'e': return 27;
	case 's': return 32;
	case 'x': case 'X':
		if(str[1] >= 48 && str[1] <= 57) RetVal = str[1] - 48;
			else if((str[1] & 0xDF) >= 65 && (str[1] & 0xDF) <= 70) RetVal = (str[1] & 0xDF) - 55;
			else return *str;
		if(str[2] >= 48 && str[2] <= 57) RetVal = (RetVal << 4) + str[2] - 48;
			else if((str[2] & 0xDF) >= 65 && (str[2] & 0xDF) <= 70) RetVal = (RetVal << 4) + (str[2] & 0xDF) - 55;
		return RetVal;
	case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7':
		RetVal = str[0] - 48;
		if(str[1] < 48 || str[1] > 55) return RetVal;
		RetVal = (RetVal << 3) + str[1] - 48;
		if(str[2] < 48 || str[2] > 55) return RetVal; else return (RetVal << 3) + str[2] - 48;
	default: return *str;
	}
}
