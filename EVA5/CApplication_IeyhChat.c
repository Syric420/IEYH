#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <sys/types.h>
#include <sys/socket.h> /* pour les types de socket */
#include <sys/time.h> /* pour les types de socket */
#include <netdb.h>

#include <errno.h>
#include <netinet/in.h>

#include <netinet/tcp.h> /* pour la conversion adresse reseau->format dot */
#include <arpa/inet.h>
/* pour la conversion adresse reseau->format dot */
#define PORT_MULTI 26085 /* Port de la socket serveur */
#define MAXSTRING 100 /* Longueur des messages */
#define IP_MULTICAST "234.5.5.9"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

//cc -o IeyhChat CApplication_IeyhChat.c -lnsl -lpthread

void *fct_threadReception(void *);
void afficheMessage(char*);

pthread_t threadReception;
struct sockaddr_in adresseSocket;
unsigned int tailleSockaddr_in;
int hSocket; /* Handle de la socket */

int main(int argc, char** argv)
{
	struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
	struct in_addr adresseIP; /* Adresse Internet au format reseau */
	int ret; /* valeur de retour */
	char msgClient[MAXSTRING], msgServeur[MAXSTRING];
    int rep;
    
    char msgAEnvoyer[MAXSTRING];
    char buff[MAXSTRING];
    char identifiant[50];
    int cpt=0;
    struct ip_mreq mreq;
    int flagReuse = 1;
    unsigned char ttl = 5;
    
    /* 1. Création de la socket */
	hSocket = socket(AF_INET, SOCK_STREAM, 0);
	if (hSocket == -1)
	{
		printf("Erreur de creation de la socket %d\n", errno);
		exit(1);
	}
	else 
		printf("Creation de la socket OK\n");

	/* 2. Acquisition des informations sur l'ordinateur distant */
	if ( (infosHost = gethostbyname("10.59.22.101"))==0)
	{
		printf("Erreur d'acquisition d'infos sur le host distant %d\n", errno);
		exit(1);
	}
	else 
		printf("Acquisition infos host distant OK\n");
	memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);
	printf("Adresse IP = %s\n",inet_ntoa(adresseIP));

	/* 3. Préparation de la structure sockaddr_in */
	memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
	adresseSocket.sin_family = AF_INET; /* Domaine */
	adresseSocket.sin_port = htons(PORT_MULTI);
	/* conversion numéro de port au format réseau */
	memcpy(&adresseSocket.sin_addr, infosHost->h_addr,infosHost->h_length);

	/* 4. Tentative de connexion */
	tailleSockaddr_in = sizeof(struct sockaddr_in);
	if (( ret = connect(hSocket, (struct sockaddr *)&adresseSocket, tailleSockaddr_in)) == -1)
	{
		printf("Erreur sur connect de la socket %d\n", errno);
		close(hSocket);
		exit(1);
	}
	else 
		printf("Connect socket OK\n");

    do
    {
        printf("Veuillez mettre votre identifiant: \n");
        fgets(identifiant, sizeof(identifiant), stdin);
    }while(strcmp("\n", identifiant)==0);//Tant qu'il a mis juste enter on reste dans la boucle
    
    identifiant[strlen(identifiant)-1]=0;
    
    system("clear");
    printf("Bienvenue %s",identifiant);
    printf("Quand vous serez dans le chat:\n");
    printf("Taper 1 pour répondre à une question\n");
    printf("Taper 2 pour signaler un evénement\n");
    printf("Taper 3 pour quitter\n\n");
    printf("Taper ENTER une fois vous avez bien compris\n");
    getchar();
    system("clear");
    if (pthread_create(&threadReception, NULL, fct_threadReception, NULL))
	{
		perror("pthread_create");
		return EXIT_FAILURE;
    }

    do
    {
        fgets(buff, sizeof(buff), stdin);
        rep = atoi(buff);
        switch(rep)
        {
            case 1:
                printf("Entrez votre réponse ?\n");
                strcpy(msgAEnvoyer, "2@");
                strcat(msgAEnvoyer, identifiant);
                strcat(msgAEnvoyer, "> ");
                fgets(buff, sizeof(buff), stdin);
                strcat(msgAEnvoyer, buff);
                break;
            case 2:
                printf("Entrez votre événement ?\n");
                strcpy(msgAEnvoyer, "3@");
                strcat(msgAEnvoyer, identifiant);
                strcat(msgAEnvoyer, "> ");
                fgets(buff, sizeof(buff), stdin);
                strcat(msgAEnvoyer, buff);
                break;
        }
        if(strlen(buff)>1)
        {
			if(sendto(hSocket,msgAEnvoyer,strlen(msgAEnvoyer),0,(struct sockaddr *) &adresseSocket, sizeof(adresseSocket)) < 0)
			{
	        	perror("sendto");
	        	exit(1);
	  		}
            fflush(stdout);
            fflush(stdin);
        }
    }while(rep!=3);
    /* 9. Fermeture de la socket */
    close(hSocket); /* Fermeture de la socket */
    printf("Socket client fermee\n");
    return 0;
}

void *fct_threadReception(void *p)
{
    char msg[MAXSTRING];
    int nbreRecv;
    
    do
    {
        /* 6.Reception d'un message serveur */
        memset(msg, 0, MAXSTRING);
        if ((nbreRecv = recvfrom(hSocket, msg, MAXSTRING, 0, (struct sockaddr *)&adresseSocket,&tailleSockaddr_in)) == -1)
        {
            printf("Erreur sur le recvfrom de la socket %d\n", errno);
            close(hSocket); /* Fermeture de la socket */
            exit(1);
        }
        else
        {
            msg[nbreRecv+1]=0;
            afficheMessage(msg);
            fflush(stdout);
        }
    }
    while (strcmp(msg, "#FINDUCHAT#"));
    
}

void afficheMessage(char *message)
{
    char * temp;
    int type;
    temp = strtok(message, "@");
    
    type = atoi(temp);
    switch(type)
    {
        case 1:
            //Question
            temp = strtok(NULL, "@");
            printf("Question: %s\n", temp);
            break;
        case 2:
            //Réponse
            temp = strtok(NULL, "@");
            printf("Réponse: %s\n", temp);
            break;
        case 3:
            //Event
            temp = strtok(NULL, "@");
            printf("Event: %s\n", temp);
            break;
    }
}
