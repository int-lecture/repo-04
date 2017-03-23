# Komponenten


## Nachrichten-Verteiler

  * Versendet Nachrichten an den jeweiligen empfänger.
  * Nachrichten werden von Datenbank 1 zu Datenbank 2 versendet.
  * Chatverlauf wird auf Abruf wieder hergestellt.

## Account-Manager

  * Verwaltet die Nutzung der bereits erstellten Benutzerprofile sowie Kontakte.
  * Kontakte können gespeichert werden, um nicht erneut nach den Verbindungsinformationen zu suchen.

## Verschlüsselung

  * Sorgt dafür, dass der Zugriff auf ein Benutzerkonto nicht von dritte verwendet werden kann.
  * Benötigt Anmeldedaten zur Überprüfung der eingaben.

## Authentifizierung

  * Verbindung (Load-Balancer)
  * Überprüft ob das Benutzerprofil nicht von Fremden benutz wird (Bsp. fremde IP).
  * Sorgt dafür, dass die Verbindung von Server und Agent aufrechterhalten bleibt.
  
## Statische Inhalte

  * Verwaltung von Medien (Fotos,Filme,...)
  * Speichern und weiterversenden von Medien und Nachrichten.
  
  
## Komponenten (mehrfachverwendbar)

  * Komponenten wie Authentifizierung sowie Verschlüsselung sollten bei großen Projekten mehrfach verwendet werden um so die Sicherheit vor Dritten zu schützen.
  * Der Webserver muss sich ständig mit anderen Komponenten Verbinden, was bei mehr Usern schnell zu Probleme führen kann.
  Man sollte deshalb auch mehrerer Webservern zur Verfügung stellen.
  

## Komponenten (einzigartig)

  * Acc-Manager bedient lediglich das UI vom jeweiligen Benutzer und hat daher keine Interaktion mit der Aussenwelt.
