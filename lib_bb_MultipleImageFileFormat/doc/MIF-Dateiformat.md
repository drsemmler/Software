				Datentypen
				==========
int(n)	Integer mit n bytes (signed für n >= 4)
string	Zeichenkette ohne Längenangabe und Endzeichen
color	RGB-Farbwert bestehend aus:
	int(1)	Rot
	int(2)	Grün
	int(1)	Blau

				Dateikopf
				=========
int(4)	1481000739 ("#GFX")
int(2)	Länge des Kommentares
string	Kommentar
int(2)	Anzahl der Bilder

				Für jedes Bild
				==============
int(4)	Größe (innerhalb der Datei)
int(2)	Breite
int(2)	Höhe
int(1)	Frames
int(1)	Frame Anzeigedauer
int(1)	Bildtyp
	1 = Farbe
	2 = Horizontaler Farbverlauf
	3 = Vertikaler Farbverlauf
	4 = Bild (unkomprimiert)
	5 = Bild (RLE-Komprimiert)
	6 = Bild (YUV-Format)
	7 = Bild (8 Bit Palette)
	8 = Bild (16 Bit Palette)
color	Transparente Farbe
				Farbe
				=====
color	Farbe

			Horizontaler Farbverlauf
			========================
color	Farbe oben
color	Farbe unten
			Vertikaler Farbverlauf
			======================
color	Farbe links
color	Farbe rechts

			Bild (unkomprimiert)
			====================
für jede Zeile
	für jedes Pixel
		color	Farbe

			Bild (RLE-Komprimiert)
			======================
für alle Pixel gleicher Farbe hintereinander
	int(1)	Anzahl gleicher Pixel
	color	Farbe

			Bild (YUV-Format)
			=================
für jede Doppel-Zeile
	für jedes Doppel-Pixel
		int(1)	R-Wert
		int(1)	G-Wert
		int(1)	Helligkeit (0|0)
		int(1)	Helligkeit (1|0)
		int(1)	Helligkeit (0|1)
		int(1)	Helligkeit (1|1)