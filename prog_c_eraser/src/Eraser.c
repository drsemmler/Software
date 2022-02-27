// Eraser 1.0
// by Diego Semmler www.dsemmler.de

#include <stdlib.h>
#include <conio.h>
#include <conio.c>
#include <ctime>
#include <sys/types.h>
#include <dirent.h>

char CommandErase(char *File);
char Erase(char *File);
long int Filesize(FILE *fp);
void Help();
char *str4concat(char *str1, char *str2, char *str3, char *str4);

char del = 0, rec = 0;
char *cmd = "", *cmdf = "";

int main(int argc, char *argv[]) {
int i, u;
char ipp = 0, success, *buffer;
if(argc < 2) {
  Help();
  system("PAUSE");
  return 0;
  }
for(i = 1; i < argc; i++) if(argv[i][0] == 45) {
  if(argv[i][1] == 45) {
    if(strncmp(argv[i],      "--help", 7)       == 0) argv[i] = "-h";
    else if(strncmp(argv[i], "--success", 10)   == 0) argv[i] = "-s";
    else if(strncmp(argv[i], "--fail", 7)       == 0) argv[i] = "-f";
    else if(strncmp(argv[i], "--delete", 9)     == 0) argv[i] = "-d";
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
      case 115: // -s
        cmd = argv[i + 1];
        ipp = 1;
        break;
      case 102: // -f
        cmdf = argv[i + 1];
        ipp = 1;
        break;
      case 100: // -d
        del = 1;
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
  success = CommandErase(argv[i]);
  }
return 0;
}

char CommandErase(char *File) {
int CT;
char retval;
DIR *dirHandle;
struct dirent * dirEntry;

textbackground(0);
printf("Eraseing \"%s\" ...\n", File);
CT = clock();
if(retval = Erase(File)) {
  textcolor(10);
  printf("OK - %d Millisekunden gebraucht.\n", clock() - CT);
  if(strlen(cmd)) system(str4concat(cmd, " \"", File, "\""));
  if(del) remove(File);
  } else {
  if(dirHandle = opendir(File)) {
    retval = 1;
    if(rec) while (0 != (dirEntry = readdir(dirHandle)))
        if(strncmp(dirEntry->d_name, ".", 1) && strncmp(dirEntry->d_name, "..", 2))
          retval &= CommandErase(str4concat(File, "/", dirEntry->d_name, ""));
    closedir(dirHandle);
    if(del) rmdir(File);
    } else {
    textcolor(12);
    printf("FEHLER!\n");
    if(strlen(cmdf)) system(str4concat(cmdf, " \"", File, "\""));
    }
  }
textcolor(7);
return retval;
}

char Erase(char *File) {
int Size, i;
FILE *F;
F = fopen(File, "r+b");
if (F == NULL) return 0; // Datei existiert nicht.
Size = Filesize(F);
srand(rand() ^ (rand() << 15) ^ time(NULL));

for(i = 1; i <= Size; i++) putc(85, F);
fclose(F);
F = fopen(File, "r+b");
for(i = 1; i <= Size; i++) putc(170, F);
fclose(F);
F = fopen(File, "r+b");
for(i = 1; i <= Size; i++) putc(rand(), F);
fclose(F);
F = fopen(File, "r+b");
for(i = 1; i <= Size; i++) putc(0, F);
fclose(F);
return 1;
}

long int Filesize(FILE *fp) {
long int fSize, fpos;
fpos = ftell(fp);
fseek(fp, 0, SEEK_END);
fSize = ftell(fp);
fseek(fp, fpos, SEEK_SET);
return fSize;
}

void Help() {
textcolor(15);
printf("\n                                   Eraser 1.0\n                                   ==========\n\n");
textcolor(11);
printf("Syntax:\n");
textcolor(7);
printf("Eraser [-hsfdr [Befehl]] \"Zu loeschende Datei 1\" Datei2 Datei3 ...\n\n");
textcolor(11);
printf("Parameter:");
textcolor(7);
printf("
-h  --help
-s  --success Befehl  Fuehrt Befehl nach erfolgreichem Ueberschreiben aus.
-f  --fail    Befehl  Fuehrt Befehl aus, falls das Ueberschreiben nicht
                      erfolgreich war.
-d  --delete          Loescht die Dateien von der Festplatte
                      (ansonsten werden sie nur ueberschrieben)
-r  --recursive       Loescht komplette Unterordner

Programmiert von ");
textcolor(14);
printf("Diego Semmler");
textcolor(9);
printf("   (www.dsemmler.de)\n");
textcolor(7);
}

char *str4concat(char *str1, char *str2, char *str3, char *str4) {
int len1, len2, len3, len4, len;
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
