/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "AccessMaterial.h"

int addMaterial (Material barque, char * fichier)
{

    //Test pour ajouter materiel
    
    strcpy(barque.identifiant, "001");
    strcpy(barque.categorie, "barque");
    strcpy(barque.etat, "OK");
    char * line;
    FILE* filedesc = fopen(fichier,"a+");
    if(filedesc==NULL)
    {
        printf("Création du fichier car inexistant");
        filedesc = fopen(fichier,"w");
    }
    sprintf(line, "%s;%s;%s\n",barque.identifiant, barque.categorie, barque.etat);
    /*printf("id = %s\n",barque.identifiant);
    printf("categorie = %s\n",barque.categorie);
    printf("etat = %s\n",barque.etat);
    printf("Line = %s", line);*/

    
    
    fwrite(line, sizeof(char), strlen(line), filedesc);
    fclose(filedesc);
    //--------------------------
}

 Material getMaterial (char * number, char * fichier)
 {
     
    FILE* filedesc = fopen(fichier,"r");
    if(filedesc==NULL)
    {
            printf("Config: Erreur d'ouverture du fichier");
            exit(0);
    }

    char *Buf = NULL, *id, *etat, *categorie;
    Material materiel;
    strcpy(materiel.identifiant, "NULL");
    strcpy(materiel.categorie, "NULL");
    strcpy(materiel.etat, "NULL");
    size_t len = 0;
    ssize_t read;
    do
    {
            read = getline(&Buf,&len,filedesc);
            printf("Line lue = %s\n", Buf);
            if(read != -1)
            {
                //On découpe la ligne
                id=strtok(Buf,";");//recupere avant ;
                printf("id = %s\n",id);
                if(strcmp(id,number)==0)
                {
                    //Si les identifiants correspondent
                    categorie=strtok(NULL,";");//recupere apres ;
                    etat=strtok(NULL,";");//recupere apres ;
                    
                    strcpy(materiel.identifiant, id);
                    strcpy(materiel.categorie, categorie);
                    strcpy(materiel.etat, etat);
                    

                    printf("categorie = %s\n",materiel.categorie);
                    printf("etat = %s\n",materiel.etat);
                    break;
                }
                
            }
    }while(read != -1);
    return materiel;
 }