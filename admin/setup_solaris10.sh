#
# Solaris 10 VirtualBox image :
#   https://cds.sun.com/is-bin/INTERSHOP.enfinity/WFS/CDS-CDS_SMI-Site/en_US/-/USD/ViewProductDetail-Start?ProductRef=virtualbox-s10U8-x86-G-F@CDS-CDS_SMI
#
# Solaris DVD downloads : 
#   http://www.oracle.com/technetwork/server-storage/solaris/downloads/index.html
#
# Install Blastwave's packaging system for GCC and al. (see commands below) :
#   http://www.blastwave.org/jir/blastwave.fam
#
# Eclipse Solaris downloads : 
#   http://ftp.rnl.ist.utl.pt/pub/eclipse/downloads/drops/R-3.5-200906111540/solPlatform.php
#
# Install JDK 6 :
#   http://www.oracle.com/technetwork/java/javase/downloads/index.html
# 
pkgadd -G -d http://download.blastwave.org/csw/pkgutil_`/sbin/uname -p`.pkg
/opt/csw/bin/pkgutil --catalog 
/opt/csw/bin/pkgutil -y --install gnupg textutils 
/opt/csw/bin/gpg --keyserver pgp.mit.edu --recv-keys A1999E90
gpg --list-keys

echo "Trust
5
quit
" | /opt/csw/bin/gpg --edit-key A1999E90

# Uncomment the line '# use_gpg = 1' :
cat /etc/opt/csw/pkgutil.conf | sed 's/# use_gpg/use_gpg/' > .tmp
rm /etc/opt/csw/pkgutil.conf.old
rm /etc/opt/csw/pkgutil.conf /etc/opt/csw/pkgutil.conf.old
mv .tmp /etc/opt/csw/pkgutil.conf

/opt/csw/bin/pkgutil -y --install maven2 gcc4 curl openssh p7zip bzip2 subversion vim binutils wget git

#
# NOW RUN THIS AS A NORMAL USER :
#

export USER_LOGIN=ochafik
export USER_HOME=/export/home/$USER_LOGIN
export USER_NAME="Olivier Chafik"
export BIN_DIR="/export/home/$USER_LOGIN/bin"

useradd -d /export/home/$USER_LOGIN -m -s /bin/bash -c "$USER_NAME" $USER_LOGIN

su $USER_LOGIN

mkdir src
mkdir bin

ls -s /opt/csw/bin/gar $BIN_DIR
ls -s /opt/csw/bin/gmake $BIN_DIR
ls -s /opt/csw/gcc4/bin/gcc $BIN_DIR

cd src

svn co https://dyncall.org/svn/dyncall/trunk dyncall
svn co https://nativelibs4java.googlecode.com/svn/trunk/libraries nativelibs4java

cd ..
cd bin

ln -s /opt/csw/bin/gar ar

cd ..

#
# Scala & sbt
#
export SCALA_HOME=/export/home/$USER_LOGIN/bin/scala
export SCALA_VERSION=2.9.0.1.final
wget http://www.scala-lang.org/downloads/distrib/files/scala-$SCALA_VERSION.tgz
mv scala-$SCALA_VERSION.tgz scala-$SCALA_VERSION.tar.gz
gunzip scala-$SCALA_VERSION.tar.gz
tar xvf scala-$SCALA_VERSION.tar
rm -fR $SCALA_HOME
mv scala-$SCALA_VERSION $SCALA_HOME
rm scala-$SCALA_VERSION.tar.gz

export SBT_HOME=/export/home/$USER_LOGIN/bin
#mkdir $SBT_HOME
wget http://simple-build-tool.googlecode.com/files/sbt-launch-0.7.4.jar -O $SBT_HOME/sbt-launch.jar
echo 'java -server -XX:+UseParallelGC -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -Xmx512M -jar' $SBT_HOME'/sbt-launch.jar "$@"' > $SBT_HOME/sbt
chmod +x $SBT_HOME/sbt

# jEdit :
JEDIT_VERSION=4.3.2
wget http://downloads.sourceforge.net/project/jedit/jedit/$JEDIT_VERSION/jedit${JEDIT_VERSION}install.jar
java -jar jedit${JEDIT_VERSION}install.jar

#
# Set paths
#
echo "
export SCALA_HOME=$USER_HOME/bin/scala
export DYNCALL_HOME=$USER_HOME/src/dyncall
export JAVA_HOME=$USER_HOME/bin/jdk1.6.0_24

export PATH=$USER_HOME/bin:\$PATH
export PATH=\$JAVA_HOME/bin:\$PATH
export PATH=\$SCALA_HOME/bin:\$PATH
export PATH=/opt/csw/bin:\$PATH
export PATH=/opt/csw/gcc4/bin:\$PATH

export LD_LIBRARY_PATH=/opt/csw/lib:/opt/csw/gcc4/lib:\$LD_LIBRARY_PATH

alias cc=gcc
alias ar=gar
alias make=gmake
alias sed=gsed
" > ~/.bashrc


