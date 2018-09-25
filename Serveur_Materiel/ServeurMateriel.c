/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: syric
 *
 * Created on September 21, 2018, 2:13 AM
 */

#include "ServeurMateriel.h"

/*
 * 
 */
int hSocketEcoute, Port_Service, Port_Admin;
char sepTrame, finTrame, sepCsv;
char pwdMaster [50], pwdAdmin[50];
int main(int argc, char** argv) {
    Config();
    char * test;
    
    
    /* Socket d'ecoute pour l'attente */
    int hSocketService;
    int i,j, /* variables d'iteration */
    retRecv; /* Code de retour dun recv */
    struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
    struct in_addr adresseIP; /* Adresse Internet au format reseau */
    struct sockaddr_in adresseSocket;
    int tailleSockaddr_in;
    char msgClient [MAXSTRING];
    hSocketEcoute = confSockSrv("localhost", 50001);
    
    puts("Thread principal : en attente d'une connexion");
    if (listen(hSocketEcoute,SOMAXCONN) == -1)
    {
      printf("Erreur sur le listen de la socket %d\n", errno);
      close(hSocketEcoute); /* Fermeture de la socket */
      exit(1);
    }
    else printf("Listen socket OK\n");
    /* 8. Acceptation d'une connexion */
    tailleSockaddr_in = sizeof(struct sockaddr_in);
    if ( (hSocketService =
      accept(hSocketEcoute, (struct sockaddr *)&adresseSocket, &tailleSockaddr_in) )
      == -1)
      {
        printf("Erreur sur l'accept de la socket %d\n", errno);
        close(hSocketEcoute); /* Fermeture de la socket */
        exit(1);
      }
      else printf("Accept socket OK\n");
    
    //retRecv = receiveSize(hSocketService, msgClient, MAXSTRING);
    retRecv = receiveSep(hSocketService, msgClient, finTrame);
    test = malloc(sizeof(char) * retRecv);
    strcpy(test, msgClient);
    puts(test);
    fflush(stdout);
    
    while(1);
    return (EXIT_SUCCESS);
}

void Config()
  {
    int temp;

    FILE* filedesc = fopen("ServeurConf.conf","r");
    if(filedesc==NULL)
    {
        puts("Erreur d'ouverture du fichier");
        exit(0);
    }
    
    char *Buf = NULL,*str;
    size_t len = 0;
    ssize_t read;
    do
    {
      read = getline(&Buf,&len,filedesc);
      if(read != -1)
      {
        str=strtok(Buf,"=");//recupere avant egal
        Buf=strtok(NULL,"=");//recupere apres egal
        if(strcmp(str,"Port_Service") == 0)
        {
            temp=atoi(Buf);
            Port_Service=temp;
            printf("Port_Service = %d\n", Port_Service);
        }
        else if(strcmp(str,"Port_Admin") == 0)
        {
            temp=atoi(Buf);
            Port_Admin=temp;
            printf("Port_Admin = %d\n", Port_Admin);
        }
        else if(strcmp(str,"sep-trame") == 0)
        {
            sepTrame = Buf[0];
            printf("sep-trame = %c\n", sepTrame);
        }
        else if(strcmp(str,"fin-trame") == 0)
        {
            finTrame = Buf[0];
            printf("fin-trame = %c\n", finTrame);
        }
        else if(strcmp(str,"sep-csv") == 0)
        {
            sepCsv = Buf[0];
            printf("sep-csv = %c\n", sepCsv);
        }
        else if(strcmp(str,"pwdMaster") == 0)
        {
          strcpy(pwdMaster, "");
          strcpy(pwdMaster,Buf);
          temp = strlen(pwdMaster);
          pwdMaster[temp-1]=NULL;//Enlever \0
          printf("pwdMaster = %s\n", pwdMaster);
        }
        else if(strcmp(str,"pwdAdmin") == 0)
        {
          strcpy(pwdAdmin, "");
          strcpy(pwdAdmin,Buf);
          temp = strlen(pwdAdmin);
          pwdAdmin[temp-1]=NULL;//Enlever \0
          printf("pwdAdmin = %s\n", pwdAdmin);
        }
        
      }
    } while(read != -1);

  }