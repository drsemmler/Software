/** conio.c **
 * This file is a replacement for the windows conio.c, so that the same interface is available under linux.
 * It uses ANSII escepe sequences to change background and foreground colors seperately.
**/

#include <stdio.h>
char conio_Dec, conio_Color, conio_Back;

void ChangeColor() {
	printf("\033[0m\033\133%d;3%d;4%dm", conio_Dec, conio_Color, conio_Back);
}

void textcolor(char Col) {
	conio_Color = Col & 7;
	if(Col & 8) conio_Dec = 1; else conio_Dec = 0;
	ChangeColor();
}

void textbackground(char Col) {
	conio_Back = (Col & 7);
	ChangeColor();
}
