// Eraser 1.1
// by Diego Semmler www.dsemmler.de

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>

char CommandErase(char *File);
char Erase(char *File);
long long Filesize(FILE *fp);
void Help();
long long Str2Int(char* Str);
char *str4concat(char *str1, char *str2, char *str3, char *str4);

int del = 1, rec = 0;
char *cmd = "", *cmdf = "", *method = "5Ar0";
char* buffer;
size_t blocksize = 4096;

int main(int argc, char *argv[]) {
int i, u, ipp = 0;

if(argc < 2) {
  Help();
  //system("PAUSE"); // Activate this under Windows to keep the command prompt open at the end of the program.
  return 0;
  }
buffer = (char*) malloc(blocksize);
if(buffer == NULL) {
  printf("FEHLER: Der Puffer von konnte nicht initialisiert werden.\n");
  return 1;
  }
for(i = 1; i < argc; i++) if(argv[i][0] == 45) {
  if(argv[i][1] == 45) {
    if(strncmp(argv[i],      "--help", 7)       == 0) argv[i] = "-h";
    else if(strncmp(argv[i], "--blocksize", 12) == 0) argv[i] = "-b";
    else if(strncmp(argv[i], "--method", 9)     == 0) argv[i] = "-m";
    else if(strncmp(argv[i], "--success", 10)   == 0) argv[i] = "-s";
    else if(strncmp(argv[i], "--fail", 7)       == 0) argv[i] = "-f";
    else if(strncmp(argv[i], "--delete", 9)     == 0) argv[i] = "-d";
    else if(strncmp(argv[i], "--nodelete", 11)  == 0) argv[i] = "-n";
    else if(strncmp(argv[i], "--recursive", 12) == 0) argv[i] = "-r";
    else {
      printf("WARNUNG: Die Option \"%s\" ist unbekannt und wird nicht verwendet.\n", argv[i]);
      argv[i] = "-";
      }
    }
  u = 1;
  while(argv[i][u] != 0) {
    switch(argv[i][u]) {
      case 104: // -h
        Help();
        return 0;
      case 98: // -b
        if(argc > i + 1) blocksize = (size_t) Str2Int(argv[i + 1]);
        if(blocksize <= 0) blocksize = 1;
        buffer = (char*) realloc(buffer, blocksize);
        if(buffer == NULL) {
          printf("FEHLER: Der Puffer von konnte nicht initialisiert werden.\n");
          return 2;
          }
        ipp = 1;
        break;
      case 109: // -m
        if(argc > i + 1) method = argv[i + 1];
        ipp = 1;
        break;
      case 115: // -s
        if(argc > i + 1) cmd = argv[i + 1];
        ipp = 1;
        break;
      case 102: // -f
        if(argc > i + 1) cmdf = argv[i + 1];
        ipp = 1;
        break;
      case 100: // -d
        del = 1;
        break;
      case 110: // -n
        del = 0;
        break;
      case 114: // -r
        rec = 1;
        break;
      default:
        printf("WARNUNG: Die Option \"-%c\" ist unbekannt und wird nicht verwendet.\n", argv[i][u]);
      }
    u++;
    }
  i += ipp;
  ipp = 0;
  } else {
  CommandErase(argv[i]);
  }
free(buffer);
return 0;
}

char CommandErase(char *File) {
long CT;
char retval;
DIR *dirHandle;
struct dirent * dirEntry;

printf("Eraseing \"%s\"", File);
CT = clock();
if(retval = Erase(File)) {
  printf("OK - %ld Millisekunden gebraucht.\n", clock() - CT);
  if(strlen(cmd)) system(str4concat(cmd, " \"", File, "\""));
  if(del) remove(File);
  } else {
  if(dirHandle = opendir(File)) {
    retval = 1;
    if(rec) while (0 != (dirEntry = readdir(dirHandle)))
        if(strncmp(dirEntry->d_name, ".", 2) && strncmp(dirEntry->d_name, "..", 3))
          retval &= CommandErase(str4concat(File, "/", dirEntry->d_name, ""));
    closedir(dirHandle);
    if(del) rmdir(File);
    } else {
    printf("\nFEHLER!\n");
    if(strlen(cmdf)) system(str4concat(cmdf, " \"", File, "\""));
    }
  }
return retval;
}

char Erase(char *File) {
int Error = 0, M, u = 0;
size_t Size, blocknr, byte, blocks;
FILE *F;

F = fopen(File, "r+b");
if (F == NULL) return 0; // Datei existiert nicht.
Size = (size_t) Filesize(F);
printf(" (%ld KB) ...\n", (Size + 512) >> 10);
srand(rand() ^ (rand() << 15) ^ time(NULL) ^ Size);
blocks = (Size - 1) / blocksize + 1;
fclose(F);
while(method[u] != 0) {
  if(method[u] >= 48 && method[u] <= 57) M = method[u] - 48;
    else if(method[u] >= 65 && method[u] <= 70) M = method[u] - 55;
    else if(method[u] >= 97 && method[u] <= 102) M = method[u] - 87;
    else if(method[u] == 82 || method[u] == 114) M = 256;
    else {
    printf("WARNUNG: Die Methode \"%c\" ist nicht bekannt und wird ignoriert.\n", method[u]);
    u++;
    continue;
    }
  if(M < 16) for(byte = 0; byte < blocksize; byte++) buffer[byte] = (char) (M << 4) | M;
    
  F = fopen(File, "r+b");
  setbuf(F, NULL); // Disable buffering
  for(blocknr = 0; blocknr < blocks; blocknr++) {
    if(M == 256) for(byte = 0; byte < blocksize; byte++) buffer[byte] = (char) (rand() & 0xFF);
    if(fwrite(buffer, 1, blocksize, F) != blocksize) Error = 1;
    }
  fclose(F);
  u++;
  }
return !Error;
}

long long Filesize(FILE *fp) {
long long fSize, fpos;
fpos = ftell(fp);
fseek(fp, 0, SEEK_END);
fSize = ftell(fp);
fseek(fp, fpos, SEEK_SET);
return fSize;
}

void Help() {
printf("\n                                   Eraser 1.1\n                                   ==========\n\n");
printf("Syntax:\n");
printf("Eraser [Parameter] \"Zu loeschende Datei 1\" [Datei2 [Datei3 [...]]]\n\n");
printf("Parameter:\n");
printf("-h  --help              Gibt diesen Text aus und beendet das Programm.\n");
printf("-b  --blocksize Groesse Ueberschreibt immer ein Vielfaches der angegebenen\n");
printf("                        Groesse. (Standard = 4096 Byte)\n");
printf("-m  --method    Methode Jedes Zeichen steht fuer einen Ueberschreibvorgang.\n");
printf("                        0 bis F steht fuer das entsprechende 4 Bit Muster,\n");
printf("                        r fuer Pseudozufaellige Daten. (Standard = 5Ar0)\n");
printf("-s  --success   Befehl  Fuehrt Befehl nach erfolgreichem Ueberschreiben aus.\n");
printf("-f  --fail      Befehl  Fuehrt Befehl aus, falls das Ueberschreiben nicht\n");
printf("                        erfolgreich war.\n");
printf("-d  --delete            Loescht die Dateien von der Festplatte (Standard)\n");
printf("-n  --nodelete          Ueberschreibt die Dateien lediglich\n");
printf("-r  --recursive         Loescht komplette Unterordner\n");
printf("\n");
printf("Programmiert von ");
printf("Diego Semmler");
printf("   (www.dsemmler.de)\n");
}

long long Str2Int(char* Str) {
long long i = 0, Val = 0;
while(Str[i] >= 48 && Str[i] <= 57) {
  Val = Val * 10 + Str[i] - 48;
  i++;
  }
if(Str[i] == 107 || Str[i] == 75) Val = Val << 10; // K
if(Str[i] == 109 || Str[i] == 77) Val = Val << 20; // M
if(Str[i] == 103 || Str[i] == 71) Val = Val << 30; // G
if(Str[i] == 116 || Str[i] == 84) Val = Val << 30; // T
return Val;
}

char *str4concat(char *str1, char *str2, char *str3, char *str4) {
long len1, len2, len3, len4, len;
char *concat;
len1 = strlen(str1);
len2 = strlen(str2);
len3 = strlen(str3);
len4 = strlen(str4);
len = len1 + len2 + len3 + len4;
concat = malloc(len + 1);
if(concat == NULL) return NULL;
memcpy(concat, str1, len1);
memcpy(concat + len1, str2, len2);
memcpy(concat + len1 + len2, str3, len3);
memcpy(concat + len1 + len2 + len3, str4, len4 + 1);
return concat;
}
