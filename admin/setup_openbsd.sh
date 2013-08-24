# http://www.openbsd.org/faq/faq15.html
# http://www.cyberciti.biz/faq/openbsd-install-ports-collection/
# su
# cd /usr
# mv ports ports.old
# wget ftp://ftp.openbsd.org/pub/OpenBSD/$(uname -r)/ports.tar.gz
# tar -zxvf ports.tar.gz

sudo pkg_add install jdk-1.7

sudo pkg_add install jdk-1.6

#cd /tmp
#ftp ftp://ftp.openbsd.org/pub/OpenBSD/5.3/ports.tar.gz
#cd /usr
#sudo tar xzf /tmp/ports.tar.gz
#cd /usr/ports/java
#make install
