# Komponenten

## Nachrichten-Verteiler

  * Versendet Nachrichten an den jeweiligen Empfänger.
  * Nachrichten werden von Datenbank 1 zu Datenbank 2 versendet.
  * Chatverlauf wird auf Abruf wieder hergestellt.

## Account-Manager

  * Verwaltet die Nutzung der bereits erstellten Benutzerprofile sowie Kontakte.
  * Kontakte können gespeichert werden, um nicht erneut nach den Verbindungsinformationen zu suchen.

## Verschlüsselung

  * Sorgt dafür, dass der Zugriff auf ein Benutzerkonto nicht von Dritten verwendet werden kann.
  * Benötigt Anmeldedaten zur Überprüfung der Eingaben.

## Authentifizierung

  * Verbindung (Load-Balancer)
  * Überprüft ob das Benutzerprofil nicht von Fremden benutz wird (Bsp. fremde IP).
  * Sorgt dafür, dass die Verbindung von Server und Agent aufrechterhalten bleibt.
  
## Statische Inhalte

  * Verwaltung von Medien (Fotos,Filme,...)
  * Speichern und weiterversenden von Medien und Nachrichten.
  
## Load Balancer

  * sorgt für die Verteilung der Verbindungen auf die Webserver.
  * soll dafür sorgen, dass einzelne Webserver nicht überlastet werden.
  
## User Interface
  
  * Daten werden für den User anschaulich dargestellt, sodass dieser mit dem Programm interagieren kann ohne Vorkenntnisse zu haben.
  
## Komponenten (mehrfachverwendbar)

  * Komponenten wie Authentifizierung sowie Verschlüsselung sollten bei großen Projekten mehrfach verwendet werden um so die Sicherheit  vor Dritten zu schützen.
  * Der Webserver muss sich ständig mit anderen Komponenten Verbinden, was bei mehr Usern schnell zu Probleme führen kann.
  Man sollte deshalb auch mehrerer Webservern zur Verfügung stellen.
  

## Komponenten (einzigartig)

  * Acc-Manager bedient lediglich das UI vom jeweiligen Benutzer und hat daher keine Interaktion mit der Aussenwelt.
