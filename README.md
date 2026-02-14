# BonescraftRanks (Paper 1.21.1 / Java 21)

## Install
1. ZIP entpacken und in dein GitHub Repo hochladen (alles überschreiben).
2. Wichtig: In deinem Repo **alle anderen Workflows löschen** (alles unter `.github/workflows/`), sodass nur `maven.yml` übrig bleibt.
3. GitHub Actions → Artifact `bonescraft-ranks-jar` downloaden.
4. JAR in `plugins/` auf dem Server kopieren und Server starten.

## Ingame
- `/bcrank list`
- `/bcrank set <spieler> builder`
- `/bcrank set <spieler> mod`
- `/bcrank get <spieler>`
- `/bcrank reload`

## Ränge
Konfiguration in `plugins/BonescraftRanks/config.yml` unter `ranks:`.
