# Server - Team08

## Probleme?

Bei Problemen und Fragen einfach eine Mail
an [benjamin.saur@uni-ulm.de](mailto:benjamin.saur@uni-ulm.de).

### Module:

* server
* shared

### Build:

Beide Module können über den jeweiligen `build` Task kompiliert werden. Dabei
ist zu beachten, dass das `shared` Modul vor dem `server` Modul kompiliert wird.
Anschließend kann der Server mit `server:jar` zu einer ausführbaren Jar
kompiliert werden.

### Testing:

Unser Server hat in beiden Modulen einige Tests zum validieren der Logik.
_(Alle tests waren bei der Abgabe des Servers valide, sollte es also dabei zu
Problemen kommen, könnte dies an dem lokalen Import oder dem OS liegen)_.

Zudem kann über Jacoco ein Report generiert werden:

    gradle clean test jacocoTestReport

Dieser befindet sich dann in: `build/reports/jacoco/test/html`

### Logging:

Standardmäßig wird sowohl in der Konsole als auch in Files geloggt. Es
existieren folgenende Log-Files:

* Server.log – Alles
* Team08.log – Alles aus unserem Server (ohne z.B. WebsocketServer)
* Error.log – Alle Fehler
* Network.log – Nur Netzwerk-Spezifische logs

Es existieren hierbei folgende Log-Level:

* OFF
* FATAL
* ERROR
* WARN
* INFO
* DEBUG
* TRACE
* ALL

Diese können auch über die Argumente gesetzt werden. Weitere Konfigurationen
könnt ihr in der [log4j2.xml](server/src/main/resources/log4j2.xml) vornehmen.