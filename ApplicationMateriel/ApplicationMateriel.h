/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   ApplicationMateriel.h
 * Author: syric
 *
 * Created on September 24, 2018, 8:05 AM
 */

#ifndef APPLICATIONMATERIEL_H
#define APPLICATIONMATERIEL_H

#include <stdio.h>
#include <stdlib.h> /* pour exit */
#include <string.h> /* pour memcpy */
#include <unistd.h>
//#include <sys/types.h>
#include <sys/socket.h> /* pour les types de socket */
#include <netdb.h> /* pour la structure hostent */
#include <errno.h>
#include <netinet/in.h> /* pour la conversion adresse reseau->format dot
ainsi que le conversion format local/format
reseau */
#include <netinet/tcp.h> /* pour la conversion adresse reseau->format dot */
#include <arpa/inet.h> /* pour la conversion adresse reseau->format dot */
#include "../Serveur_Materiel/SocketUtilities.h"
//#include "tcpiter.h"
#define DOC "DENY_OF_CONNEXION" /*  dans tcpiter.h */
#define EOC "END_OF_CONNEXION"
#define MAXSTRING 5000 /* Longueur des messages */

#define LOGIN_OFFICER 1
#define LOGOUT_OFFICER 2
#define CHECK_TICKET 3
#define PAYMENT_DONE 4

/*void Config();
void Init();
int Login();
int MenuConnexion();
int MenuPrincipal();
int demandeTicket();
int sendMessage(char *);
int addLugagge(int,int);
char * rcvMessage();*/

#endif /* APPLICATIONMATERIEL_H */

