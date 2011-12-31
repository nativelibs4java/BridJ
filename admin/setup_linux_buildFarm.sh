sudo apt-get install subversion git maven2

mkdir src
mkdir bin

cd src

svn co https://dyncall.org/svn/dyncall/trunk dyncall
git clone https://ochafik@github.com/ochafik/nativelibs4java.git

cd dyncall
echo "export DYNCALL_HOME=\"`pwd`\"" >> ~/.bashrc

cat ../nativelibs4java/libraries/Runtime/BridJ/src/main/cpp/bridj/dyncall.diff | sed 's/~\/src\/dyncall\///' | patch -p0

cd
cd src/nativelibs4java/libraries
mvn install -DskipTests

#cd
#cd bin
#
#export MAVEN_VERSION=2.2.1
#wget ftp://ftp.inria.fr/pub/Apache//maven/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz
#tar zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz
#echo "export PATH=`pwd`/apache-maven-$MAVEN_VERSION/bin:\$PATH" >> ~/.bashrc
#chmod +x apache-maven-$MAVEN_VERSION/bin/*

#cd
#bash


