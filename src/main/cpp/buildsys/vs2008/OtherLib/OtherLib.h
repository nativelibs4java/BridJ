// Le bloc ifdef suivant est la façon standard de créer des macros qui facilitent l'exportation 
// à partir d'une DLL. Tous les fichiers contenus dans cette DLL sont compilés avec le symbole OTHERLIB_EXPORTS
// défini sur la ligne de commande. Ce symbole ne doit pas être défini dans les projets
// qui utilisent cette DLL. De cette manière, les autres projets dont les fichiers sources comprennent ce fichier considèrent les fonctions 
// OTHERLIB_API comme étant importées à partir d'une DLL, tandis que cette DLL considère les symboles
// définis avec cette macro comme étant exportés.
#ifdef OTHERLIB_EXPORTS
#define OTHERLIB_API __declspec(dllexport)
#else
#define OTHERLIB_API __declspec(dllimport)
#endif

// Cette classe est exportée de OtherLib.dll
class OTHERLIB_API COtherLib {
public:
	COtherLib(void);
	// TODO : ajoutez ici vos méthodes.
};

extern OTHERLIB_API int nOtherLib;

OTHERLIB_API int fnOtherLib(int, int);
