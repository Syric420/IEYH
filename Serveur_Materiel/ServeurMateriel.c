#include "ServeurMateriel.h"
#include "SocketUtilities.h"

//les mutex + variables de conditions
pthread_mutex_t mutexIndiceCourant;
pthread_cond_t condIndiceCourant;

int hSocketEcoute;

//variables pour ServeurConf
int Port_Service, Port_Admin;
char sepTrame, finTrame, sepCsv;
char pwdMaster [50], pwdAdmin[50],loginCsv[50], barqueCsv[50], pedaloCsv[50];

//vecteurs de sockets + handles
int indiceCourant=-1;
int hSocketServices[NB_MAX_CLIENTS];
pthread_t threadHandle[NB_MAX_CLIENTS];

int main(int argc, char** argv) 
{
	Config();
	int ret;
	struct sigaction sigact;
	sigemptyset(&sigact.sa_mask);
	sigact.sa_handler=HandlerQuit;
	if (sigaction(SIGINT,&sigact,NULL) == -1)
		perror("main: Erreur d'armement du signal SIGQUIT");
    
	/* Socket d'ecoute pour l'attente */
	int i,j, /* variables d'iteration */
	retRecv; /* Code de retour dun recv */
	
	char msgServeur[MAXSTRING];
	int hSocketService;

	/* Si la socket n'est pas utilisee, le descripteur est a -1 */
	for (i=0; i<NB_MAX_CLIENTS; i++) hSocketServices[i] = -1;

	hSocketEcoute = confSockSrv("localhost", Port_Service);

	/* 6. Lancement des threads */
	for (i=0; i<NB_MAX_CLIENTS; i++)
	{
		ret = pthread_create(&threadHandle[i],NULL,fctThread, (void*)i);
		printf("main: Thread secondaire %d lance !\n", i);
		ret = pthread_detach(threadHandle[i]);
	}
    
	do
	{
		/* 7. Mise a l'ecoute d'une requete de connexion */
		printf("main: Thread principal : en attente d'une connexion\n");
		if (listen(hSocketEcoute,SOMAXCONN) == -1)
		{
			printf("main: Erreur sur le listen de la socket %d\n", errno);
			close(hSocketEcoute); /* Fermeture de la socket */
			exit(1);
		}
		else 
			printf("main: Listen socket OK\n");

		/* 8. Acceptation d'une connexion */
		hSocketService = acceptServ(hSocketEcoute);

		/* 9. Recherche d'une socket connectee libre */
		printf("main: Recherche d'une socket connecteee libre ...\n");
		for (j=0; j<NB_MAX_CLIENTS && hSocketServices[j] !=-1; j++);
		if (j == NB_MAX_CLIENTS)
		{
			printf("main: Plus de connexion disponible\n");
			sprintf(msgServeur,DOC);
			sendSize(hSocketService,msgServeur,MAXSTRING);
			close(hSocketService); /* Fermeture de la socket */
		}
		else
		{
			/* Il y a une connexion de libre */
			printf("main: Connexion sur la socket num. %d\n", j);
			pthread_mutex_lock(&mutexIndiceCourant);
			hSocketServices[j] = hSocketService;
			indiceCourant=j;
			pthread_mutex_unlock(&mutexIndiceCourant);
			pthread_cond_signal(&condIndiceCourant);
		}
	}while (1);
    
    
	//Pour recevoir une trame avec caractère FinTrame
	/*
	retRecv = receiveSep(hSocketService, msgServeur, finTrame);
	test = malloc(sizeof(char) * retRecv);
	strcpy(test, msgServeur);*/
	//sendSep


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
    char msgServeur[MAXSTRING];
    
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
        /*if(receiveSep(hSocketServ, msgServeur))
          printf("\nReceive sep = %s\n", msgServeur);*/
        finDialogue=0;
        do
        {
            if(receiveSep(hSocketServ, msgServeur, finTrame)==0)
            {
              printf("fctThread: Erreur sur le recv de la socket connectee : %d\n", errno);
              //exit(0);
              finDialogue=1;
              break;
            }

            printf("fctThread: Requete recue = %s\n",msgServeur);
            fflush(stdout);

            if (strcmp(msgServeur, EOC)==0)
            {
              finDialogue=1; break;
            }
            else
            {
                traiteRequete(&msgServeur, hSocketServ);
            }
            
        }while (!finDialogue);
        
        /* 3. Fin de traitement */
        pthread_mutex_lock(&mutexIndiceCourant);
        hSocketServices[iCliTraite]=-1;
        pthread_mutex_unlock(&mutexIndiceCourant);
        close (hSocketServ);
    }
}

void traiteRequete(char * req, int socket)
{
    int typeReq;
    char *chargeUtile = NULL,*str;
    char msgServeur[MAXSTRING];
    
    printf("traiteRequete: Req = %s\n", req);
    fflush(stdout);
    //On découpe la chaine pour connaitre le type de requête
    str=strtok(req,&sepCsv);//recupere avant egal
    chargeUtile=strtok(NULL,&sepCsv);//recupere apres ";"
    typeReq=atoi(str);

    printf("traiteRequete: typeReq = %d\nMessage = %s\n", typeReq, chargeUtile);
    fflush(stdout);
    
    switch(typeReq)
    {
        case 1: //login
            printf("traiteRequete: case 1: login\n");
            fflush(stdout);
            int ret = fctLogin(chargeUtile);
            if(ret == 0)
                strcpy(msgServeur, "1;OK");
            else
            {
                sprintf(msgServeur, "1;NOK%cUtilisateur ou mot de passe errone", sepTrame);
                //strcpy(msgServeur, "1;NOK;Utilisateur ou mot de passe errone");
                
            }
            printf("traiteRequete: envoi du msgServeur = %s\n", req);
            fflush(stdout);
            sendSep(socket, msgServeur, finTrame);
			printf("traiteRequete: message envoyé!\n");
    		fflush(stdout);
            break;
    }
    //--------------------------------------------------------
    //printf("ReqType: %d\nmessage: %s", &typeReq, Buf);
    //fflush(stdout);
}

void Config()
{
	int temp;

	FILE* filedesc = fopen("ServeurConf.conf","r");
	if(filedesc==NULL)
	{
		printf("Config: Erreur d'ouverture du fichier");
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
				//printf("Port_Service = %d\n", Port_Service);
			}
			else if(strcmp(str,"Port_Admin") == 0)
			{
				temp=atoi(Buf);
				Port_Admin=temp;
				//printf("Port_Admin = %d\n", Port_Admin);
			}
			else if(strcmp(str,"sep-trame") == 0)
			{
				sepTrame = Buf[0];
				//printf("sep-trame = %c\n", sepTrame);
			}
			else if(strcmp(str,"fin-trame") == 0)
			{
				finTrame = Buf[0];
				//printf("fin-trame = %c\n", finTrame);
			}
			else if(strcmp(str,"sep-csv") == 0)
			{
				sepCsv = Buf[0];
				//printf("sep-csv = %c\n", sepCsv);
			}
			else if(strcmp(str,"loginCsv") == 0)
			{
				strcpy(loginCsv, "");
				strcpy(loginCsv,Buf);
				temp = strlen(loginCsv);
				loginCsv[temp-1]=NULL;//Enlever \0
				//printf("loginCsv = %s\n", loginCsv);
			}
			else if(strcmp(str,"pedaloCsv") == 0)
			{
				strcpy(pedaloCsv, "");
				strcpy(pedaloCsv,Buf);
				temp = strlen(pedaloCsv);
				pedaloCsv[temp-1]=NULL;//Enlever \0
				//printf("pedaloCsv = %s\n", pedaloCsv);
			}
			else if(strcmp(str,"barqueCsv") == 0)
			{
				strcpy(barqueCsv, "");
				strcpy(barqueCsv,Buf);
				temp = strlen(barqueCsv);
				barqueCsv[temp-1]=NULL;//Enlever \0
				//printf("barqueCsv = %s\n", barqueCsv);
			}
			else if(strcmp(str,"pwdMaster") == 0)
			{
				strcpy(pwdMaster, "");
				strcpy(pwdMaster,Buf);
				temp = strlen(pwdMaster);
				pwdMaster[temp-1]=NULL;//Enlever \0
				//printf("pwdMaster = %s\n", pwdMaster);
			}
			else if(strcmp(str,"pwdAdmin") == 0)
			{
				strcpy(pwdAdmin, "");
				strcpy(pwdAdmin,Buf);
				temp = strlen(pwdAdmin);
				pwdAdmin[temp-1]=NULL;//Enlever \0
				//printf("pwdAdmin = %s\n", pwdAdmin);
			}
		}
	}while(read != -1);
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
    printf("HandlerQuit: Fin du serveur\n");
    fflush(stdout);
    pthread_mutex_lock(&mutexIndiceCourant);
    //Couper toutes les sockets de services
    for(i=0;i<NB_MAX_CLIENTS;i++)
    {
        printf("HandlerQuit: Coupe la socket numero : %d\n",i);
        if(hSocketServices[i]!=-1)
            close(hSocketServices[i]);
        hSocketServices[i]=-1;
    }
    pthread_mutex_unlock(&mutexIndiceCourant);
    close(hSocketEcoute);
    exit(0);
}

int fctLogin(char * chargeUtile)
{
	int temp, i;

	//loginCsv[temp-1]=NULL; //Enlever \0
	FILE* filedesc = fopen(loginCsv,"r");

	char *pswCSV = NULL,*usernameCSV = NULL, *buf = NULL;
	char *psw = NULL,*username = NULL;
	size_t len = 0;
	ssize_t read;

	username=strtok(chargeUtile,&sepTrame);
	psw=strtok(NULL,&sepTrame); //recupere apres sepTrame

	i=0;
	do
	{
		read = getline(&buf,&len,filedesc);
		if(read != -1)
		{
			usernameCSV=strtok(buf,&sepCsv); //recupere avant egal
			pswCSV=strtok(NULL,&sepCsv); //recupere apres egal
			temp = strlen(pswCSV);
			pswCSV[temp-1]=NULL; //Enlever \0

			if(strcmp(username, usernameCSV) == 0 && strcmp(psw, pswCSV) == 0)
			{
				printf("fctLogin: login OK -> return 0\n");
				fflush(stdout);
				return 0;
			}
			i++;
		}
	}while(read != -1);
	return -1;
}
