char conio_Dec, conio_Color, conio_Back;

void textcolor(char Col) {
	conio_Color = Col & 7;
	if(Col & 8) conio_Dec = 1; else conio_Dec = 0;
	ChangeColor();
}

void textbackground(char Col) {
	conio_Back = (Col & 7);
	ChangeColor();
}

void ChangeColor() {
	printf("\033[0m\033\133%d;3%d;4%dm", conio_Dec, conio_Color, conio_Back);
}
