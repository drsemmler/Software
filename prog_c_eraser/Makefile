C = gcc

all: bin/Eraser

bin/Eraser: src/Eraser.c src/conio.c
	$(C) -Wextra -Wno-unused-result -O2 $+ -o $@

install: bin/Eraser
	cp bin/Eraser /bin/

uninstall:
	rm /bin/Eraser

clean:
	rm bin/Eraser
