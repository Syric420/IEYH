#include "SocketUtilities.h"

int receiveSize(int socket, void * struc, int size)
{
    int nbreBytesRecus, tailleMsgRecu;
    tailleMsgRecu=0;
    do
    {
        printf("receiveSize: Passage boucle de reception\n");
        if ( (nbreBytesRecus = recv(socket, ((char*)struc) + tailleMsgRecu, size-tailleMsgRecu, 0))== -1) /* pas message urgent */
        {
            printf("receiveSize: Erreur sur le recv de la socket %d\n", errno);
            close(socket); /* Fermeture de la socket */
            close(socket); /* Fermeture de la socket */
            exit(1);
        }
        else
        {
            printf("receiveSize: Taile msg recu = %d et taille attendue = %d\n", tailleMsgRecu, size);
            tailleMsgRecu += nbreBytesRecus;
        }
        printf("receiveSize: Taille msg = %d et nbreBytes = %d \n", tailleMsgRecu, nbreBytesRecus);
    }while (nbreBytesRecus != 0 && nbreBytesRecus != -1 &&tailleMsgRecu <size );
    return tailleMsgRecu;
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
        printf("receiveSep: Erreur sur le getsockopt de la socket %d\n", errno);
        exit(1);
    }
    else
    {
        //printf("getsockopt OK\n");
        //printf("Taille maximale d'un segment = %d\n", tailleS);
    }
    
    do
    {
        if ( (nbreBytesRecus = recv(socket, buf, tailleS, 0)) == -1)
        {
            printf("receiveSep: Erreur sur le recv de la socket %d\n", errno);
            close(socket); close(socket); exit(1);
        }
        else
        {
            finDetectee = marqueurRecu (buf, sep);
            memcpy((char *)msgClient + tailleMsgRecu, buf,nbreBytesRecus);
            tailleMsgRecu += nbreBytesRecus;
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
            //printf("# trouveeeeee\n");
            m[i]='\0';
            return 0;
        }   
    }
    return -1;
}

int sendSep(int socket,char * msg, char sep)
{
	char *buffer;
	//buffer = malloc((char)strlen(msg)+sizeof(char));
	printf("sendSep: strlen = %d\n",strlen(msg));
	fflush(stdout);
	buffer = malloc(sizeof(char) * strlen(msg) + sizeof(char));

	strcpy(buffer, msg);
	buffer[strlen(msg)]=sep;
	//strcat(buffer,sep);
	printf("sendSep: Msg dans sendSep = %s\n", buffer);
	fflush(stdout);
	if (send(socket, buffer, strlen(buffer), 0) == -1)
	{
		printf("sendSep: Erreur sur le send de la socket %d\n", errno);
		close(socket); /* Fermeture de la socket */
		return -1;
	}
	else 
		return 0;
}

int sendSize(int socket,void * struc, int size)
{
  if (send(socket, (char*) struc, size, 0) == -1)
  {
    printf("sendSize: Erreur sur le send de la socket %d\n", errno);
    close(socket); /* Fermeture de la socket */
    return -1;
  }
  else return 0;
}

int connectToServ(char * ip, int port)
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
        printf("connectToServ: Erreur de creation de la socket %d\n", errno);
        exit(1);
    }
    else 
	{
		printf("connectToServ: Creation de la socket OK\n");
    	fflush(stdout);
    }
    
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
        printf("connectToServ: Erreur sur connect de la socket %d\n", errno);
        close(hSocket);
        return -1;
    }
    else printf("connectToServ: Connect socket OK\n");
    
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
		printf("confSockSrv: Erreur de creation de la socket %d\n", errno);
		exit(1);
	}
	else 
	{
		printf("confSockSrv: Creation de la socket OK\n");
	}

	/* 3. Acquisition des informations sur l'ordinateur local */
	if ( (infosHost = gethostbyname(adresse))==0)
	{
		printf("confSockSrv: Erreur d'acquisition d'infos sur le host %d\n", errno);
		exit(1);
	}
	else 
		printf("confSockSrv: Acquisition infos host OK\n");
	memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);
	printf("confSockSrv: Adresse IP = %s\n",inet_ntoa(adresseIP));

	/* 4. Préparation de la structure sockaddr_in */
	memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
	adresseSocket.sin_family = AF_INET;
	adresseSocket.sin_port = htons(Port);
	memcpy(&adresseSocket.sin_addr, infosHost->h_addr, infosHost->h_length);

	/* 5. Le système prend connaissance de l'adresse et du port de la socket */
	if (bind(hSocketEcoute, (struct sockaddr *)&adresseSocket, sizeof(struct sockaddr_in)) == -1)
	{
		printf("confSockSrv: Erreur sur le bind de la socket %d\n", errno);
		exit(1);
	}
	else 
		printf("confSockSrv: Bind adresse et port socket OK\n");

	return hSocketEcoute;
}

int acceptServ(int hSocketEcoute)
{
	//struct hostent * infosHost; /*Infos sur le host : pour gethostbyname */
	struct in_addr adresseIP; /* Adresse Internet au format reseau */
	struct sockaddr_in adresseSocket;
	int tailleSockaddr_in = sizeof(struct sockaddr_in);
	int hSocketService;

	if ( (hSocketService = accept(hSocketEcoute, (struct sockaddr *)&adresseSocket, &tailleSockaddr_in)) == -1)
	{
		printf("acceptServ: Erreur sur l'accept de la socket %d\n", errno);
		close(hSocketEcoute); /* Fermeture de la socket */
		exit(1);
	}
	else 
		printf("acceptServ: Accept socket OK\n");

	return hSocketService;
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
		printf("confSockCli: Erreur de creation de la socket %d\n", errno);
		exit(1);
	}
	else 
		printf("confSockCli: Creation de la socket OK\n");

	/* 2. Acquisition des informations sur l'ordinateur distant */
	if ( (infosHost = gethostbyname(adresse))==0)
	{
	printf("confSockCli: Erreur d'acquisition d'infos sur le host distant %d\n", errno);
	exit(1);
	}
	else 
		printf("confSockCli: Acquisition infos host distant OK\n");
	memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);
	printf("confSockCli: Adresse IP = %s\n",inet_ntoa(adresseIP));

	/* 3. Préparation de la structure sockaddr_in */
	memset(&adresseSocket, 0, sizeof(struct sockaddr_in));
	adresseSocket.sin_family = AF_INET; /* Domaine */
	adresseSocket.sin_port = htons(Port);
	memcpy(&adresseSocket.sin_addr, infosHost->h_addr,infosHost->h_length);
	tailleSockaddr_in = sizeof(struct sockaddr_in);
	if (( ret = connect(hSocket, (struct sockaddr *)&adresseSocket, tailleSockaddr_in)) == -1)
	{
		printf("confSockCli: Erreur sur connect de la socket %d\n", errno);
		switch(errno)
		{
			case EBADF: 
				printf("confSockCli: EBADF - hsocket n'existe pas\n");
				break;
			default : 
				printf("confSockCli: Erreur inconnue ?\n");
		}
		close(hSocket);
		exit(1);
	}
	else 
		printf("confSockCli: Connect socket OK\n");

	return hSocket;
}

