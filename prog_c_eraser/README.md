Eraser
======
Eraser entfernt Dateien von der Festplatte, sodass sie ohne größten Aufwand nicht mehr wiederherstellbar sind.
Version:	1.00
Bedienung:	Eraser wird über die Kommandozeile bedient. Benutze folgendes Schema:
	Eraser -rd Datei1.doc Datei2.test "Datei mit Leerzeichen.xxy" Ordner1
	löscht die drei oben angegebenen Dateien und den Ordner.
	Wenn dir die Kommandozeile nicht zusagt, kannst du die zu löschenden Dateien
	(keine Ordner) per Drag & Drop auf das Programm ziehen.
Tipps:	Unter Windows kann man das Programm schneller aufrufen, wenn man eine Verknüpfung
	im Kontext-Menü anlegt.
	Falls du die Datei Eraser.exe in dein Windows-Verzeichnis kopierst, hast du mit
	der Eingabeaufforderung überall Zugriff darauf.
	Um den C-Code unter Unix / Linux kompilieren zu können, musst du dir die conio.h
	und conio.c in dein Include-Verzeichnis legen und die Anweisung
	'system("PAUSE");' in der 25. Zeile löschen.

Optionen:
	-h	--help	Zeigt die Hilfe an.
	-s	--success	Führt einen Befehl aus, falls das Überscheiben erfolgreich war.
	-f	--fail	Führt einen Befehl aus, falls das Überscheiben nicht erfolgreich war.
	-d	--delete	Löscht die Datei, falls das Überscheiben erfolgreich war.
	-r	--recursive	Löscht auch Unterordner.
Funktionsweise:	Alle Bytes der zu löschenden Datei werden zunächst mit den
	Mustern 010101... und 101010... überschrieben, anschließend mit zufällig
	generierten Zahlen und zuletzt mit Nullen.
Grenzen:	Das Programm vertraut darauf, dass das Betriebssystem die Daten wieder an
	die selbe Stelle schreibt. Sollten die Daten (z.B. wegen eines defekten
	Festplattensektors oder aus welchem Grund auch immer) an eine andere Stelle
	geschrieben werden oder in einem Cache fest hängen, hat das Programm nicht die
	volle Wirkung.
	Weiterhin sollte man darauf achten, wenn man eine verschlüsselte Partition
	benutzt, dass eine große Anzahl von Nullen die Sicherheit der Verschlüsselung
	nicht gefährdet.
