sudo apt-get install subversion git maven2 openjdk-7-jdk g++

mkdir src
mkdir bin

cd src

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


