/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   SocketUtilities.c
 * Author: syric
 *
 * Created on September 24, 2018, 7:46 AM
 */
#include "SocketUtilities.h"



int receiveSize(int socket, char * struc, int size)
{
  char *buf = (char*)malloc(100);
  fflush(stdout);
  int retRecv;
  if ((retRecv=recv(socket, struc, size, 0)) == -1)
  {
    printf("Erreur sur le recv de la socket %d\n", errno);
    fflush(stdout);
    close(socket); /* Fermeture de la socket */
  }
  else
  if (retRecv==0)
  {
    sprintf(buf,"Le client est parti !!!\n"); printf("%s", buf);
    fflush(stdout);
    return 0;
  }
  else
  {
    /*sprintf(buf,"Message recu = %s\n", ((char*)struc));
    printf("%s", buf);*/
    fflush(stdout);
    return 1;
  }
}


int receiveSep(int socket, char * msgClient, char sep)
{
    char buf[5500];
    int tailleMsgRecu, finDetectee,nbreBytesRecus, tailleO, tailleS;
    
    tailleMsgRecu = 0;
    finDetectee = 0;
    memset(buf,0,sizeof(buf));
    
    tailleO=sizeof(int);
    if (getsockopt(socket, IPPROTO_TCP, TCP_MAXSEG, &tailleS, &tailleO) == -1)
    {
        printf("Erreur sur le getsockopt de la socket %d\n", errno);
        exit(1);
    }
    else
    {
        printf("getsockopt OK\n");
        printf("Taille maximale d'un segment = %d\n", tailleS);
    }
    
    do
    {
        puts("Passage boucle de reception");
        if ( (nbreBytesRecus = recv(socket, buf, MAXSTRING, 0)) == -1)
        {
            printf("Erreur sur le recv de la socket %d\n", errno);
            close(socket); close(socket); exit(1);
        }
        else
        {
            finDetectee = marqueurRecu (buf, sep);
            memcpy((char *)msgClient + tailleMsgRecu, buf,nbreBytesRecus);
            tailleMsgRecu += nbreBytesRecus;
            printf("finDetecteee = %d\n", finDetectee);
            printf("Nombre de bytes recus = %d\n", nbreBytesRecus );
            printf("Taile totale msg recu = %d\n", tailleMsgRecu );
        }
    }while (nbreBytesRecus != 0 && nbreBytesRecus != -1 && finDetectee!=0);
    
    return tailleMsgRecu;
}

char marqueurRecu (char *m, char sep)
/* Recherche de la sequence # */
{
    int longueurMsg, i;
    
    longueurMsg = strlen(m);
    
    for(i=0;i<longueurMsg;i++)
    {
        if(m[i]==sep)
        {
            printf("# trouveeeeee\n");
            return 0;
        }   
    }
    
    return -1;
}

int sendSep(int socket,char * msg, char sep)
{
  char *buffer;
  //printf("Ctout sep = %c",sep);
  fflush(stdout);
  //buffer = malloc((char)strlen(msg)+sizeof(char));
  printf("strlen = %d\n",strlen(msg));
  fflush(stdout);
  buffer = malloc(sizeof(char) * strlen(msg) + sizeof(char));
  
  strcpy(buffer, msg);
  buffer[strlen(msg)]=sep;
  //strcat(buffer,sep);
  printf("Msg dans sendSep = %s", buffer);
  fflush(stdout);
  if (send(socket, buffer, strlen(buffer), 0) == -1)
  {
    printf("Erreur sur le send de la socket %d\n", errno);
    close(socket); /* Fermeture de la socket */
    return -1;
  }
  else 
    return 0;
}

int sendSize(int socket,char * buf, int size)
{

  if (send(socket, buf, size, 0) == -1)
  {
    printf("Erreur sur le send de la socket %d\n", errno);
    close(socket); /* Fermeture de la socket */
    return 0;
  }
  else return 1;
}

int connectToServ(char * ip, int port )
{
    int hSocket;
    struct hostent * infosHost; 
    struct in_addr adresseIP; 
    struct sockaddr_in adresseSocket; 
    unsigned int tailleSockaddr_in;
    int ret;
    
    /* 1. Création de la socket */
    hSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (hSocket == -1)
    {
        printf("ToServ: Erreur de creation de la socket %d\n", errno);
        exit(1);
    }
    else printf("ToServ: Creation de la socket OK\n");
    fflush(stdout);
    
    
    /* 3. Préparation de la structure sockaddr_in */
    memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
    adresseSocket.sin_family = AF_INET; /* Domaine */
    adresseSocket.sin_port = htons(port); /* conversion port au format réseau */
    //memcpy(&adresseSocket.sin_addr, infosHost->h_addr,infosHost->h_length);
    adresseSocket.sin_addr.s_addr = inet_addr(ip);
    
    /* 4. Tentative de connexion */
    tailleSockaddr_in = sizeof(struct sockaddr_in);
    if (( ret = connect(hSocket, (struct sockaddr *)&adresseSocket, tailleSockaddr_in) )
    == -1)
    {
        printf("ToServ: Erreur sur connect de la socket %d\n", errno);
        close(hSocket);
        return -1;
    }
    else printf("ToServ: Connect socket OK\n");
    
    return hSocket;
}

int confSockSrv(char *adresse,int Port)
{
  struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
  struct in_addr adresseIP; /* Adresse Internet au format reseau */
  struct sockaddr_in adresseSocket;
  int hSocketEcoute;

  hSocketEcoute = socket(AF_INET,SOCK_STREAM,0);
  if (hSocketEcoute == -1)
  {
    printf("Erreur de creation de la socket %d\n", errno);
    exit(1);
  }
  else printf("Creation de la socket OK\n");
  /* 3. Acquisition des informations sur l'ordinateur local */
  if ( (infosHost = gethostbyname(adresse))==0)
  {
    printf("Erreur d'acquisition d'infos sur le host %d\n", errno);
    exit(1);
  }
  else printf("Acquisition infos host OK\n");
  memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);
  printf("Adresse IP = %s\n",inet_ntoa(adresseIP));
  /* 4. Préparation de la structure sockaddr_in */
  memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
  adresseSocket.sin_family = AF_INET;
  adresseSocket.sin_port = htons(Port);
  memcpy(&adresseSocket.sin_addr, infosHost->h_addr, infosHost->h_length);
  /* 5. Le système prend connaissance de l'adresse et du port de la socket */
  if (bind(hSocketEcoute, (struct sockaddr *)&adresseSocket,
  sizeof(struct sockaddr_in)) == -1)
  {
    printf("Erreur sur le bind de la socket %d\n", errno);
    exit(1);
  }
  else printf("Bind adresse et port socket OK\n");

  return hSocketEcoute;
}
int confSockCli(char *adresse,int Port)
{
  struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
  struct in_addr adresseIP; /* Adresse Internet au format reseau */
  struct sockaddr_in adresseSocket;
  int hSocket,ret,tailleSockaddr_in;

  hSocket = socket(AF_INET, SOCK_STREAM, 0);
  if (hSocket == -1)
  {
    printf("Erreur de creation de la socket %d\n", errno);
    exit(1);
  }
  else printf("Creation de la socket OK\n");
  /* 2. Acquisition des informations sur l'ordinateur distant */
  if ( (infosHost = gethostbyname(adresse))==0)
  {
    printf("Erreur d'acquisition d'infos sur le host distant %d\n", errno);
    exit(1);
  }
  else printf("Acquisition infos host distant OK\n");
  memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);
  printf("Adresse IP = %s\n",inet_ntoa(adresseIP));
  /* 3. Préparation de la structure sockaddr_in */
  memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
  adresseSocket.sin_family = AF_INET; /* Domaine */
  adresseSocket.sin_port = htons(Port);

  memcpy(&adresseSocket.sin_addr, infosHost->h_addr,infosHost->h_length);
  tailleSockaddr_in = sizeof(struct sockaddr_in);
  if (( ret = connect(hSocket, (struct sockaddr *)&adresseSocket, tailleSockaddr_in) )
  == -1)
  {
    printf("Erreur sur connect de la socket %d\n", errno);
    switch(errno)
    {
      case EBADF : printf("EBADF - hsocket n'existe pas\n");
      break;
      default : printf("Erreur inconnue ?\n");
    }
    close(hSocket);
    exit(1);
  }
  else printf("Connect socket OK\n");

  return hSocket;

}

