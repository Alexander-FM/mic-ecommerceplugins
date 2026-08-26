# Compose local

Estructura para levantar el proyecto en local con Docker Compose desde WSL.

## Carpetas

- `config/`: scripts SQL de arranque de MySQL.
- `secrets/`: credenciales locales, por ejemplo Google Drive.
- `scripts/`: `setup`, `up`, `down`, `tear-down`.

## Uso

Desde WSL Ubuntu:

```bash
cd /mnt/c/dev/mic-ecommerceplugins/code/compose
bash scripts/setup.sh
bash scripts/up.sh
```

Para detener sin borrar datos:

```bash
bash scripts/down.sh
```

Para borrar también la persistencia:

```bash
bash scripts/tear-down.sh
```

## Persistencia

MySQL usa un volumen nombrado `mysql-data`.

- `docker compose down` **no borra** la persistencia.
- `docker compose down -v` o `docker volume rm` **sí la borra**.
- Borrar una imagen no borra el volumen.

## Secrets locales

`./secrets/google-drive-credentials.json` debe guardarse localmente y no enviarse al repositorio. El archivo queda en el host y se monta en el contenedor; el proyecto ya tiene esa carpeta ignorada por Git para evitar commits accidentales de credenciales reales.

