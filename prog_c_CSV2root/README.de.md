CSV2root
========
CSV2root wandelt ein oder mehrere Tabellen im .csv-Format in einen Root-Tree um. Das
Programm "Root" wird vom Cern entwickelt und wird in der Teilchen- und Kernphysik zur
Datenanalyse verwendet.

Version:	0.2
Installation:	Das Programm muss zuerst compiliert werden. Der entsprechende Befehl
	steht am Anfang der Datei CSV2root.c und kann dann in das Path-Verzeichnis (siehe
	unten) kopiert werden. Das Script CSV2root_installer.sh übernimmt diese Schritte
	auf unixoiden Betriebssystemen z.B. Linux bei entsprechenden Rechten automatisch.
Bedienung:	CSV2root wird über die Kommandozeile bedient. Benutze dazu folgendes Schema:
	./CSV2root -o Ausgabe.root [Optionen] Datei1.csv [Mehr Optionen] [Datei2.csv] ...
	Dieser Aufruf legt die Datei Ausgabe.root an und schreibt einen Tree für jede
	angegebenen CSV Datei hinein. In der ersten Zeile der CSV Datei müssen die Namen
	der Spalten stehen. Der Datentyp wird automatisch so festgelegt, dass er ohne
	Datenverlust möglichst wenig Speicher verbraucht.

	Eine Komplette Bedienungsanleitung findet sich auf der Man-Page (englisch).
Tipps:	Am besten ist es die Binärdatei "CSV2root" in sein "/bin"-Verzeichnis zu
	platzieren. Dann kann man das Programm von jedem Pfad aufrufen.
	Wenn man die Datei "CSV2root.1" in sein "/usr/share/man/man1"-Verzeichnis legt,
	hat man mit dem Befehl man CSV2root jederzeit Zugriff auf die Hilfe.

Grenzen: 	Das Programm wählt im Moment bei Float-Variablen immer den 64-Bit Typ.
