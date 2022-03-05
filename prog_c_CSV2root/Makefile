CXX = `root-config --cxx`

all: bin/CSV2root

bin/CSV2root: src/CSV2root.c src/ReadCSV.c
	mkdir -p bin
	$(CXX) -g2 -O3 -W -Wextra -o $@ src/CSV2root.c -I`root-config --incdir --libs`

install: bin/Eraser
	cp CSV2root /bin
	cp CSV2root.1 /usr/share/man/man1

uninstall:
	rm /bin/CSV2root
	rm /usr/share/man/man1/CSV2root.1

clean:
	rm -r bin
