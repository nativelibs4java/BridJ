#pragma once

// Les macros suivantes définissent la plateforme minimale requise. La plateforme minimale requise
// est la version de Windows, Internet Explorer etc. qui dispose des fonctionnalités nécessaires pour exécuter 
// votre application. Les macros fonctionnent en activant toutes les fonctionnalités disponibles sur les versions de la plateforme jusqu'à la 
// version spécifiée.

// Modifiez les définitions suivantes si vous devez cibler une plateforme avant celles spécifiées ci-dessous.
// Reportez-vous à MSDN pour obtenir les dernières informations sur les valeurs correspondantes pour les différentes plateformes.
#ifndef _WIN32_WINNT            // Spécifie que la plateforme minimale requise est Windows Vista.
#define _WIN32_WINNT 0x0600     // Attribuez la valeur appropriée à cet élément pour cibler d'autres versions de Windows.
#endif

