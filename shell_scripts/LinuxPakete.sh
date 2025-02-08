#!/bin/cat

# A list of recommended packages for different purposes to set up a fresh linux system for peraonal use.
# This list is not a script ready to install, because you probably don't want to install all of them,
# so I leave it as code snippets rather than a sealed script.

sudo add-apt-repository 'deb http://downloads.sourceforge.net/project/ubuntuzilla/mozilla/apt all main'
sudo apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 2667CA5C
sudo apt-get update
sudo apt-get upgrade

sudo apt-get install terminator screen gimp pdfchain vlc rar unrar htop sshfs # Essential Tools for the GUI
sudo apt-get install pdfsam geany emacs joe r-base r-recommended # Further Tools
sudo apt-get install net-tools openssh-server # For Lubuntu
sudo apt-get install texlive-full # LaTex
sudo apt-get install kdenlive libavformat-dev libavcodec-dev libmp3lame-dev libfaac-dev libfaad-dev libxvidcore4 # Video editing
sudo apt-get install gimp gimp-help-de language-pack-gnome-de inkscape libwmf-bin pstoedit sketch # Image editing
sudo apt-get install sane xsane libsane gocr cuneiform tesseract-ocr # Scan
sudo apt-get install lm-sensors # For detecting temperatures via I²C interface

# For the web
sudo apt-get install seamonkey-mozilla-build # Seamonkey browser (I use it for email, only now because the maintenance got worse)
sudo apt-get install chromium-browser
sudo apt-get install mariadb-server # Database server
sudo apt-get install apache2 php8 python3 # Web Server

# Machine learning
sudo apt-get install python3-pip python3-dev build-essential cmake git unzip pkg-config libopenblas-dev liblapack-dev python3-numpy python3-scipy python3-matplotlib python3-yaml libhdf5-serial-dev python3-h5py graphviz python3-opencv
sudo pip3 install pydot-ng theano keras tensorflow tensorflow-gpu

sudo apt autoremove
sudo apt-get check

snap install dbeaver-ce jami # Database browser and telephone tool (if you know better ones let me know)
snap install --classic android-studio
snap install --classic mysql-workbench-community
snap install --classic flutter
snap install --classic intellij-idea-community
snap install --classic pycharm-community
snap install --classic node
