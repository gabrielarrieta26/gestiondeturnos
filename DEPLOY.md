# Despliegue en Fly.io

Guía para publicar **GestionTurnos** (Spring Boot + MySQL) en [Fly.io](https://fly.io).

La arquitectura queda compuesta por **dos apps** dentro de la misma organización:

| App | Qué es | Acceso |
| --- | --- | --- |
| `gestionturnos` | La aplicación Spring Boot (Docker) | Público por HTTPS |
| `gestionturnos-db` | MySQL 8.4 con volumen persistente | Solo red privada (`*.internal`) |

```
Internet ──HTTPS──> gestionturnos.fly.dev
                         │  red privada 6PN
                         └──> gestionturnos-db.internal:3306
```

---

## 1. Requisitos

- Cuenta en Fly.io y tarjeta cargada (Fly pide tarjeta para crear máquinas).
- [flyctl](https://fly.io/docs/flyctl/install/) instalado:

```powershell
# Windows (PowerShell)
iwr https://fly.io/install.ps1 -useb | iex
```

```bash
# Linux / macOS
curl -L https://fly.io/install.sh | sh
```

- Login:

```bash
fly auth login
```

- Opcional para probar la imagen localmente: Docker Desktop.

---

## 2. Crear la base de datos MySQL

> Elegí la región `gru` (San Pablo, la más cercana a Argentina). Si preferís otra,
> cambiala en **los dos** `fly.toml` y en los comandos de esta guía.

```bash
# 1) Crear la app de base de datos
fly apps create gestionturnos-db

# 2) Crear el volumen persistente (3 GB alcanza para empezar)
fly volumes create mysql_data --app gestionturnos-db --region gru --size 3

# 3) Definir contraseñas (usar valores fuertes y distintos entre sí)
fly secrets set --app gestionturnos-db \
  MYSQL_ROOT_PASSWORD="CAMBIA_ESTO_root" \
  MYSQL_PASSWORD="CAMBIA_ESTO_app"

# 4) Desplegar MySQL (una sola máquina, con volumen)
fly deploy --config deploy/mysql/fly.toml --ha=false
```

Verificar que arrancó:

```bash
fly logs --app gestionturnos-db
```

Esperá el mensaje `ready for connections`. La base **no** es accesible desde
Internet: solo desde otras apps de la organización por `gestionturnos-db.internal`.

> `MYSQL_DATABASE=gestionturnos` y `MYSQL_USER=gestionturnos` ya están definidos en
> `deploy/mysql/fly.toml`. La contraseña de ese usuario es el secret `MYSQL_PASSWORD`.

---

## 3. Desplegar la aplicación

```bash
# 1) Crear la app (si el nombre está ocupado, elegí otro y actualizá fly.toml)
fly apps create gestionturnos

# 2) Secretos de la app
fly secrets set --app gestionturnos \
  DB_PASSWORD="CAMBIA_ESTO_app" \
  ADMIN_PASSWORD="UnaClaveSeguraParaElAdmin"

# 3) Deploy (una sola máquina: las sesiones se guardan en memoria)
fly deploy --ha=false
```

`DB_PASSWORD` **debe coincidir** con el `MYSQL_PASSWORD` del paso anterior.

Al arrancar, la app crea las tablas (`ddl-auto=update`), los roles básicos y el
usuario administrador si la base está vacía. Luego abrí:

```
https://gestionturnos.fly.dev/gestionturnos
```

Usuario: el definido en `ADMIN_USERNAME` (por defecto `admin`).
Contraseña: el secret `ADMIN_PASSWORD`.

> Si no definís `ADMIN_PASSWORD`, la app genera una contraseña aleatoria y la
> muestra **una sola vez** en los logs del primer arranque:
> `fly logs --app gestionturnos`.

> Importante: la app guarda las sesiones en memoria, así que debe correr en
> **una sola máquina**. Los comandos de esta guía usan `--ha=false`. Si alguna
> vez quedaron dos, ejecutá `fly scale count 1 --app gestionturnos`.

---

## 4. Variables de entorno

Todas tienen un valor por defecto para desarrollo local; en Fly.io se configuran
en `fly.toml` (`[env]`) o como secretos (`fly secrets set`).

| Variable | Default | Descripción |
| --- | --- | --- |
| `PORT` | `8091` | Puerto HTTP interno. Debe coincidir con `internal_port`. |
| `CONTEXT_PATH` | `/gestionturnos` | Prefijo de la URL. |
| `DB_HOST` | `localhost` | Host MySQL (`gestionturnos-db.internal` en Fly). |
| `DB_PORT` | `3306` | Puerto MySQL. |
| `DB_NAME` | `test9` | Nombre de la base. En Fly: `gestionturnos`. |
| `DB_USER` | `root` | Usuario MySQL. |
| `DB_PASSWORD` | *(vacío)* | **Secret.** Contraseña MySQL. |
| `DB_SSL` | `false` | `true` si usás un MySQL externo con TLS. |
| `JPA_DDL_AUTO` | `update` | Estrategia de Hibernate. |
| `JPA_SHOW_SQL` | `false` | Log de SQL. |
| `SESSION_COOKIE_SECURE` | `false` | En Fly: `true` (HTTPS). |
| `ADMIN_USERNAME` | `admin` | Usuario administrador inicial. |
| `ADMIN_PASSWORD` | *(vacío)* | **Secret.** Contraseña inicial del admin. |
| `APP_BOOTSTRAP_ENABLED` | `true` | Crea roles/admin si la base está vacía. |
| `THYMELEAF_CACHE` | `true` | Caché de plantillas. |

---

## 5. Deploy automático desde GitHub

El workflow `.github/workflows/fly-deploy.yml` despliega la app en cada push a
`main` (o manualmente desde la pestaña **Actions**).

1. Generá un token de despliegue:

```bash
fly tokens create deploy --app gestionturnos
```

2. En GitHub: **Settings → Secrets and variables → Actions → New repository
   secret**.
   - Nombre: `FLY_API_TOKEN`
   - Valor: el token generado.

3. Subí el proyecto a GitHub (rama `main`) y el workflow se ejecuta solo.

Si el proyecto todavía no es un repositorio Git:

```bash
git init
git add .
git commit -m "Preparar despliegue en Fly.io"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/GestionTurnos.git
git push -u origin main
```

> El workflow usa `--remote-only`: compila en los builders de Fly.io, no hace
> falta Docker en el runner. La base de datos se despliega **una sola vez** a
> mano (paso 2); no está en el workflow para evitar recrearla por accidente.

Si el repositorio tiene el proyecto dentro de una subcarpeta, agregá
`working-directory` al paso de deploy o mové el `fly.toml` a la raíz del repo.

---

## 6. Migrar los datos actuales (opcional)

Si querés llevar los datos de tu MySQL local (`test9`):

```bash
# En tu máquina, con MySQL instalado
mysqldump -u root -p --no-tablespaces --single-transaction --set-gtid-purged=OFF test9 > gestionturnos.sql
```

```bash
# Abrir un túnel al MySQL de Fly (dejar esta terminal abierta)
fly proxy 3306 --app gestionturnos-db

# En otra terminal, importar
mysql -h 127.0.0.1 -P 3306 -u gestionturnos -p gestionturnos < gestionturnos.sql
```

Notas:

- El contenedor MySQL se inicializa con `lower_case_table_names=1`, así que los
  nombres de tablas del dump de Windows se restauran sin problemas de mayúsculas.
- La app vuelve a crear el usuario admin solo si no existe. Si importaste la tabla
  `usuarios`, usá las credenciales que ya tenías (por ejemplo `admin` / `Jakarta89`)
  y cambialas al ingresar.
- `TurnosSchemaMigrator` puede recrear la tabla `turnos` si detecta un `id` que no
  es `VARCHAR` (migración de UUID a id legible). Revisá los logs del primer arranque.

---

## 7. Backups

- Fly toma **snapshots automáticos diarios** del volumen (retención 7 días,
  configurada en `deploy/mysql/fly.toml`).
- Backup lógico manual:

```bash
fly proxy 3306 --app gestionturnos-db
# en otra terminal:
mysqldump -h 127.0.0.1 -P 3306 -u gestionturnos -p \
  --no-tablespaces --single-transaction --set-gtid-purged=OFF \
  gestionturnos > backup-$(date +%Y%m%d).sql
```

- Listar / restaurar snapshots:

```bash
fly volumes snapshots list <VOLUME_ID> --app gestionturnos-db
fly volumes restore <SNAPSHOT_ID> <VOLUME_ID> --app gestionturnos-db
```

---

## 8. Probar la imagen localmente (antes de desplegar)

```bash
docker compose up --build
```

Abrir <http://localhost:8091/gestionturnos> (usuario `admin`, contraseña `admin1234`).

Esto levanta un MySQL y la app con la misma configuración de producción.

---

## 9. Problemas comunes

| Síntoma | Causa / solución |
| --- | --- |
| `Error: app name already taken` | Cambiá `app` en `fly.toml`, en `deploy/mysql/fly.toml` y `DB_HOST`. |
| La app reinicia y no arranca | MySQL todavía no está listo. `fly logs -a gestionturnos-db` y luego `fly machine restart -a gestionturnos`. |
| `Communications link failure` | `DB_HOST` mal escrito o apps en organizaciones distintas. Debe ser `gestionturnos-db.internal`. |
| `Access denied for user` | `DB_PASSWORD` distinto de `MYSQL_PASSWORD`. Volvé a setear ambos secrets. |
| El healthcheck falla | Revisá `fly logs -a gestionturnos`; el endpoint es `/gestionturnos/actuator/health`. Si cambiás `CONTEXT_PATH`, actualizá el `path` en `fly.toml`. |
| Se pierde la sesión al navegar | Debe haber **una sola máquina** (`--ha=false`, `min_machines_running = 1`). |
| Error de memoria / OOM | Subí `memory` en `[[vm]]` (por ejemplo `"2gb"`). |
| Quiero servir la app en la raíz | `CONTEXT_PATH="/"` en `fly.toml` y healthcheck `path = "/actuator/health"`. |

---

## 10. Comandos útiles

```bash
fly status -a gestionturnos
fly logs -a gestionturnos
fly ssh console -a gestionturnos
fly ssh console -a gestionturnos-db        # mysql -u root -p
fly scale show -a gestionturnos
fly secrets list -a gestionturnos
fly open -a gestionturnos
fly deploy --ha=false                       # redeploy manual
```
