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

  * 
