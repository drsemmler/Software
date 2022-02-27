// Code by Diego Semmler (dsemmler äd ikp. physik. tu-darmstadt.de)

// Reads a CSV table into a Char_t*** array
// TrimWithQuotes of the raw csv-data is done. Quotes ("'`) are respected, and removed.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <iostream>
#include "TFile.h"
#include "TTree.h"

const char* Quotes = "\"'`";

Char_t* TrimWithQuotes(Char_t* String) {
int i = 0;
while(*String == 9 || *String == 10 || *String == 13 || *String == 32) String++;
while(String[i] != 0) i++;
i--;
while((String[i] == 9 || String[i] == 10 || String[i] == 13 || String[i] == 32) && i >= 0) String[i--] = 0;
if(String[i] == String[0] && (String[0] == 34 || String[0] == 39 || String[0] == 96)) {
	String++;
	String[--i] = 0;
	}
return String;
}

ULong64_t FileSize(FILE* Stream) { // This is only an estimation and does not guarranty to return the correct value.
ULong64_t CurPos, Size;
CurPos = ftell(Stream);
if(fseek(Stream, 0, SEEK_END) != 0) return 0;
Size = ftell(Stream);
fseek(Stream, CurPos, SEEK_SET);
return Size;
}

void* ReadFile(Char_t* FileName, ULong64_t* Size) { // 0-Terminated
void* Memory;
void* NewMemory;
FILE* FPointer;
ULong64_t AllocatedSize;
FPointer = fopen(FileName, "r");
if(FPointer == NULL) {
	fprintf(stderr, "The file \"%s\" not found.\n", FileName);
	return NULL;
	}
AllocatedSize = FileSize(FPointer);
Memory = malloc(AllocatedSize + 1); // One extra to terminate string
if(Memory == NULL) {
	fprintf(stderr, "The required memory of \"%lld\" bytes could nor be allocated.\n", AllocatedSize + 1);
	return NULL;
	}
*Size = fread(Memory, 1, AllocatedSize, FPointer);
while(feof(FPointer) != 0) { // The estimation of FileSize was wrong, we have not read everything yet.
	if(*Size == AllocatedSize) { // We need more memory
		AllocatedSize += 65536;
		Memory = realloc(Memory, AllocatedSize + 1);
		if(Memory == NULL) {
			free(Memory);
			fprintf(stderr, "The required memory of \"%lld\" bytes could not be allocated.\n", AllocatedSize + 1);
			return NULL;
			}
		}
	*Size += fread((((Char_t*) Memory) + *Size), 1, AllocatedSize - *Size, FPointer); // Try to fill the rest of the buffer at once.
	}
fclose(FPointer);
*(((Char_t*) Memory) + *Size) = 0; // Terminator
if(AllocatedSize != *Size) { // We have allocated to much memory. Let's free what we don't need.
	NewMemory = realloc(Memory, *Size + 1);
	if(NewMemory != NULL) return NewMemory;
	}
return Memory;
}

void freeCSVfile(Char_t*** CSVdata, ULong64_t Columns) {
free(*(CSVdata + Columns)); // Free the file data (The Pointer of the given address always points to the first element in the memory which is used to free it.)
free(*CSVdata); // Free pointers to file data
}

Char_t*** readCSVfile(Char_t* Filename, ULong64_t* Columns, ULong64_t* Rows, Char_t Seperator, Char_t LineBreak, Char_t Comment, Bool_t ShowStatus) {
void* Buffer;
Char_t*** Pointer;
Char_t*** NewPointer;
Char_t StringMode = 0;
Char_t* Pos;
clock_t NextPrint, StartTime;
ULong64_t i = 0, Column = 0, RowBuffer = 1024, Size;

*Rows = 0;
*Columns = 1;
Buffer = ReadFile(Filename, &Size);
if(Buffer == NULL) return NULL;
for(Pos = (Char_t*) Buffer; Pos < (Char_t*) Buffer + Size; Pos++) { // Read the first line and check how many colums we have
	if(*Pos == 34 && StringMode == 0) StringMode = 1;
	else if(*Pos == 34 && StringMode == 1) StringMode = 0;
	else if(*Pos == 39 && StringMode == 0) StringMode = 2;
	else if(*Pos == 39 && StringMode == 2) StringMode = 0;
	else if(*Pos == 96 && StringMode == 0) StringMode = 3;
	else if(*Pos == 96 && StringMode == 3) StringMode = 0;
	else if(*Pos == Seperator && StringMode == 0) (*Columns)++;
	else if((*Pos == LineBreak || *Pos == Comment) && StringMode == 0) break;
	}
if(StringMode != 0) {
	fprintf(stderr, "WARNING: String (%c) from the first line not closed. CSV data is useless.\n", Quotes[StringMode - 1]);
	return NULL;
	}
Pointer = (Char_t***) calloc(RowBuffer * (*Columns), sizeof(Char_t*));
if(Pointer == NULL) {
	free(Buffer);
	return NULL;
	}

Column = 1;
Pointer[0] = (Char_t**) Buffer;
NextPrint = clock() + CLOCKS_PER_SEC / 10;
StartTime = clock();
for(Pos = (Char_t*) Buffer; Pos < (Char_t*) Buffer + Size; Pos++) { // Parse the buffer
	if(*Pos == 34 && StringMode == 0) StringMode = 1;
	else if(*Pos == 34 && StringMode == 1) StringMode = 0;
	else if(*Pos == 39 && StringMode == 0) StringMode = 2;
	else if(*Pos == 39 && StringMode == 2) StringMode = 0;
	else if(*Pos == 96 && StringMode == 0) StringMode = 3;
	else if(*Pos == 96 && StringMode == 3) StringMode = 0;
	else if(*Pos == Comment && StringMode == 0) { // Ignore the rest of the line
		StringMode = 4;
		*Pos = 0;
		}
	else if(*Pos == Seperator && StringMode == 0 && Column < (*Columns)) {
		*Pos = 0;
		Pointer[(*Rows) * (*Columns) + Column - 1] = (char**) TrimWithQuotes((char*) Pointer[(*Rows) * (*Columns) + Column - 1]);
		Pointer[(*Rows) * (*Columns) + Column] = (Char_t**) (Pos + 1);
		Column++;
		}
	else if(*Pos == LineBreak && (StringMode == 0 || StringMode == 4)) {
		StringMode = 0;
		if((*((char*) Pointer[(*Rows) * (*Columns)]) != LineBreak && *((char*) Pointer[(*Rows) * (*Columns)]) != 0) || Column != 1) { // Ignore blank lines
			for(; Column < (*Columns); Column++) Pointer[(*Rows) * (*Columns) + Column] = (Char_t**) Pos; // Set unspecified columns to empty string
			*Pos = 0;
			Pointer[(*Rows) * (*Columns) + Column - 1] = (char**) TrimWithQuotes((char*) Pointer[(*Rows) * (*Columns) + Column - 1]);
			(*Rows)++;
			if((*Rows) == RowBuffer) {
				RowBuffer *= 2;
				NewPointer = (Char_t***) realloc(Pointer, RowBuffer * (*Columns) * sizeof(Char_t*));
				if(NewPointer == NULL) {
					free(Buffer);
					free(Pointer);
					return NULL;
					}
				Pointer = NewPointer;
				}
			}
		while(*(Pos + 1) == 9 || *(Pos + 1) == 10 || *(Pos + 1) == 13 || *(Pos + 1) == 32) Pos++; // LTrim Lines
		Pointer[(*Rows) * (*Columns)] = (Char_t**) (Pos + 1);
		Column = 1;
		}
	if(ShowStatus) {
		if(clock() > NextPrint) {
			NextPrint = clock() + CLOCKS_PER_SEC;
			printf("Analyzing CSV data (%llu %% done) ... Time remaining: %llu s  \n\033[1A", 100 * (Pos - (char*) Buffer) / Size, (clock() - StartTime) * (Size - (Pos - (char*) Buffer)) / (Pos - (char*) Buffer) / CLOCKS_PER_SEC);
			}
		}
	}
if(StringMode != 0) {
	fprintf(stderr, "WARNING: String (%c) not closed. CSV data is useless.\n", Quotes[StringMode - 1]);
	free(Buffer);
	free(Pointer);
	return NULL;
	}
for(; Column < (*Columns); Column++) Pointer[(*Rows) * (*Columns) + Column] = (Char_t**) Pos; // Empty string
(*Rows)++;
NewPointer = (Char_t***) realloc(Pointer, ((*Rows) * ((*Columns) + 1) + 1) * sizeof(Char_t*));
if(NewPointer == NULL) {
	free(Buffer);
	free(Pointer);
	return NULL;
	}
Pointer = NewPointer;
printf("Analyzing of CSV data is done.                               \n");

for(i = 0; i < (*Rows); i++) Pointer[(*Rows) * (*Columns) + i] = (Char_t**) (Pointer + i * (*Columns));
Pointer[((*Rows) + 1) * (*Columns)] = (Char_t**) Buffer; // For Freeing Buffer
return &Pointer[(*Rows)-- * (*Columns)];
}
