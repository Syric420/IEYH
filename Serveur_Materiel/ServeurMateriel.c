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
//les mutex + variables de conditions
pthread_mutex_t mutexIndiceCourant;
pthread_mutex_t mutexFichierTicket;
pthread_cond_t condIndiceCourant;

int indiceCourant=-1;
int hSocketEcoute, Port_Service, Port_Admin;
char sepTrame, finTrame, sepCsv;
char pwdMaster [50], pwdAdmin[50];
int hSocketServices[NB_MAX_CLIENTS];
pthread_t threadHandle[NB_MAX_CLIENTS];



int main(int argc, char** argv) {
    Config();
    int ret;
    struct sigaction sigact;
    sigemptyset(&sigact.sa_mask);
    sigact.sa_handler=HandlerQuit;
    if (sigaction(SIGINT,&sigact,NULL) == -1)
        perror("Erreur d'armement du signal SIGQUIT");
    
    
    /* Socket d'ecoute pour l'attente */
    int i,j, /* variables d'iteration */
    retRecv; /* Code de retour dun recv */
    struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
    struct in_addr adresseIP; /* Adresse Internet au format reseau */
    struct sockaddr_in adresseSocket;
    int tailleSockaddr_in;
    char msgClient [MAXSTRING];
    int hSocketService;
    
    /* Si la socket n'est pas utilisee, le descripteur est a -1 */
    for (i=0; i<NB_MAX_CLIENTS; i++) hSocketServices[i] = -1;
    
    hSocketEcoute = confSockSrv("localhost", 50001);
    
    /* 6. Lancement des threads */
    for (i=0; i<NB_MAX_CLIENTS; i++)
    {
      ret = pthread_create(&threadHandle[i],NULL,fctThread, (void*)i);
      printf("Thread secondaire %d lance !\n", i);
      ret = pthread_detach(threadHandle[i]);
    }
    
    do
    {
      /* 7. Mise a l'ecoute d'une requete de connexion */
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
      
        /* 9. Recherche d'une socket connectee libre */
        printf("Recherche d'une socket connecteee libre ...\n");
        for (j=0; j<NB_MAX_CLIENTS && hSocketServices[j] !=-1; j++);
        if (j == NB_MAX_CLIENTS)
        {
          printf("Plus de connexion disponible\n");
          sprintf(msgClient,DOC);
          sendSize(hSocketService,msgClient,MAXSTRING);
          close(hSocketService); /* Fermeture de la socket */
        }
        else
        {
          /* Il y a une connexion de libre */
          printf("Connexion sur la socket num. %d\n", j);
          pthread_mutex_lock(&mutexIndiceCourant);
          hSocketServices[j] = hSocketService;
          indiceCourant=j;
          pthread_mutex_unlock(&mutexIndiceCourant);
          pthread_cond_signal(&condIndiceCourant);
        }
      }while (1);
    
    
    //Pour recevoir une trame avec caractère FinTrame
    /*
    retRecv = receiveSep(hSocketService, msgClient, finTrame);
    test = malloc(sizeof(char) * retRecv);
    strcpy(test, msgClient);*/
    
    
    //Pour recevoir une structure
    /*RequeteStump *req = (RequeteStump *)malloc(sizeof(RequeteStump));
    retRecv = receiveSize(hSocketService, req, LONG_STRUCT);
    printf("req type = %d\nreq msg = %s", req->type, req->chargeUtile);
    fflush(stdout);*/
    return (EXIT_SUCCESS);
}

/* -------------------------------------------------------- */
void * fctThread (void *param)
{
    int vr = (int)(param), finDialogue=0, i, iCliTraite;
    int hSocketServ;
    char * numThr = getThreadIdentity(), *buf = (char*)malloc(100);;
    char msgClient[MAXSTRING];
    
    while (1)
    {
        /* 1. Attente d'un client à traiter */
        pthread_mutex_lock(&mutexIndiceCourant);
        while (indiceCourant == -1)
            pthread_cond_wait(&condIndiceCourant, &mutexIndiceCourant);
        iCliTraite = indiceCourant; indiceCourant=-1;
        hSocketServ = hSocketServices[iCliTraite];
        pthread_mutex_unlock(&mutexIndiceCourant);
        sprintf(buf,"Je m'occupe du numero %d ... avec la socket %d", iCliTraite, hSocketServ);affThread(numThr, buf);
        
        /* 2. Dialogue thread-client */
        /*if(receiveSep(hSocketServ, msgClient))
          printf("\nReceive sep = %s\n", msgClient);*/
        finDialogue=0;
        do
        {
            if(receiveSep(hSocketServ, msgClient, finTrame)==0)
            {
              printf("Erreur sur le recv de la socket connectee : %d\n", errno);
              exit(0);
            }

            printf("Requete recue = %s",msgClient);
            fflush(stdout);
            if (strcmp(msgClient, EOC)==0)
            {
              finDialogue=1; break;
            }
        }while (!finDialogue);
        
        /* 3. Fin de traitement */
        pthread_mutex_lock(&mutexIndiceCourant);
        hSocketServices[iCliTraite]=-1;
        pthread_mutex_unlock(&mutexIndiceCourant);
        close (hSocketServ);
    }
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

char * getThreadIdentity()
{
    unsigned long numSequence;
    char *buf = (char *)malloc(30);
    sprintf(buf, "%d", getpid());
    return buf;
} 
 
 void HandlerQuit(int var)
{
     int i;
    printf("Fin du serveur\n");
    fflush(stdout);
    pthread_mutex_lock(&mutexIndiceCourant);
    //Couper toutes les sockets de services
    for(i=0;i<NB_MAX_CLIENTS;i++)
    {
        printf("Coupe la socket numero : %d\n",i);
        close(hSocketServices[i]);
        hSocketServices[i]=-1;
    }
    pthread_mutex_unlock(&mutexIndiceCourant);
    close(hSocketEcoute);
    exit(0);
}
  