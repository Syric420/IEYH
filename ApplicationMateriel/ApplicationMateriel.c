#include "ApplicationMateriel.h"

int hSocket; /* Handle de la socket */

//variables pour ServeurConf
int Port_Service, Port_Admin;
char sepTrame, finTrame, sepCsv;
char pwdMaster [50], pwdAdmin[50],loginCsv[50], barqueCsv[50], pedaloCsv[50];

int main(int argc, char** argv) 
{
    Config();
    int ret, choix, logOK = 0;
    hSocket = connectToServ("127.0.0.1", Port_Service);
    if(hSocket==-1)
    {
        printf("Connexion au serveur impossible\n");
        exit(0);
    }
    char msgClient[MAXSTRING];
    
    /*RequeteStump req;
    req.type=LOGIN_OFFICER;
    strcpy(req.chargeUtile, "test");
    sendSize(hSocket,(void *)&req, LONG_STRUCT);*/

    //menu
    do
    {
        printf("\nMENU - IEYH\n\n");
        if(logOK==0) printf("1. Login\n");
        if(logOK == 1)
        {
            printf("2. Demande d'action\n");
            printf("3. Liste des actions\n");
            printf("4. Supprimer une action\n");
            printf("5. Demande de matériel\n");
        }
        printf("0. Quitter\n");
        scanf("%d",&choix);
        system("clear");
        if(choix >= 0 || choix <= 5)
        {
            switch(choix)
            {
            case 0:
                    sendSep(hSocket, EOC,finTrame);
                    sleep(0.5);
                    return (EXIT_SUCCESS);
                break;
            case 1:
                    ret = fctLogin();
                    if(ret==0)
                    {
                        //printf("main: Login réussi\n");
                        fflush(stdout);
                        logOK = 1;
                    }
                    else
                    {
                        //printf("main: Login raté\n");
                        fflush(stdout);
                        logOK = 0;
                    }
                    break;
            case 2:
                    break;
            case 3:
                    break;
            case 4:
                    break;
            case 5:
                    break;
            }
        }
    }while(1);
}

int fctLogin()
{
        char *chargeUtile = NULL,*str;
	char nomUtilisateur[50], pwd[50];
	char msgClient[MAXSTRING];
        strcpy(msgClient,"");
        strcpy(pwd,"");
        strcpy(nomUtilisateur,"");
        
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
	/*strcpy(msgClient,"1;");
        printf("fctLogin: message = %s\n", msgClient);
	strcat(msgClient, nomUtilisateur);
        printf("fctLogin: message nomUtilisateur = %s\n", msgClient);
        char sep = sepTrame;
        strcat(msgClient, &sepTrame);
	//msgClient[strlen(msgClient)]=sep; //pas de strcat car c'est une char et pas un string
        printf("fctLogin: message sepTrame = %s\n", msgClient);
	strcat(msgClient, pwd);
        printf("fctLogin: message pwd = %s\n", msgClient);

	printf("fctLogin: envoi du msgClient: %s\n", msgClient);
	fflush(stdout);*/
        sprintf(msgClient, "1;%s%c%s", nomUtilisateur, sepTrame, pwd);
	sendSep(hSocket, msgClient, finTrame);
	printf("fctLogin: message envoyé!\n");
	fflush(stdout);

	printf("fctLogin: attente d'une réponse...\n");
	fflush(stdout);
	if(receiveSep(hSocket, msgClient, finTrame)==0)
	{
            printf("fctLogin: Erreur sur le recv de la socket connectee : %d\n", errno);
            exit(0);
	}
	printf("fctLogin: Requete recue = %s\n",msgClient);
	fflush(stdout);
	if(strcmp(msgClient,"1;OK")==0)
            return 0;
        else
        {
            char sep = sepTrame;
            //On affiche le 3éme champ qui est la raison de type 
            //1;NOK&Utilisateur ou mot de passe errone
            //printf("Erreur login sepTrame : %c\n", sepTrame);
            str=strtok(msgClient,&sep);//recupere avant sepTrame
            //printf("Erreur login str : %s\n", str);
            chargeUtile=strtok(NULL,&sep);//recupere apres sepTrame
            printf("Erreur login : %s\n", chargeUtile);
            fflush(stdout);
        }
	   
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

