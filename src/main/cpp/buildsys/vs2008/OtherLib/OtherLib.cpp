// OtherLib.cpp : définit les fonctions exportées pour l'application DLL.
//

#include "stdafx.h"
#include "OtherLib.h"


// Il s'agit d'un exemple de variable exportée
OTHERLIB_API int nOtherLib=0;

// Il s'agit d'un exemple de fonction exportée.
OTHERLIB_API int fnOtherLib(int a, int b)
{
	return a + b;
}

// Il s'agit du constructeur d'une classe qui a été exportée.
// consultez OtherLib.h pour la définition de la classe
COtherLib::COtherLib()
{
	return;
}
