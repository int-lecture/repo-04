# Schwierigkeiten und Schwächen 

## Schwierigkeiten:

1. Problem: anstatt ein Fehler kam beim öffnen einer php-Seite immer nur ein weißer Bildschirm
Lösung: display_errors = On  wurde im Code hinzugefügt. + Benutzung von http://phpcodechecker.com/ bei Klammerfehlern
2. Problem: Logindaten mussten auf diverse Seiten übertragen werden.
Lösung: Nutzung von Sessions um Variablen über mehrere Seiten zu setzen
3. Problem: Chat im iframe kann sich nicht automatisch aktualisieren.
Lösung: Durch hinzufügen eines Reoload buttons wurde das Problem unschön umgangen
4. Problem: Chat User durften nicht doppelt angelegt werden
Lösung: Nutzung einer Schleife mit fgets um Userliste durchzugehen und mit neuen User zu vergleichen
5. Problem: Dateien auf dem Server waren schreibgeschützt angelegt und konnten keine neuen Einträge speichern
Lösung: Rechte ändern per Terminal und chmod
6. Problem: Bei Benutzung von Leerzeichen wurden die Userdateien falsch angelegt Lösung: str_replace benutzt um Leerzeichen aus dem Usernamen zu löschen (nicht die beste Lösung)
7. Problem: Server wurde durch ungeordnete Textdateien zugemüllt Lösung: Speicherung und schreiben der Daten in Ordnern
8. Problem: Session konnte nicht gestoppt werde. Kein einloggen als anderer User möglich
9. Lösung: Logout Button wurde eingeführt mit session_destroy Schwächen:

–	Chats werden nicht automatisch aktualisiert
–	.Chats werden manchmal erst nach kurzer bis langer Zeit angezeigt
–	System leicht hackkbar, da Dateinamen aus Chatpartnern erstellt werden
–	Keine Zensur von anstößigen Begriffen
–	Neue Nachrichten werden nicht speziell hervorgehoben
–	Designtechnisch veraltet
–	Keine verschlüsselten Nachrichten
–	Chats können nicht gruppiert werden
–	Mehrere Nutzer können sich als der selbe User einloggen
–	runterscrollen nach langen Chats ist nach refreshen nötig
