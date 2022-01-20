## Choix techniques

### Sealed class OperationResponse

J'ai fait le choix de gérer les success et erreurs avec une sealed class afin de pouvoir typer mes erreurs 

Ca a plusieurs avantages :

- Plus facile à déboguer
- Toutes les erreurs gérées sont référencées et facile à exploiter avec un when
- J'ai l'habitude de faire ça :)
- On peut ajouter des données dans les erreurs
- De plus, je trouve ça mieux que de lever des Exceptions

Ici je l'ai fait pour gérer les règles métiers, mais on pourrait très bien utiliser le même pattern pour gérer le retour
des appels à OperationRepository. Pour gérer une multitude d'erreurs potentielles à l'appel du service :
timeout, code http, erreurs métiers (exemple : le compte n'existe pas lors d'une tentative d'opération)

### BigDecimal vs type Amount

J'ai eu quelques problèmes lors des tests de print des montants avec que des zéro après la virgule (10.000) 
Il peut aussi y avoir des problèmes de comparaison entre 10.0 et 10.00 par exemple
Pour m'éviter ce genre de problème j'aurais pu utiliser une data class Amount qui aurait géré ces cas.
Par manque de temps je préfère laisser comme ça. Je le ferais pour la prochaine fois :)

Exemple :
data class Amount(val value: BigDecimal) {
    compagnon object {
        fun build(input: String) : Amount {
            // construction en gérant les cas 
        }
    }
}

### Utilisation d'un long pour la date

Par expérience, se balader des dates dans le code n'est pas toujours une bonne idée, la zone, le format.
Je préfère utiliser l'unixtime puis laisser le choix à l'affichage de gérer la zone
Inconvénients : 
- pour déboguer c'est plus facile d'avoir de vraies dates (en plus l'API DateTime de java 8 est cool)
- lecture des logs
- lecture des champs d'une base de données
