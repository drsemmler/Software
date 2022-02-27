Dim GFX%(256), Farben%(65536), MaskColor%(256)
Dim FrameDelay%(256)

Function LoadImages(File$, MaxImages% = 256)
Local Buffer%, InFile%, NumImages%, Size%, Width%, Height%, Anim%, ImageType%, XV%, YV%, I%, U%
Local H00%, H10%, H01%, H11%, Rot1%, Gruen1%, Blau1%, Rot2%, Gruen2%, Blau2%, Counter%
Local Farbe%, Offset%, NumFarben%

Buffer% = GraphicsBuffer()
InFile% = ReadFile(File$)
If Not InFile% Then Return -1 ; Die Datei konnte nicht geöffnet werden.
If ReadInt(InFile%) <> 1481000739 Then Return -2 ; Die Datei ist Keine MIF-Datei.
SeekFile InFile%, 6 + ReadShort(InFile%)
NumImages% = ReadShort(InFile%)
If NumImages% > MaxImages% Then NumImages% = Maximages%
For I% = 1 To NumImages%
	Size% = ReadInt(InFile%)
	Width% = ReadShort(InFile%)
	Height% = ReadShort(InFile%)
	Anim% = ReadByte(InFile%)
	FrameDelay%(I%) = ReadByte(InFile%)
	ImageType% = ReadByte(InFile%)
	GFX%(I%) = CreateImage(Width%, Height%, Anim%)
	MaskColor%(I%) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
	MaskImage GFX%(I%), 0, 0, MaskColor%(I%)
	For Frame% = 0 To Anim% - 1
		SetBuffer ImageBuffer(GFX%(I%), Frame%)
		Select ImageType%
			Case 1 ; Farbe
				ClsColor ReadByte(InFile%), ReadByte(InFile%), ReadByte(InFile%)
				Cls
			Case 2 ; Horizontaler Farbverlauf
				Rot1% = ReadByte(InFile%)
				Gruen1% = ReadByte(InFile%)
				Blau1% = ReadByte(InFile%)
				Rot2% = ReadByte(InFile%)
				Gruen2% = ReadByte(InFile%)
				Blau2% = ReadByte(InFile%)
				For U% = 0 To Height%
					Color Rot1% * (Height% - U%) / Height% + Rot2% * U% / Height%, Gruen1% * (Height% - U%) / Height% + Gruen2% * U% / Height%, Blau1% * (Height% - U%) / Height% + Blau2% * U% / Height%
					Line 0, U%, Width%, U%
					Next
			Case 3 ; Vertikaler Farbverlauf
				Rot1% = ReadByte(InFile%)
				Gruen1% = ReadByte(InFile%)
				Blau1% = ReadByte(InFile%)
				Rot2% = ReadByte(InFile%)
				Gruen2% = ReadByte(InFile%)
				Blau2% = ReadByte(InFile%)
				For U% = 0 To Width%
					Color Rot1% * (Width% - U%) / Width% + Rot2% * U% / Width%, Gruen1% * (Width% - U%) / Width% + Gruen2% * U% / Width%, Blau1% * (Width% - U%) / Width% + Blau2% * U% / Width%
					Line U%, 0, U%, Height%
					Next
			Case 4 ; Bild (unkomprimiert)
				LockBuffer
				For YV% = 0 To Height% - 1
					For XV% = 0 To Width% - 1
						WritePixelFast XV%, YV%, ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
						Next
					Next
				UnlockBuffer
			Case 5 ; Bild (RLE-Komprimiert)
				Counter% = -1
				LockBuffer
				For YV% = 0 To Height% - 1
					For XV% = 0 To Width% - 1
						If Counter% = -1
							Counter% = ReadByte(InFile%)
							Farbe% = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
							EndIf
						WritePixelFast XV%, YV%, Farbe%
						Counter% = Counter% - 1
						Next
					Next
				UnlockBuffer
			Case 6 ; Bild (YUV-Format)
				LockBuffer
				For YV% = 0 To Ceil(Float Height% / 2) - 1
					For XV% = 0 To Ceil(Float Width% / 2) -1
						RColor% = ReadByte(InFile%)
						GColor% = ReadByte(InFile%)
						BColor% = 255 - RColor% - GColor%
						H00% = ReadByte(InFile%)
						H10% = ReadByte(InFile%)
						H01% = ReadByte(InFile%)
						H11% = ReadByte(InFile%)
						SetPixel XV% * 2	, YV% * 2,	   RColor% * H00% / 85, GColor% * H00% / 85, BColor% * H00% / 85
						SetPixel XV% * 2 + 1, YV% * 2,	   RColor% * H10% / 85, GColor% * H10% / 85, BColor% * H10% / 85
						SetPixel XV% * 2	, YV% * 2 + 1, RColor% * H01% / 85, GColor% * H01% / 85, BColor% * H01% / 85
						SetPixel XV% * 2 + 1, YV% * 2 + 1, RColor% * H11% / 85, GColor% * H11% / 85, BColor% * H11% / 85
						Next
					Next
				UnlockBuffer
			Case 7 ; Bild (256 Farben)
				NumFarben% = ReadByte(InFile%)
				If NumFarben% = 0 Then NumFarben% = 256
				For U% = 1 To NumFarben%
					Farben%(U%) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
					Next
				Counter% = -1
				LockBuffer
				For YV% = 0 To Height% - 1
					For XV% = 0 To Width% - 1
						If Counter% = -1
							Counter% = ReadByte(InFile%)
							Farbe% = Farben%((Counter% Mod NumFarben%) + 1)
							Counter% = Counter% / NumFarben%
							EndIf
						WritePixelFast XV%, YV%, Farbe%
						Counter% = Counter% - 1
						Next
					Next
				UnlockBuffer
			Case 8 ; Bild (64 K Farben)
				NumFarben% = ReadShort(InFile%)
				If NumFarben% = 0 Then NumFarben% = 65536
				For U% = 1 To NumFarben%
					Farben%(U%) = ReadByte(InFile%) Shl 16 Or ReadByte(InFile%) Shl 8 Or ReadByte(InFile%)
					Next
				Counter% = -1
				LockBuffer
				For YV% = 0 To Height% - 1
					For XV% = 0 To Width% - 1
						If Counter% = -1
							Counter% = ReadShort(InFile%)
							Farbe% = Farben%((Counter% Mod NumFarben%) + 1)
							Counter% = Counter% / NumFarben%
							EndIf
						WritePixelFast XV%, YV%, Farbe%
						Counter% = Counter% - 1
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

Function CountImages(File$)
Local InFile%, NumImages%
InFile% = ReadFile(File$)
If Not InFile% Then Return -1 ; Die Datei konnte nicht geöffnet werden.
If ReadInt(InFile%) <> 1481000739 Then Return -2 ; Die Datei ist Keine MIF-Datei.
SeekFile InFile%, 6 + ReadShort(InFile%)
NumImages% = ReadShort(InFile%)
Return NumImages%
End Function

Function GetMifComment$(File$)
InFile% = ReadFile(File$)
If Not InFile% Then Return -1 ; Die Datei konnte nicht geöffnet werden.
If ReadInt(InFile%) <> 1481000739 Then Return -2 ; Die Datei ist Keine MIF-Datei.
Length% = ReadShort(InFile%)
For I% = 1 To Length%
	Comment$ = Comment$ + Chr$(ReadByte(InFile%))
	Next
Return Comment$
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