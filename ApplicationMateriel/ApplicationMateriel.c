/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: syric
 *
 * Created on September 24, 2018, 8:00 AM
 */

#include "ApplicationMateriel.h"

int Port_Service;

int hSocket; /* Handle de la socket */
struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
struct in_addr adresseIP; /* Adresse Internet au format reseau */
struct sockaddr_in adresseSocket;
int tailleSockaddr_in;
/*
 * 
 */
int main(int argc, char** argv) {
    int i;
    hSocket = connectToServ("127.0.0.1", 50001);
    RequeteStump req;
    req.type=LOGIN_OFFICER;
    strcpy(req.chargeUtile, "test");
    sendSize(hSocket,(void *)&req, LONG_STRUCT);
}

