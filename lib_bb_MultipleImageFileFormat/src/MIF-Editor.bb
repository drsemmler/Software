Include "F:\Eigene Dateien\Projekte\MyGUI\GUI.bi"
Include "F:\Eigene Dateien\Blitz\BlitzSys V1.05\blitzsys.bb"
Const Title$ = "MIF-Editor 1.2"
Global FileFilter$ = "Bild-Dateien (*.jpg, *.png, *.bmp)" + Chr$(0) + "*.jpg; *.png; *.bmp" + Chr$(0) + "JPEG-Dateien (*.jpg)" + Chr$(0) + "*.jpg" + Chr$(0) + "Portable Netzwerk Grafiken (*.png)" + Chr$(0) + "*.png" + Chr$(0) + "Bitmaps (*.bmp)" + Chr$(0) + "*.bmp" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*" + Chr$(0)
Global Mouse%, Selection% = 1, SelectionO%, DoubleClick%, MenuWidth%
Dim FrameDelay%(256)
Dim Disable%(11)
Dim Dateiname$(24), ImageHandle%(24), Bildbreite%(24), Bildhoehe%(24), Frames%(24), Speicherart%(24)
Dim Farben%(24, 3)
Dim Farbtabelle%(257)
Disable%(5) = 1
Disable%(8) = 1
Disable%(9) = 1

Graphics 640, 480, 0, 2
AppTitle Title$
ClsColor 0, 0, SwapRGB(GetSysColor(COLOR_MENU))
Global SaveFile$ = CommandLine()
For I% = 1 To 24
	Frames%(I%) = 1
	Speicherart%(I%) = 1
	Next
For I% = 1 To LoadImages(SaveFile$, 24)
	Dateiname$(I%) = "#" + Str(I%)
	Next
SetBuffer BackBuffer()
ChangeDir SystemProperty("appdir")
Global ColorBank% = DLLCreateColorBank()
SetFont LoadFont("Arial", 20)
Dim Speicherarten$(7)
Speicherarten$(1) = "Farbe"
Speicherarten$(2) = "Farbverlauf (Horizontal)"
Speicherarten$(3) = "Farbverlauf (Vertikal)"
Speicherarten$(4) = "Bild (Unkomprimiert)"
Speicherarten$(5) = "Bild (RLE-Kompression)"
Speicherarten$(6) = "Bild (YUV-Kompression)"
Speicherarten$(7) = "Bild (256 Farben)"
For I% = 1 To 7
	If StringWidth(Speicherarten$(I%)) > MenuWidth% Then MenuWidth% = StringWidth(Speicherarten$(I%))
	Next
MenuWidth% = MenuWidth% + 4
Draw

While Not KeyDown(1)
	If MouseDown(1) = 1 Then Mouse% = 1 ElseIf MouseDown(2) Then Mouse% = 2 Else Mouse% = 0
	Key% = GetKey()
	If Key% Then
		If Key% = 83 Or Key% = 115 Then Speichern
		If SelectionO% = 3 And Key% > 47 And Key% < 59 Then Bildbreite%(Selection%) = Bildbreite%(Selection%) * 10 + Key% - 48
		If SelectionO% = 3 And Key% = 8 Then Bildbreite%(Selection%) = Int(Bildbreite%(Selection%) / 10)
		If SelectionO% = 4 And Key% > 47 And Key% < 59 Then Bildhoehe%(Selection%) = Bildhoehe%(Selection%) * 10 + Key% - 48
		If SelectionO% = 4 And Key% = 8 Then Bildhoehe%(Selection%) = Int(Bildhoehe%(Selection%) / 10)
		If SelectionO% = 5 And Key% > 47 And Key% < 59 Then Frames%(Selection%) = Frames%(Selection%) * 10 + Key% - 48
		If SelectionO% = 5 And Key% = 8 Then Frames%(Selection%) = Int(Frames%(Selection%) / 10)
		Draw
		EndIf
	If Mouse% Then
		Sel% = MouseY() / 20 + 1
		If MouseX() < 200 Then Selection% = Sel%
		If MouseX() > 200 And Sel% < 12 Then If Disable%(Sel%) Then
			If SelectionO% = Sel% Then DoubleClick% = 1 Else DoubleClick% = 0
			SelectionO% = Sel%
			If DoubleClick% Then
				If SelectionO% = 1 Then
					If ImageHandle%(Selection%) Then FreeImage ImageHandle%(Selection%)
					Dateiname$(Selection%) = DLLGetOpenFileName$(Title$ + " - Bild Laden", CurrentDir(), FileFilter$, OFN_FILEMUSTEXIST Or OFN_HIDEREADONLY)
					ImageHandle%(Selection%) = LoadImage(Dateiname$(Selection%))
					EndIf
				If SelectionO% = 8 Then DrawMenu()
				If SelectionO% = 9 Then
					Farben%(Selection%, 1) = SwapRGB(DLLChooseColor(ColorBank%, CC_FULLOPEN))
					If ImageHandle%(Selection%) Then MaskImage ImageHandle%(Selection%), 0, 0, Farben%(Selection%, 1)
					EndIf
				If SelectionO% = 10 Then Farben%(Selection%, 2) = SwapRGB(DLLChooseColor(ColorBank%, CC_FULLOPEN))
				If SelectionO% = 11 Then Farben%(Selection%, 3) = SwapRGB(DLLChooseColor(ColorBank%, CC_FULLOPEN))
				EndIf
			EndIf
		Draw
		While MouseDown(1) Or MouseDown(2)
			Wend
		EndIf
	Delay 20
	Wend
End

Function Draw()
If Speicherart%(Selection%) < 4 Then
	Disable%(1) = 0
	Disable%(3) = 1
	Disable%(4) = 1
	Disable%(10) = 1
	If Speicherart%(Selection%) = 1 Then Disable%(11) = 0 Else Disable%(11) = 1
	Else
	Disable%(1) = 1
	Disable%(3) = 0
	Disable%(4) = 0
	Disable%(10) = 0
	Disable%(11) = 0
	If ImageHandle%(Selection%) Then
		Bildbreite%(Selection%) = ImageWidth(ImageHandle%(Selection%))
		Bildhoehe%(Selection%) = ImageHeight(ImageHandle%(Selection%))
		EndIf
	EndIf
Cls
Color 0, 0, SwapRGB(GetSysColor(COLOR_HIGHLIGHT)) ; Linker Frame
Rect 2, Selection% * 20 - 19, 196, 18
If SelectionO% = 1 Then Rect 353, SelectionO% * 20 - 19, 285, 18 Else Rect 353, SelectionO% * 20 - 19, MenuWidth% - 4, 18
Viewport 2, 2, 196, 476
Color 0, 0, SwapRGB(GetSysColor(COLOR_MENUTEXT))
For I% = 1 To 24
	Text 2, I% * 20 - 20, "Bild " + Str(I%)
	If Bildbreite%(I%) <> 0 And Bildhoehe%(I%) <> 0 And Frames%(I%) <> 0 Then
		Text 60, I% * 20 - 20, Speicherarten$(Speicherart%(I%))
		EndIf
	Next
Viewport 202, 2, 436, 222 ; Oberer Frame
If Not Disable%(1) Then Color 0, 0, SwapRGB(GetSysColor(COLOR_GRAYTEXT)) Else Color 0, 0, SwapRGB(GetSysColor(COLOR_MENUTEXT))
Text 203, 2, "Dateiname: "
Text 353, 2, Dateiname$(Selection%)
If Not Disable%(3) Then Color 0, 0, SwapRGB(GetSysColor(COLOR_GRAYTEXT)) Else Color 0, 0, SwapRGB(GetSysColor(COLOR_MENUTEXT))
Text 203, 42, "Bildbreite: "
Text 353, 42, Str(Bildbreite%(Selection%)) + " px"
Text 203, 62, "Bildhöhe: "
Text 353, 62, Str(Bildhoehe%(Selection%)) + " px"
Text 203, 182, "Farbe 1: "
Text 353, 182, "#" + Mid(Hex(Farben%(Selection%, 2)), 3)
Color 0, 0, Farben%(Selection%, 2)
Rect 423, 181, 18, 18
If Not Disable%(11) Then Color 0, 0, SwapRGB(GetSysColor(COLOR_GRAYTEXT)) Else Color 0, 0, SwapRGB(GetSysColor(COLOR_MENUTEXT))
Text 203, 202, "Farbe 2: "
Text 353, 202, "#" + Mid(Hex(Farben%(Selection%, 3)), 3)
Color 0, 0, Farben%(Selection%, 3)
Rect 423, 201, 18, 18

Color 0, 0, SwapRGB(GetSysColor(COLOR_GRAYTEXT))
Text 203, 22, "Dateigröße: "
Text 353, 22, Str(FileSize(Dateiname$(Selection%)) / 1024) + " KB"
Text 203, 102, "Framebreite: "
If Frames%(Selection%) Then Text 353, 102, Str(Bildbreite%(Selection%) / Frames%(Selection%)) + " px" Else Text 353, 102, "#inf px"
Text 203, 122, "Framehöhe: "
Text 353, 122, Str(Bildhoehe%(Selection%)) + " px"

Color 0, 0, SwapRGB(GetSysColor(COLOR_MENUTEXT))
Text 203, 82, "Anzahl an Frames: "
Text 353, 82, Frames%(Selection%)
Text 203, 142, "Speicherart: "
Text 353, 142, Speicherarten$(Speicherart%(Selection%))
Text 203, 162, "Transparente Farbe: "
Text 353, 162, "#" + Mid(Hex(Farben%(Selection%, 1)), 3)
Color 0, 0, Farben%(Selection%, 1)
Rect 423, 161, 18, 18

Viewport 202, 226, 436, 252 ; Unterer Frame
Select Speicherart%(Selection%)
	Case 1
		Color 0, 0, Farben%(Selection%, 2)
		Rect 202, 226, Bildbreite%(Selection%), Bildhoehe%(Selection%)
	Case 2
		Width% = Bildbreite%(Selection%)
		Height% = Bildhoehe%(Selection%)
		If Width% > 436 Then Width% = 436
		If Height% > 252 Then Height2% = 252 Else Height2% = Height%
		If Height% = 0 Then Height% = 1
		Rot1% = (Farben%(Selection%, 2) And $FF0000) Shr 16
		Gruen1% = (Farben%(Selection%, 2) And $00FF00) Shr 8
		Blau1% = (Farben%(Selection%, 2) And $0000FF)
		Rot2% = (Farben%(Selection%, 3) And $FF0000) Shr 16
		Gruen2% = (Farben%(Selection%, 3) And $00FF00) Shr 8
		Blau2% = (Farben%(Selection%, 3) And $0000FF)
		For U% = 0 To Height2%
			Color Rot1% * (Height% - U%) / Height% + Rot2% * U% / Height%, Gruen1% * (Height% - U%) / Height% + Gruen2% * U% / Height%, Blau1% * (Height% - U%) / Height% + Blau2% * U% / Height%
			Line 202, 226 + U%, 202 + Width%, 226 + U%
			Next
	Case 3
		Width% = Bildbreite%(Selection%)
		Height% = Bildhoehe%(Selection%)
		If Height% > 252 Then Height% = 252
		If Width% > 436 Then Width2% = 436 Else Width2% = Width%
		If Width% = 0 Then Width% = 1
		Rot1% = (Farben%(Selection%, 2) And $FF0000) Shr 16
		Gruen1% = (Farben%(Selection%, 2) And $00FF00) Shr 8
		Blau1% = (Farben%(Selection%, 2) And $0000FF)
		Rot2% = (Farben%(Selection%, 3) And $FF0000) Shr 16
		Gruen2% = (Farben%(Selection%, 3) And $00FF00) Shr 8
		Blau2% = (Farben%(Selection%, 3) And $0000FF)
		For U% = 0 To Width2%
			Color Rot1% * (Width% - U%) / Width% + Rot2% * U% / Width%, Gruen1% * (Width% - U%) / Width% + Gruen2% * U% / Width%, Blau1% * (Width% - U%) / Width% + Blau2% * U% / Width%
			Line 202 + U%, 226, 202 + U%, 226 + Height%
			Next
	Default
		If ImageHandle%(Selection%) Then DrawImage ImageHandle%(Selection%), 202, 226
	End Select
Viewport 0, 0, 640, 480
Color 0, 0, SwapRGB(GetSysColor(COLOR_GRAYTEXT))
Line 200, 0, 200, 480
Line 200, 224, 640, 224
Flip
End Function

Function DrawMenu()
Reset% = Speicherart%(Selection%)
While MouseDown(1)
	Wend
While Not MouseDown(1)
	Color 0, 0, SwapRGB(GetSysColor(COLOR_MENU))
	Rect 351, 162, MenuWidth%, 140
	Sel% = MouseY() / 20 + 1
	If Sel% > 8 And Sel% < 16 And MouseX() > 352 And MouseX() < 350 + MenuWidth% Then
		Color 0, 0, SwapRGB(GetSysColor(COLOR_HIGHLIGHT))
		Speicherart%(Selection%) = Sel% - 8
		Rect 353, Sel% * 20 - 18, MenuWidth% - 4, 18
		Else Speicherart%(Selection%) = Reset%
		EndIf
	Color 255, 255, 255
	Rect 351, 162, MenuWidth%, 140, 0
	Color 0, 0, 0
	Line 351, 302, 351 + MenuWidth%, 302
	Line 351 + MenuWidth%, 162, 351 + MenuWidth%, 302
	For I% = 1 To 7
		Text 353, 142 + I% * 20, Speicherarten$(I%)
		Next
	Flip
	Wend
End Function

Function Speichern()
For I% = 1 To 24
	If Bildbreite%(I%) <> 0 And Bildhoehe%(I%) <> 0 And Frames%(I%) <> 0 Then Pics% = Pics% + 1
	Next
MyFile$ = DLLGetSaveFileName$("MIF-Writer", Path$, "Multiple Image Files (*.mif)" + Chr$(0) + "*.mif" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*", $8806)
If MyFile$ = "" Then Return
Comment$ = "Written with " + Title$ + " *** MIF-Fileformat (C) Diego Semmler www.dsemmler.de"
File% = WriteFile(MyFile$)
If File% = 0 Then
	MessageBox 0, "Die Datei konnte nicht geöffnet werden.", Title$, 48
	Return
	EndIf
WriteInt File%, 1481000739
WriteShort File%, Len(Comment$)
WriteLine File%, Comment$
SeekFile File%, 6 + Len(Comment$)
WriteShort File%, Pics%

For I% = 1 To 24
	While Not Bildbreite%(I%) <> 0 And Bildhoehe%(I%) <> 0 And Frames%(I%) <> 0
		I% = I% + 1
		If I% = 24 Then
			CloseFile File%
			MessageBox 0, "Datei wurde erfolgreich gespeichert.", Title$, 64
			Return
			EndIf
		Wend
	Select Speicherart%(I%)
		Case 1
			WriteInt File%, 13
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			WriteColor File%, Farben%(I%, 2)
		Case 2
			WriteInt File%, 16
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			WriteColor File%, Farben%(I%, 2)
			WriteColor File%, Farben%(I%, 3)
		Case 3
			WriteInt File%, 16
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			WriteColor File%, Farben%(I%, 2)
			WriteColor File%, Farben%(I%, 3)
		Case 4
			WriteInt File%, 10 + Bildbreite%(I%) * Bildhoehe%(I%) * 3
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			SetBuffer ImageBuffer(ImageHandle%(I%))
			For F% = 0 To Frames%(I%) - 1
				FrameOffset% = F% * Bildbreite%(I%) / Frames%(I%)
				For Y% = 0 To Bildhoehe%(I%) - 1
					For X% = 0 To Bildbreite%(I%) / Frames%(I%) - 1
						Farbe% = ReadPixel(X% + FrameOffset%, Y%)
						WriteByte File%, Farbe% Shr 16
						WriteByte File%, Farbe% Shl 16 Shr 24
						WriteByte File%, Farbe% Shl 24 Shr 24
						Next
					Next
				Next
		Case 5
			NumBytes% = 0
			FPos% = FilePos(File%)
			WriteInt File%, 0
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			.RLEBild
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			SetBuffer ImageBuffer(ImageHandle%(I%))
			For F% = 0 To Frames%(I%) - 1
				FrameOffset% = F% * Bildbreite%(I%) / Frames%(I%)
				AltFarbe% = ReadPixel(0, 0)
				Counter% = -1
				For Y% = 0 To Bildhoehe%(I%) - 1
					For X% = 0 To Bildbreite%(I%) / Frames%(I%) - 1
						Farbe% = ReadPixel(X% + FrameOffset%, Y%)
						If Farbe% = AltFarbe% And Counter% < 255 Then
							Counter% = Counter% + 1
							Else
							WriteByte File%, Counter%
							WriteByte File%, AltFarbe% Shr 16
							WriteByte File%, AltFarbe% Shl 16 Shr 24
							WriteByte File%, AltFarbe% Shl 24 Shr 24
							NumBytes% = NumBytes% + 4
							AltFarbe% = Farbe%
							Counter% = 0
							EndIf
						Next
					Next
				WriteByte File%, Counter%
				WriteByte File%, AltFarbe% Shr 16
				WriteByte File%, AltFarbe% Shl 16 Shr 24
				WriteByte File%, AltFarbe% Shl 24 Shr 24
				Next
			EndPos% = FilePos(File%)
			SeekFile File%, FPos%
			WriteInt File%, NumBytes%
			SeekFile File%, EndPos%
		Case 6
			WriteInt File%, 10 + Ceil(Float ImageHeight(ImageHandle%(I%)) / 2) * Ceil(Float ImageWidth(ImageHandle%(I%)) / 2) * 6
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, 1 ; Animation (Wird nicht verwendet) Frames%(I%)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Speicherart%(I%)
			WriteColor File%, Farben%(I%, 1)
			SetBuffer ImageBuffer(ImageHandle%(I%))
			For Y% = 0 To Ceil(Float ImageHeight(ImageHandle%(I%)) / 2) - 1
				For X% = 0 To Ceil(Float ImageWidth(ImageHandle%(I%)) / 2) - 1
					Farbe% = ReadPixel(X% * 2, Y% * 2)
					RColor00% = Farbe% Shl 08 Shr 24
					GColor00% = Farbe% Shl 16 Shr 24
					BColor00% = Farbe% Shl 24 Shr 24
					Farbe% = ReadPixel(X% * 2 + 1, Y% * 2)
					RColor10% = Farbe% Shl 08 Shr 24
					GColor10% = Farbe% Shl 16 Shr 24
					BColor10% = Farbe% Shl 24 Shr 24
					Farbe% = ReadPixel(X% * 2, Y% * 2 + 1)
					RColor01% = Farbe% Shl 08 Shr 24
					GColor01% = Farbe% Shl 16 Shr 24
					BColor01% = Farbe% Shl 24 Shr 24
					Farbe% = ReadPixel(X% * 2 + 1, Y% * 2 + 1)
					RColor11% = Farbe% Shl 08 Shr 24
					GColor11% = Farbe% Shl 16 Shr 24
					BColor11% = Farbe% Shl 24 Shr 24
					Bright00% = RColor00% + GColor00% + BColor00%
					Bright10% = RColor10% + GColor10% + BColor10%
					Bright01% = RColor01% + GColor01% + BColor01%
					Bright11% = RColor11% + GColor11% + BColor11%
					RColor% = RColor00% + RColor10% + RColor01% + RColor11%
					GColor% = GColor00% + GColor10% + GColor01% + GColor11%
					BColor% = BColor00% + BColor10% + BColor01% + BColor11%
					Bright% = RColor% + GColor% + BColor%
					If Bright% <> 0 Then
						RColor% = 255 * RColor% / Bright%
						GColor% = 255 * GColor% / Bright%
						EndIf
					WriteByte File%, RColor%
					WriteByte File%, GColor%
					WriteByte File%, Bright00% / 3
					WriteByte File%, Bright10% / 3
					WriteByte File%, Bright01% / 3
					WriteByte File%, Bright11% / 3
					Next
				Next
		Case 7
			NumBytes% = 0
			FPos% = FilePos(File%)
			WriteInt File%, 0
			WriteShort File%, Bildbreite%(I%)
			WriteShort File%, Bildhoehe%(I%)
			WriteByte File%, Frames%(I%)
			WriteByte File%, 1
			Cls
			SetBuffer ImageBuffer(ImageHandle%(I%))
			NumFarben% = 1
			Farbtabelle%(1) = ReadPixel(0, 0)
			For Y% = 0 To Bildhoehe%(I%) - 1 ; Farbtabelle erstellen
				For X% = 0 To Bildbreite%(I%) - 1
					CurPixel% = ReadPixel(X%, Y%)
					For Farbe% = 1 To NumFarben%
						If Farbtabelle%(Farbe%) = CurPixel% Then Exit
						Next
					If Farbe% > NumFarben% Then
						NumFarben% = NumFarben% + 1
						If NumFarben% = 257 Then
							MessageBox 0, "Das Bild " + I% + " hat mehr als 256 Farben." + Chr(13) + "Es wird RLE-Komprimiert gespeichert.", "Warnung", 48
							Speicherart%(I%) = 5
							Goto RLEBild
							EndIf
						Farbtabelle%(NumFarben%) = CurPixel%
						EndIf
					Next
				Next
			; Hier können optimierungen der Farbtabelle vorgenommen werden
			WriteByte File%, Art%
			WriteColor File%, Farben%(I%, 1)
			WriteByte File%, NumFarben%
			For Farbe% = 1 To NumFarben%
				WriteByte File%, Farbtabelle%(Farbe%) Shr 16
				WriteByte File%, Farbtabelle%(Farbe%) Shr 8
				WriteByte File%, Farbtabelle%(Farbe%)
				Next
			NumBytes% = NumBytes% + NumFarben% * 3
			AltFarbe% = ReadPixel(0, 0)
			Counter% = 0
			Farbe% = 1
			For Y% = 0 To Bildhoehe%(I%) - 1
				For X% = 0 To Bildbreite%(I%) - 1
					CurPixel% = ReadPixel(X%, Y%)
					If CurPixel% = AltFarbe% And Farbe% + (Counter% + 1) * NumFarben% <= 256 Then
						Counter% = Counter% + 1
						Else
						WriteByte File%, Farbe% - 1 + Counter% * NumFarben%
						NumBytes% = NumBytes% + 1
						AltFarbe% = CurPixel%
						Counter% = 0
						For Farbe% = 1 To NumFarben%
							If Farbtabelle%(Farbe%) = AltFarbe% Then Exit
							Next
						EndIf
					Next
				Next
			WriteByte File%, Farbe% - 1 + Counter% * NumFarben%
			NumBytes% = NumBytes% + 1
			EndPos% = FilePos(File%)
			SeekFile File%, FPos%
			WriteInt File%, NumBytes%
			SeekFile File%, EndPos%
		End Select
	Next
CloseFile File%
MessageBox 0, "Datei wurde erfolgreich gespeichert.", Title$, 64
End Function

Function WriteColor(File%, Value%)
WriteByte File%, (Value% And $FF0000) Shr 16
WriteByte File%, (Value% And $FF00) Shr 8
WriteByte File%, Value% And $FF
End Function

Function LoadImages(File$, MaxImages% = 256)
Local Buffer%, InFile%, NumImages%, Size%, Anim%, ImageType%, Bank%
Local I%, U%, Rot1%, Gruen1%, Blau1%, Rot2%, Gruen2%, Blau2%, Counter%, Farbe%, Offset%

Buffer% = GraphicsBuffer()
InFile% = ReadFile(File$)
If Not InFile% Then Return -1 ; Die Datei konnte nicht geöffnet werden.
If ReadInt(InFile%) <> 1481000739 Then Return -2 ; Die Datei ist Keine MIF-Datei.
SeekFile InFile%, 6 + ReadShort(InFile%)
NumImages% = ReadShort(InFile%)
If NumImages% > MaxImages% Then
	MessageBox 0, "WARNUNG!!!" + Chr(13) + "Der MIF-Editor kann z.Z. nicht mehr als 24 Bilder bearbeiten." + Chr(13) + "Die restlichen Bilder gehen beim Speichern verloren.", "Wichtiger Hindwis !", 48
	NumImages% = Maximages%
	EndIf
Bank% = CreateBank(14)
For I% = 1 To NumImages%
	ReadBytes Bank%, InFile%, 0, 14
	Size% = PeekInt(Bank%, 0)
	Bildbreite%(I%) = PeekShort(Bank%, 4)
	Bildhoehe%(I%) = PeekShort(Bank%, 6)
	Anim% = PeekByte(Bank%, 8)
	FrameDelay%(I%) = PeekByte(Bank%, 9)
	ImageType% = PeekByte(Bank%, 10)
	Speicherart%(I%) = ImageType%
	If ImageType% > 3 Then ImageHandle%(I%) = CreateImage(Bildbreite%(I%), Bildhoehe%(I%), Anim%)
	Farben%(I%, 1) = PeekByte(Bank%, 11) Shl 16 Or PeekByte(Bank%, 12) Shl 8 Or PeekByte(Bank%, 13)
	For Frame% = 0 To Anim% - 1
		If ImageHandle%(I%) Then SetBuffer ImageBuffer(ImageHandle%(I%), Frame%)
		Select ImageType%
			Case 1 ; Farbe
				Farben%(I%, 2) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
			Case 2 ; Horizontaler Farbverlauf
				Farben%(I%, 2) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
				Farben%(I%, 3) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
			Case 3 ; Vertikaler Farbverlauf
				Farben%(I%, 2) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
				Farben%(I%, 3) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
			Case 4 ; Bild (unkomprimiert)
				LockBuffer
				For YV% = 0 To Bildhoehe%(I%) - 1
					For XV% = 0 To Bildbreite%(I%) - 1
						ReadBytes Bank%, InFile%, 0, 3
						WritePixelFast XV%, YV%, PeekByte(Bank%, 0) Shl 16 Or PeekByte(Bank%, 1) Shl 8 Or PeekByte(Bank%, 2)
						Next
					Next
				UnlockBuffer
			Case 5 ; Bild (RLE-Komprimiert)
				Counter% = -1
				LockBuffer
				For YV% = 0 To Bildhoehe%(I%) - 1
					For XV% = 0 To Bildbreite%(I%) - 1
						If Counter% = -1
							ReadBytes Bank%, InFile%, 0, 4
							Counter% = PeekByte(Bank%, 0)
							Farbe% = PeekByte(Bank%, 1) Shl 16 Or PeekByte(Bank%, 2) Shl 8 Or PeekByte(Bank%, 3)
							EndIf
						WritePixelFast XV%, YV%, Farbe%
						Counter% = Counter% - 1
						Next
					Next
				UnlockBuffer
			Case 6 ; Bild (YUV-Format)
				LockBuffer
				For YV% = 0 To Ceil(Float Bildhoehe%(I%) / 2) - 1
					For XV% = 0 To Ceil(Float Bildbreite%(I%) / 2) -1
						ReadBytes Bank%, InFile%, 0, 6
						RColor% = PeekByte(Bank%, 0)
						GColor% = PeekByte(Bank%, 1)
						BColor% = 255 - RColor% - GColor%
						SetPixel XV% * 2	, YV% * 2,	   RColor% * PeekByte(Bank%, 2) / 85, GColor% * PeekByte(Bank%, 2) / 85, BColor% * PeekByte(Bank%, 2) / 85
						SetPixel XV% * 2 + 1, YV% * 2,	   RColor% * PeekByte(Bank%, 3) / 85, GColor% * PeekByte(Bank%, 3) / 85, BColor% * PeekByte(Bank%, 3) / 85
						SetPixel XV% * 2	, YV% * 2 + 1, RColor% * PeekByte(Bank%, 4) / 85, GColor% * PeekByte(Bank%, 4) / 85, BColor% * PeekByte(Bank%, 4) / 85
						SetPixel XV% * 2 + 1, YV% * 2 + 1, RColor% * PeekByte(Bank%, 5) / 85, GColor% * PeekByte(Bank%, 5) / 85, BColor% * PeekByte(Bank%, 5) / 85
						Next
					Next
				UnlockBuffer
			End Select
		Next
	Next
FreeBank Bank%
SetBuffer Buffer%
Return NumImages%
End Function

Function SetPixel(X%, Y%, Red%, Green%, Blue%)
If Red% > 255	Then Red% = 255
If Red% < 0		Then Red% = 0
If Green% > 255	Then Green% = 255
If Green% < 0	Then Green% = 0
If Blue% > 255	Then Blue% = 255
If Blue% < 0	Then Blue% = 0
WritePixel X%, Y%, Red% Shl 16 Or Green% Shl 8 Or Blue%
End Function

Function DialogSub(Dialog%, ObjectID%, Event%)
End Function