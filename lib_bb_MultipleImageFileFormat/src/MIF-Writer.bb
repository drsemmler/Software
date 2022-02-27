Dim Farben%(257)
Global SupportedFiles$ = "Bitmaps (*.bmp)" + Chr$(0) + "*.bmp" + Chr$(0) + "JPEG-Dateien (*.jpg)" + Chr$(0) + "*.jpg" + Chr$(0) + "PNG-Dateien (*.png)" + Chr$(0) + "*.png" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*"
;Global Path$ = SystemProperty("appdir")
Global InFile%, OutFile%, Command$ = CommandLine()

If Command$ = "" Then If Lower(Input("MIF-Writer-Script öffnen? (y/n) ")) = "y" Then Command$ = DLLGetOpenFileName$("MIF-Writer", Path$, "MIF-Writer-Scripts (*.mws)" + Chr$(0) + "*.mws" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*", $1804)
If Command$ <> "" Then
	InFile% = ReadFile(Command$)
	If InFile% <> 0 Then If Lower(ReadLine(InFile%)) <> "mif-writer-script" Then
		CloseFile InFile%
		InFile% = 0
		EndIf
	If InFile% = 0 Then Print "MIF-Writer-Script konnte nicht geöffnet werden." Else Print "MIF-Writer-Script geöffnet."
	Else
	If Lower(Input("MIF-Writer-Script erstellen? (y/n) ")) = "y" Then OutFile% = WriteFile(DLLGetSaveFileName$("MIF-Writer", Path$, "MIF-Writer-Scripts (*.mws)" + Chr$(0) + "*.mws" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*", $8806))
	If OutFile% = 0 Then
		Print "Die Datei konnte nicht geöffnet werden."
		Else
		Print "Es wird ein MIF-Writer-Script erstellt."
		WriteLine OutFile%, "MIF-Writer-Script"
		EndIf
	EndIf
MyFile$ = DLLGetSaveFileName$("MIF-Writer", Path$, "MIF-Dateien (*.mif)" + Chr$(0) + "*.mif" + Chr$(0) + "Alle Dateien (*.*)" + Chr$(0) + "*.*", $8806)
If Lower(Right(MyFile$, 4)) <> ".mif" Then MyFile$ = MyFile$ + ".mif"
Print "Kommentar (Kann weggelassen werden)"
Comment$ = GetLine(" ")
Pics% = GetLine("Anzahl der Bilder: ")

Global File% = WriteFile(MyFile$)
WriteInt File%, 1481000739
WriteShort File%, Len(Comment$)
WriteLine File%, Comment$
SeekFile File%, 6 + Len(Comment$)
WriteShort File%, Pics%

For I% = 1 To Pics%
	Print ""
	Print "1 = Farbe"
	Print "2 = Horizontaler Farbverlauf"
	Print "3 = Vertikaler Farbverlauf"
	Print "4 = Bild (unkomprimiert)"
	Print "5 = Bild (LZW komprimiert)"
	Print "6 = Bild (YUV formatiert)"
	Print "7 = Bild (256 Farben)"
	Art% = GetLine("Art des Bildes: ")
	Select Art%
		Case 1
			WriteInt File%, 13
			Width% = GetLine("Breite in Pixeln: ")
			Height% = GetLine("Höhe in Pixeln: ")
			WriteShort File%, Width%
			WriteShort File%, Height%
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			Print "Bildfarbe"
			SchreibeFarbe
		Case 2
			WriteInt File%, 16
			Width% = GetLine("Breite in Pixeln: ")
			Height% = GetLine("Höhe in Pixeln: ")
			WriteShort File%, Width%
			WriteShort File%, Height%
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			Print "Bildfarbe oben:"
			SchreibeFarbe
			Print "Bildfarbe unten:"
			SchreibeFarbe
		Case 3
			WriteInt File%, 16
			Width% = GetLine("Breite in Pixeln: ")
			Height% = GetLine("Höhe in Pixeln: ")
			WriteShort File%, Width%
			WriteShort File%, Height%
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			Print "Bildfarbe links:"
			SchreibeFarbe
			Print "Bildfarbe rechts:"
			SchreibeFarbe
		Case 4
			Bild% = LoadImage(GetLine())
			Print "Obiekt -> Bild (unkomprimiert)"
			WriteInt File%, 10 + ImageHeight(Bild%) * ImageWidth(Bild%) * 3
			WriteShort File%, ImageWidth(Bild%)
			WriteShort File%, ImageHeight(Bild%)
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			SetBuffer ImageBuffer(Bild%)
			For Y% = 0 To ImageHeight(Bild%) - 1
				For X% = 0 To ImageWidth(Bild%) - 1
					Farbe% = ReadPixel(X%, Y%)
					WritePixel X%, Y%, Farbe%, FrontBuffer()
					WriteByte File%, Farbe% Shr 16
					WriteByte File%, Farbe% Shl 16 Shr 24
					WriteByte File%, Farbe% Shl 24 Shr 24
					Next
				Next
			FreeImage Bild%
			Cls
		Case 5
			NumBytes% = 0
			Bild% = LoadImage(GetLine())
			Print "Obiekt -> Bild (RLE komprimiert)"
			FPos% = FilePos(File%)
			WriteInt File%, 0
			WriteShort File%, ImageWidth(Bild%)
			WriteShort File%, ImageHeight(Bild%)
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			.RLEBild
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			SetBuffer ImageBuffer(Bild%)
			AltFarbe% = ReadPixel(0, 0)
			Counter% = -1
			For Y% = 0 To ImageHeight(Bild%) - 1
				For X% = 0 To ImageWidth(Bild%) - 1
					Farbe% = ReadPixel(X%, Y%)
					If Farbe% = AltFarbe% And Counter% < 255 Then
						Counter% = Counter% + 1
						Else
						WritePixel X%, Y%, Farbe%, FrontBuffer()
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
			FreeImage Bild%
			EndPos% = FilePos(File%)
			SeekFile File%, FPos%
			WriteInt File%, NumBytes%
			SeekFile File%, EndPos%
			Cls
		Case 6
			Bild% = LoadImage(GetLine())
			Print "Obiekt -> Bild (YUV formatiert)"
			WriteInt File%, 10 + Ceil(Float ImageHeight(Bild%) / 2) * Ceil(Float ImageWidth(Bild%) / 2) * 6
			WriteShort File%, ImageWidth(Bild%)
			WriteShort File%, ImageHeight(Bild%)
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			SetBuffer ImageBuffer(Bild%)
			For Y% = 0 To Ceil(Float ImageHeight(Bild%) / 2) - 1
				For X% = 0 To Ceil(Float ImageWidth(Bild%) / 2) - 1
					Farbe% = ReadPixel(X% * 2, Y% * 2)
					WritePixel X%, Y%, Farbe%, FrontBuffer()
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
			FreeImage Bild%
			Cls
		Case 7
			NumBytes% = 0
			Bild% = LoadImage(GetLine())
			Print "Obiekt -> Bild (256 Farben)"
			FPos% = FilePos(File%)
			WriteInt File%, 0
			WriteShort File%, ImageWidth(Bild%)
			WriteShort File%, ImageHeight(Bild%)
			WriteByte File%, 1 ; Animation (Wird nicht verwendet)
			WriteByte File%, 1 ; Framerate (Wird nicht verwendet)
			Cls
			SetBuffer ImageBuffer(Bild%)
			NumFarben% = 1
			Farben%(1) = ReadPixel(0, 0)
			For Y% = 0 To ImageHeight(Bild%) - 1 ; Farbtabelle erstellen
				For X% = 0 To ImageWidth(Bild%) - 1
					CurPixel% = ReadPixel(X%, Y%)
					For Farbe% = 1 To NumFarben%
						If Farben%(Farbe%) = CurPixel% Then Exit
						Next
					If Farbe% > NumFarben% Then
						NumFarben% = NumFarben% + 1
						If NumFarben% = 257 Then
							Print "Das Bild hat mehr als 256 Farben."
							Print "Es wird RLE-Komprimiert gespeichert."
							Art% = 5
							Goto RLEBild
							EndIf
						Farben%(NumFarben%) = CurPixel%
						EndIf
					Next
				Next
			Print NumFarben% + " Farben detektiert"
			Print "Es wird keine optimierung der Farbtabelle"
			Print "vorgenommen."
			; Hier können optimierungen der Farbtabelle vorgenommen werden
			WriteByte File%, Art%
			Print "Transparente Farbe: "
			SchreibeFarbe
			WriteByte File%, NumFarben%
			For Farbe% = 1 To NumFarben%
				WriteByte File%, Farben%(Farbe%) Shr 16
				WriteByte File%, Farben%(Farbe%) Shr 8
				WriteByte File%, Farben%(Farbe%)
				Next
			NumBytes% = NumBytes% + NumFarben% * 3
			AltFarbe% = ReadPixel(0, 0)
			Counter% = -1
			Farbe% = 1
			For Y% = 0 To ImageHeight(Bild%) - 1
				For X% = 0 To ImageWidth(Bild%) - 1
					CurPixel% = ReadPixel(X%, Y%)
					If CurPixel% = AltFarbe% And Farbe% + (Counter% + 1) * NumFarben% <= 256 Then
						Counter% = Counter% + 1
						Else
						WriteByte File%, Farbe% - 1 + Counter% * NumFarben%
						NumBytes% = NumBytes% + 1
						AltFarbe% = CurPixel%
						Counter% = 0
						For Farbe% = 1 To NumFarben%
							If Farben%(Farbe%) = AltFarbe% Then Exit
							Next
						EndIf
					Next
				Next
			WriteByte File%, Farbe% - 1 + Counter% * NumFarben%
			NumBytes% = NumBytes% + 1
			FreeImage Bild%
			EndPos% = FilePos(File%)
			SeekFile File%, FPos%
			WriteInt File%, NumBytes%
			SeekFile File%, EndPos%
		Default
			Print "Bildtyp wird nicht unterstützt."
			I% = I% - 1
		End Select
	Next
CloseFile File%
End

Function SchreibeFarbe()
WriteByte File%, GetLine("Farbe Rot:  ")
WriteByte File%, GetLine("Farbe Grün: ")
WriteByte File%, GetLine("Farbe Blau: ")
End Function

Function GetLine$(OutText$ = "")
If InFile% Then
	T$ = ReadLine(InFile%)
	Print OutText$ + T$
	If Eof(InFile%) Then
		CloseFile InFile%
		InFile% = 0
		EndIf
	Return T$
	EndIf
If OutText$ <> "" Then OutText$ = Input(OutText$) Else OutText$ = DLLGetOpenFileName$("MIF-Writer", Path$, SupportedFiles$, $1804)
If OutFile% Then WriteLine OutFile%, OutText$
Return OutText$
End Function

Function DLLGetSaveFileName$(sTitle$,sInitialDir$,sFilter$,iFlags = 0,iOutBufferSize = 512)
	Local iBankSize,mBankIn,mBankOut,iResult,sResult$
	
	iBankSize = Len(sTitle$) + Len(sInitialDir$) + Len(sFilter$) + 4 + 4
	
	mBankIn  = CreateBank(iBankSize)
	mBankOut = CreateBank(iOutBufferSize)

	PokeInt(mBankIn,0,iFlags)
	PokeString(mBankIn,sInitialDir$,4)
	iBankOffset = Len(sInitialDir$) + 1 + 4
	PokeString(mBankIn,sTitle$,iBankOffset)
	iBankOffset = iBankOffset + Len(sTitle$) + 1
	PokeString(mBankIn,sFilter$,iBankOffset)

	iResult = CallDLL("blitzsys","GetSaveFileNameWrapper",mBankIn,mBankOut)
	
	If iResult = True
		sResult$ = PeekString$(mBankOut,0)
	EndIf
	
	FreeBank mBankIn
	FreeBank mBankOut
	
	Return sResult$
End Function

Function DLLGetOpenFileName$(sTitle$,sInitialDir$,sFilter$,iFlags = 0,iOutBufferSize = 512)
	Local iBankSize,mBankIn,mBankOut,iResult,sResult$
	
	iBankSize = Len(sTitle$) + Len(sInitialDir$) + Len(sFilter$) + 4 + 4
	
	mBankIn  = CreateBank(iBankSize)
	mBankOut = CreateBank(iOutBufferSize)

	PokeInt(mBankIn,0,iFlags)
	PokeString(mBankIn,sInitialDir$,4)
	iBankOffset = Len(sInitialDir$) + 1 + 4
	PokeString(mBankIn,sTitle$,iBankOffset)
	iBankOffset = iBankOffset + Len(sTitle$) + 1
	PokeString(mBankIn,sFilter$,iBankOffset)

	iResult = CallDLL("blitzsys","GetOpenFileNameWrapper",mBankIn,mBankOut)
	
	If iResult = True
		sResult$ = PeekString$(mBankOut,0)
	EndIf
	
	FreeBank mBankIn
	FreeBank mBankOut
	
	Return sResult$
End Function

; Null terminated string poke...
Function PokeString(mBankAddr,sStringOut$,iBufferOffset = 0)
	For n = 1 To Len(sStringOut$)
		PokeByte mBankAddr,iBufferOffset,Asc(Mid$(sStringOut$,n,1))
		iBufferOffset = iBufferOffset + 1
	Next
	PokeByte mBankAddr,iBufferOffset,0 ; Null terminate
End Function

; Null terminated string peek...
Function PeekString$(mBankAddr,iBufferOffset = 0)
	Local sOutStr$ = "",iByte
	
	For n = 0 To BankSize(mBankAddr)
		iByte = PeekByte(mBankAddr,iBufferOffset)
		If iByte <> 0 
			sOutStr$ = sOutStr$ + Chr(iByte)
		Else
			Exit
		EndIf
		iBufferOffset = iBufferOffset + 1
	Next

	Return sOutStr$
End Function