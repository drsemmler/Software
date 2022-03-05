Eraser
======
Eraser entfernt Dateien von der Festplatte, sodass sie ohne größten Aufwand
nicht mehr wiederherstellbar sind.

Wichtige Grenzen
----------------
Das Programm vertraut darauf, dass das Betriebssystem die Daten wieder an
die selbe Stelle schreibt. Sollten die Daten an eine andere Stelle geschrieben
werden oder in einem Cache fest hängen, hat das Programm nicht die erwünschte
Wirkung. Dies trifft unter folgenden Umständen auf:
 - Bei Flash Speichern, wie SSDs, USB Sticks oder Speicherkarten
 - Bei defekten Festplattensektoren
 - Bei eingeschalteter Deduplizierung (nicht unter Windows)


Version:	1.1
Bedienung:	Eraser wird über die Kommandozeile bedient. Benutze folgendes Schema:
	Eraser -rd Datei1.doc Datei2.test "Datei mit Leerzeichen.xxy" Ordner1
	löscht die drei oben angegebenen Dateien und den Ordner.
	Wenn dir die Kommandozeile nicht zusagt, kannst du die zu löschenden Dateien
	(keine Ordner) per Drag & Drop auf das Programm ziehen.
Kompilierung unter Linux:
	make all in diesem Ordner kompiliert das Programm
	make install kompiliert das Programm und legt die kompilierte Version unter /bin
	ab. (Administratorrechte erforderlich)
Tipps:	Unter Windows kann man das Programm schneller aufrufen, wenn man eine Verknüpfung
	im Kontext-Menü anlegt.
	Falls du die Datei Eraser.exe in dein Windows-Verzeichnis kopierst, hast du mit
	der Eingabeaufforderung überall Zugriff darauf.
	Damit sich unter Windows das Programm nach getaner Arbeit nicht sofort beendet,
	solltest du die Anweisung system("PAUSE"); in Zeile 29 entkommentieren.
Optionen:
	-h  --help              Gibt diesen Text aus und beendet das Programm.
	-b  --blocksize Größe   Überschreibt immer ein Vielfaches der angegebenen
	                        Größe. (Standard = 4096 Byte)
	-m  --method    Methode Jedes Zeichen steht für einen Überschreibvorgang.
	                        0 bis F steht für das entsprechende 4 Bit Muster,
	                        r für Pseudozufällige Daten. (Standard = 5Ar0)
	-s  --success   Befehl  Führt Befehl nach erfolgreichem Überschreiben aus.
	-f  --fail      Befehl  Führt Befehl aus, falls das Überschreiben nicht
	                        erfolgreich war.
	-d  --delete            Löscht die Dateien von der Festplatte (Standard)
	-n  --nodelete          Überschreibt die Dateien lediglich
	-r  --recursive         Löscht komplette Unterordner
Funktionsweise:	Alle Bytes der zu löschenden Datei werden zunächst mit den
	Mustern 010101... und 101010... überschrieben, anschließend mit zufällig
	generierten Zahlen und zuletzt mit Nullen.n Das Muster lässt sich mit der
	Option m ändern.
