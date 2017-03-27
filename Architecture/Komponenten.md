# Komponenten

## Nachrichten-Verteiler

  * Versendet Nachrichten an den jeweiligen Empfänger.
  * Nachrichten werden von Datenbank 1 zu Datenbank 2 versendet.
  * Chatverlauf wird auf Abruf wieder hergestellt.
  * (Filtert verbotene Eingaben)

## Account-Manager

  * Verwaltet die Nutzung der bereits erstellten Benutzerprofile sowie Kontakte.
  * Kontakte können gespeichert werden, um nicht erneut nach den Verbindungsinformationen zu suchen.
  * Suchanfragen nach Kontakten werden bearbeitez.

## Verschlüsselung

  * Sorgt dafür, dass der Zugriff auf ein Benutzerkonto nicht von Dritten verwendet werden kann.
  * Benötigt Anmeldedaten zur Überprüfung der Eingaben.
  * Bevorzugt wird eine End to End Verschlüsselung, 

## Authentifizierung

  * Verbindung (Load-Balancer)
  * Überprüft ob das Benutzerprofil nicht von Fremden benutz wird (Bsp. fremde IP).
  * Sorgt dafür, dass die Verbindung von Server und Agent aufrechterhalten bleibt.
  * Wird benutzt beim Abgleich vom eingegeben und gespeicherten Passwort 
  
## Statische Inhalte
  * Daten welche nicht bzw. selten verändert werden.
  * Verwaltung von Medien (Fotos,Filme,...)
  * Speichern und weiterversenden von Medien und Nachrichten.
  * Speichern von Einstellungen welche selten verändert werden.
  
## Load Balancer

  * sorgt für die Verteilung der Verbindungen auf die verügbaren Webserver.
  * soll dafür sorgen, dass einzelne Webserver nicht überlastet werden.
  * verbessert die Ausfallsicherheit
  
## User Interface
  
  * Daten werden für den User anschaulich dargestellt, sodass dieser mit dem Programm interagieren kann ohne Vorkenntnisse zu haben-
  
## Komponenten (mehrfachverwendbar)

  * Komponenten wie Authentifizierung sowie Verschlüsselung sollten bei großen Projekten mehrfach verwendet werden um so die Sicherheit  vor Dritten zu schützen.
  * Der Webserver muss sich ständig mit anderen Komponenten Verbinden, was bei mehr Usern schnell zu Probleme führen kann.
  Man sollte deshalb auch mehrerer Webservern zur Verfügung stellen.
  *
  

## Komponenten (einzigartig)

  * Acc-Manager bedient lediglich das UI vom jeweiligen Benutzer und hat daher keine Interaktion mit der Aussenwelt.
  * Load Balancer müssten sehr gut aufeinander abgestimmt sein um sich nicht gegenseitig zu behindern
