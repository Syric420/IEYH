#include <stdio.h>
#include <stdlib.h> /* pour exit */
#include <string.h> /* pour memcpy */
#include <unistd.h>
//#include <sys/types.h>
#include <sys/socket.h> /* pour les types de socket */
#include <netdb.h> /* pour la structure hostent */
#include <errno.h>
#include <netinet/in.h> /* pour la conversion adresse reseau->format dot ainsi que le conversion format local/format reseau */
#include <netinet/tcp.h> /* pour la conversion adresse reseau->format dot */
#include <arpa/inet.h> /* pour la conversion adresse reseau->format dot */
//#include "SocketsUtilities.c"
//#include "tcpiter.h"
#define DOC "DENY_OF_CONNEXION" /*  dans tcpiter.h */
#define EOC "END_OF_CONNEXION"
#define MAXSTRING 160 /* Longueur des messages */

#ifndef SOCKETUTILITIES_H
#define SOCKETUTILITIES_H

int receiveSize(int, void *, int);
int sendSize(int, void *, int);
int confSockSrv(char*,int);
char marqueurRecu (char *, char);
int receiveSep(int, char *, char );
int sendSep(int ,char * , char );
//int confSockCli(char*,int);
//int connectToServ(char *, int);


#endif /* SOCKETUTILITIES_H */

