Include "Liste.bi"
Include "C:\Eigene Dateien\Blitz\AlphaGraphics.bi"

Function DrawListAlpha(X%, Y%, Width%, Height%, ColorBank%, List%, Offset% = 0, Deep% = 1)
If Deep% <> 0 Then Deep% = 1
Local LineHeight% = FontHeight(), I%, MouseOverElement%, BackupColor%, BackupBackground%, DrawWidth%
DrawBoxAlpha X%, Y%, Width%, Height%, ColorBank%, Deep%
If PeekInt(ColorBank%, 32) <> -1 Or PeekInt(ColorBank%, 36) <> -1 Then ; OnMouseOver-Effekt
	MouseOverElement% = GetFocus(X%, Y%, Width%, Height%, List%, Offset%)
	BackupColor% = ListColors%(MouseOverElement%, 1, List%)
	BackupBackground% = ListColors%(MouseOverElement%, 2, List%)
	ListColors%(MouseOverElement%, 1, List%) = PeekInt(ColorBank%, 32)
	ListColors%(MouseOverElement%, 2, List%) = PeekInt(ColorBank%, 36)
	EndIf
If ListLength%(List%) * LineHeight% > Height% Then ; Zeichne Scrollbalken
	HeightPerLine# = Float(Height% - LineHeight% * 2 - 6) / ListLength%(List%)
	DrawBoxAlpha Width% - LineHeight% - 2 + X%, 2 + Y%, LineHeight, LineHeight%, ColorBank%, Not Deep%
	DrawBoxAlpha Width% - LineHeight% - 2 + X%, Height% - LineHeight% - 2 + Y%, LineHeight%, LineHeight%, ColorBank%, Not Deep%
	ScrollbarY% = LineHeight% + 3 + HeightPerLine# * Offset%
	If ScrollbarY% + HeightPerLine# * Height% / LineHeight% - 1 > Height% - LineHeight% Then ScrollbarY% = Height% - LineHeight% - HeightPerLine# * Height% / LineHeight%
	DrawBoxAlpha Width% - LineHeight% - 2 + X%, ScrollbarY% + Y%, LineHeight%, HeightPerLine# * Height% / LineHeight% - 3, ColorBank%, Not Deep%
	DrawPolygonAlpha Width% - LineHeight% / 2 - 2 + X%, 2 + LineHeight% * 3 / 8 + Y%, LineHeight% / 4, PeekInt(ColorBank%, 12)
	DrawPolygonAlpha Width% - LineHeight% / 2 - 2 + X%, Height% - LineHeight% * 5 / 8 - 2 + Y%, LineHeight% / 4, PeekInt(ColorBank%, 12), 1
	DrawWidth% = Width% - 6 - LineHeight%
	Else
	DrawWidth% = Width% - 6
	EndIf
Viewport X% + 3, Y% + 3, DrawWidth%, Height% - 6
For I% = 0 To Height% / LineHeight% ; Liste Zeichnen
	If I% + 1 + Offset% > ListLength%(List%) Then Exit
	If ListColors%(I% + 1 + Offset%, 2, List%) Then RectAlpha 3 + X%, 3 + I% * LineHeight% + Y%, DrawWidth%, LineHeight%, ListColors%(I% + 1 + Offset%, 2, List%)
	If ListColors%(I% + 1 + Offset%, 1, List%) Then Color 0, 0, ListColors%(I% + 1 + Offset%, 1, List%) Else Color 0, 0, PeekInt(ColorBank%, 0)
	Text 3 + X%, 3 + I% * LineHeight% + Y%, ListContent$(I% + 1 + Offset%, List%)
	Next
If PeekInt(ColorBank%, 32) <> -1 Or PeekInt(ColorBank%, 36) <> -1 Then ; OnMouseOver-Effekt zurücksetzen
	ListColors%(MouseOverElement%, 1, List%) = BackupColor%
	ListColors%(MouseOverElement%, 2, List%) = BackupBackground%
	EndIf
Viewport 0, 0, GraphicsWidth(), GraphicsHeight()
End Function

Function DrawBoxAlpha(X%, Y%, Width%, Height%, ColorBank%, Deep% = 1)
RectAlpha X% + 2, Y% + 2, Width% - 4, Height% - 4, PeekInt(ColorBank%, 4), 1 ; Background
LineAlpha X%, Y%, X% + Width%, Y%, PeekInt(ColorBank%, 16 + Deep% * 12) ; Border
LineAlpha X%, Y% + 1, X%, Y% + Height% - 1, PeekInt(ColorBank%, 16 + Deep% * 12)
LineAlpha X% + 1, Y% + 1, X% + Width% - 1, Y% + 1, PeekInt(ColorBank%, 20 + Deep * 4)
LineAlpha X% + 1, Y% + 2, X% + 1, Y% + Height% - 2, PeekInt(ColorBank%, 20 + Deep * 4)
LineAlpha X% + Width% - 1, Y% + 2, X% + Width% - 1, Y% + Height% - 1, PeekInt(ColorBank%, 24 - Deep * 4)
LineAlpha X% + 1, Y% + Height% - 1, X% + Width% - 2, Y% + Height% - 1, PeekInt(ColorBank%, 24 - Deep * 4)
LineAlpha X% + Width% , Y% + 1, X% + Width%, Y% + Height%, PeekInt(ColorBank%, 28 - Deep * 12)
LineAlpha X%, Y% + Height%, X% + Width% - 1, Y% + Height%, PeekInt(ColorBank%, 28 - Deep * 12)
End Function

Function DrawPolygonAlpha(X%, Y%, Height%, Farbe%, Down = 0)
Local I%
If Down% Then
	For I% = 1 To Height% - 1
		LineAlpha X% - Height% + I%, Y% + I%, X% + Height% - I%, Y% + I%, Farbe%
		Next
	PlotAlpha X%, Y% + Height%, Farbe%
	Else
	PlotAlpha X%, Y%, Farbe%
	For I% = 1 To Height% - 1
		LineAlpha X% - I%, Y% + I%, X% + I%, Y% + I%, Farbe%
		Next
	EndIf
End Function