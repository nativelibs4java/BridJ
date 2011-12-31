# Also see http://www.alexwhittemore.com/?p=398

apt-get install jikes jamvm vim

# Add MacBook Pro key for autologin :
mkdir ~/.ssh
touch ~/.ssh/authorized_keys
echo "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq9hvZ0QQYxxRQES6ktR+pCVfHPb2AV/ociz0W9ycM9PUFR3aMZ/mj5x3OjoQZfNn3Rpy9Sx9ybQcM8uZLt+U1T4n4EdV2FehKeyIdm8IQaBTfMIDG3oNNdakgnZaSahs6LRUkJnZ7XvWxoP/bmj1ujpySvKioq9K38kGnuuE4CcW2Lm9yHv0X8o/sn8uBOD+64o4XS8vw6rr5Gbl15GYsaiPVmR3To3D4+L1U6sKmUXaa+ny2w0SgN6iAFmIFeYiUQ3s3nnjLou1Wk01Gzv2K2ywgD4e2bzHOjurVlEx8mwWWYLuS0XcaBp6Q9S3ofVj9YaPfT9WDrU3YB4RB/H83Q== olivier.chafik@gmail.com" >> ~/.ssh/authorized_keys

# Install GCC :
# http://blog.syshalt.net/index.php/2010/09/12/compile-c-applications-with-gcc-on-ios-4-iphone/

wget http://www.syshalt.net/pub/iphone/gcc-iphone/fake-libgcc_1.0_iphoneos-arm.deb
dpkg Ði fake-libgcc_1.0_iphoneos-arm.deb
apt-get install iphone-gcc
wget http://www.syshalt.net/iphone/gcc-iphone/sdk-2.0-headers.tar.gz
tar -xvzf sdk-2.0-headers.tar.gz
cd include-2.0-sdk-ready-for-iphone
cp Ðr * /usr/include
cd ..
wget http://www.syshalt.net/iphone/gcc-iphone/gcc_files.tar.gz
tar -xvzf gcc_files.tar.gz
cd gcc_files
cp Ðr * /usr/lib
apt-get install ldid
chmod -R 755 /usr/include
# Sign your compiled aplication using: 
#   ldid ÐS <application>

export IOS_SDK=/Developer/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS4.3.sdk
export LIBSTD_CPP_VERSION=6.0.9

ssh root@iphone "mv /usr/include/c++ /usr/include/c++-old"
scp -r $IOS_SDK/usr/include/c++ root@iphone:/usr/include/c++
scp $IOS_SDK/usr/lib/libstdc++.$LIBSTD_CPP_VERSION.dylib root@iphone:/usr/lib/libstdc++.$LIBSTD_CPP_VERSION.dylib
ssh root@iphone "ln -s /usr/lib/libstdc++.$LIBSTD_CPP_VERSION.dylib /usr/lib/libstdc++.dylib"

# Install Scala and sbt :
export USER_LOGIN=mobile
export USER_HOME=/var/$USER_LOGIN

cd
mkdir bin
cd bin
export SCALA_HOME=$USER_HOME/bin/scala
export SCALA_VERSION=2.9.0.1
wget http://www.scala-lang.org/downloads/distrib/files/scala-$SCALA_VERSION.tgz
mv scala-$SCALA_VERSION.tgz scala-$SCALA_VERSION.tar.gz
gunzip scala-$SCALA_VERSION.tar.gz
tar xvf scala-$SCALA_VERSION.tar
rm -fR $SCALA_HOME
mv scala-$SCALA_VERSION $SCALA_HOME
rm scala-$SCALA_VERSION.tar.gz


export SBT_HOME=$USER_HOME/bin
#mkdir $SBT_HOME
wget http://simple-build-tool.googlecode.com/files/sbt-launch-0.7.4.jar -O $SBT_HOME/sbt-launch.jar
echo 'java -server -XX:+UseParallelGC -XX:+DoEscapeAnalysis -XX:+UseCompressedOops -Xmx512M -jar' $SBT_HOME'/sbt-launch.jar "$@"' > $SBT_HOME/sbt
chmod +x $SBT_HOME/sbt

#
# JamVM
#
# http://draenog.blogspot.com/2011/02/openjdkjamvm-git-repository.html
cd
cd src
git clone git://git.berlios.de/jamvm
cd jamvm
./autogen.sh --with-java-runtime-library=openjdk


#
# Set paths
#
echo "
export SCALA_HOME=$USER_HOME/bin/scala
export DYNCALL_HOME=$USER_HOME/src/dyncall/dyncall

export PATH=$USER_HOME/bin:\$PATH
export PATH=\$SCALA_HOME/bin:\$PATH

" > ~/.profile

# Install Maven
cd 
cd bin
export MAVEN_VERSION=2.2.1
wget ftp://ftp.inria.fr/pub/Apache//maven/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz
tar zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz
echo "export PATH=`pwd`/apache-maven-$MAVEN_VERSION/bin:\$PATH" >> ~/.profile
chmod +x apache-maven-$MAVEN_VERSION/bin/*


# Backup
# http://www.iphonedownloadblog.com/2010/11/24/how-to-backup-your-cydia-apps/
ssh root@iphone "dpkg --get-selections" > admin/iphone.pkgs
