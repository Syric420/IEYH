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

int hSocket; /* Handle de la socket */
struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
struct in_addr adresseIP; /* Adresse Internet au format reseau */
struct sockaddr_in adresseSocket;
int tailleSockaddr_in;

int hSocketEcoute, Port_Service, Port_Admin;
char sepTrame, finTrame, sepCsv;
char pwdMaster [50], pwdAdmin[50],loginCsv[50], barqueCsv[50], pedaloCsv[50];
/*
 * 
 */
int main(int argc, char** argv) 
{
    Config();
    int ret, choix;
    hSocket = connectToServ("127.0.0.1", Port_Service);
    char msgClient[MAXSTRING];
    
    /*RequeteStump req;
    req.type=LOGIN_OFFICER;
    strcpy(req.chargeUtile, "test");
    sendSize(hSocket,(void *)&req, LONG_STRUCT);*/
    do
    {
        printf("MENU - IEYH\n\n");
        printf("1. Login\n");
        printf("6. Quitter\n");
        scanf("%d",&choix);
        system("clear");
    }while(choix <= 0 || choix>6);
    switch(choix)
    {
    case 1:
        ret = fctLogin();
        if(ret==0)
        {
            printf("Login réussi");
        }
        else
            printf("Login raté");
    break;
    }
    
    while(1);
}

int fctLogin()
{
    char nomUtilisateur[50], pwd[50];
    char msgClient[MAXSTRING];
    
    viderBuffer();
    printf("Veuillez entrer votre nom d'utilisateur\n");
    fflush(stdin);
    fflush(stdout);
    fgets(nomUtilisateur, sizeof(nomUtilisateur), stdin);
    nomUtilisateur[strlen(nomUtilisateur)-1]='\0';
    
    printf("Veuillez entrer votre mot de passe\n");
    fflush(stdin);
    fflush(stdout);
    fgets(pwd, sizeof(pwd), stdin);
    pwd[strlen(pwd)-1]='\0';
    
    //On crée la requête sous forme de chaine de caractères
    strcpy(msgClient,"1;");
    strcat(msgClient, nomUtilisateur);
    msgClient[strlen(msgClient)]=sepTrame; //pas de strcat car c'est une char et pas un string
    strcat(msgClient, pwd);
    
    sendSep(hSocket, msgClient, finTrame);

    if(receiveSep(hSocket, msgClient, finTrame)==0)
    {
      printf("Erreur sur le recv de la socket connectee : %d\n", errno);
      exit(0);
    }
    printf("Requete recue = %s",msgClient);
    if(strcmp(msgClient,"1;OK")==0)
        return 0;
        
        
    return -1;
                
          
}

void viderBuffer()
{
    int c = 0;
    while (c != '\n' && c != EOF)
    {
        c = getchar();
    }
}

void Config()
{
    int temp;

    FILE* filedesc = fopen("../Serveur_Materiel/ServeurConf.conf","r");
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
        else if(strcmp(str,"loginCsv") == 0)
        {
            strcpy(loginCsv, "");
            strcpy(loginCsv,Buf);
            temp = strlen(loginCsv);
            loginCsv[temp-1]=NULL;//Enlever \0
            printf("loginCsv = %s\n", loginCsv);

        }
        else if(strcmp(str,"pedaloCsv") == 0)
        {
            strcpy(pedaloCsv, "");
            strcpy(pedaloCsv,Buf);
            temp = strlen(pedaloCsv);
            pedaloCsv[temp-1]=NULL;//Enlever \0
            printf("pedaloCsv = %s\n", pedaloCsv);

        }
        else if(strcmp(str,"barqueCsv") == 0)
        {
            strcpy(barqueCsv, "");
            strcpy(barqueCsv,Buf);
            temp = strlen(barqueCsv);
            barqueCsv[temp-1]=NULL;//Enlever \0
            printf("barqueCsv = %s\n", barqueCsv);

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