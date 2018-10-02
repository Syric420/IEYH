/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   AccessMaterial.h
 * Author: syric
 *
 * Created on October 1, 2018, 3:17 AM
 */

#ifndef ACCESSMATERIAL_H
#define ACCESSMATERIAL_H

#include <stdio.h>
#include <stdlib.h> /* pour exit */
#include <string.h> /* pour memcpy */

typedef struct
{
    char identifiant[4];
    char categorie[20];
    char etat[4];
} Material;

Material getMaterial (char * number, char * fichier);
int addMaterial (Material materiel, char * fichier);
int delMaterial (char * number, char * fichier);
 

#endif /* ACCESSMATERIAL_H */

