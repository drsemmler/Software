Include "MIF LoadImages.bi"

Graphics 400, 300, 0, 2
AppTitle "MIF-Viever"
SetFont LoadFont("Arial", 20)
File$ = CommandLine()
If File$ = "" Then File$ = Input("Datei: ")
If File$ = "" Then End
Comment$ = GetMifComment$(File$)
Print "Datei " + Chr$(34) + File$ + Chr$(34) + " wird geladen ..."
Ret% = LoadImages(File$)
If Ret% = -1 Then RuntimeError "Fehler! Die Datei konnte nicht geöffnet werden."
If Ret% = -2 Then RuntimeError "Fehler! Die Datei ist keine gültige MIF-Datei"
If Ret% <= 0 Then RuntimeError "Unbekannter Fehler bein Öffnen der Datei."
Global CurPic% = 1, Schrift% = CreateImage(GraphicsWidth(), 100)
Global MinX%, MinY%, MaxX%, MaxY%
PreparePicture
Timer% = CreateTimer(40)

While Not KeyDown(1) Or ((KeyDown(56) Or KeyDown(184)) And KeyDown(62))
	If KeyDown(200) Then Y% = Y% - 5
	If KeyDown(203) Then X% = X% - 5
	If KeyDown(205) Then X% = X% + 5
	If KeyDown(208) Then Y% = Y% + 5
	If KeyDown(201) Then
		CurPic% = CurPic% + 1
		If CurPic% > Ret% Then CurPic% = 1
		While KeyDown(201)
			Delay 1
			Wend
		EndIf
	If KeyDown(209) Then
		CurPic% = CurPic% - 1
		If CurPic% < 1 Then CurPic% = Ret%
		While KeyDown(209)
			Delay 1
			Wend
		EndIf
	If KeyDown(57) Then Pic% = 0 Else Pic% = GFX%(CurPic%)
	If KeyDown(59) Then
		Help% = 1
		Pic% = 0
		Else Help% = 0
		EndIf
	If X% < MinX% Then X% = MinX%
	If Y% < MinY% Then Y% = MinY%
	If X% > MaxX% Then X% = MaxX%
	If Y% > MaxY% Then Y% = MaxY%
	If Pic% <> AltPic% Or Help% <> AltHelp% Or X% <> AltX% Or Y% <> AltY% Then
		Cls
		Draw Pic%, X%, Y%, Help%
		AltPic% = Pic%
		AltHelp% = Help%
		AltX% = X%
		AltY% = Y%
		EndIf
	WaitTimer Timer%
	Wend
End

Function PreparePicture()
MinX% = 0
MinY% = 0
MaxX% = GraphicsWidth() - ImageWidth(GFX%(CurPic%))
MaxY% = GraphicsHeight() - ImageHeight(GFX%(CurPic%))
If MinX% > MaxX% Then
	Temp% = MinX%
	MinX% = MaxX%
	MaxX% = Temp%
	EndIf
If MinY% > MaxY% Then
	Temp% = MinY%
	MinY% = MaxY%
	MaxY% = Temp%
	EndIf
SetBuffer ImageBuffer(Schrift%)
Cls
Text 0, 0, "Datei: " + File$
Text 0, 20, "Datei-Größe: " + Str(FileSize(File$) / 1024) +  " KB"
Text 0, 40, "Bild-Breite: " + Str(ImageWidth(GFX%(CurPic%))) + " px"
Text 0, 60, "Bild-Höhe: " + Str(ImageHeight(GFX%(CurPic%))) + " px"
Text 0, 80, "Kommentar: " + Comment$
SetBuffer BackBuffer()
End Function

Function Draw(Bild%, X% = 0, Y% = 0, Help% = 1)
DrawBlock Schrift%, 0, 0
If Help% Then
	Locate 0, 125
	Color 0, 255, 255
	Print "Tastenbelegung:"
	Color 255, 255, 255
	Print "[Esc] oder [Alt] + [F4]: Beenden"
	Print "[F1]: Hilfe"
	Print "[Leertaste]: Bild ausblenden"
	Print "[Pfeiltasten]: Bild bewegen"
	Print "[Bild Hoch / runter]: Bild wechseln"
	Print
	Color 255, 255, 0
	Print "MIF-Viewer by Diego Semmler www.dsemmler.de"
	Print "MIF-Fileformat by D. Semmler www.dsemmler.de"
	Color 255, 255, 255
	EndIf
If Bild% Then DrawBlock Bild%, X%, Y%
Flip
End Function