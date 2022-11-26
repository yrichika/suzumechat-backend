
## Start development environment

You need DB and Redis to start this app.
DB and Redis is included in the `docker-compose.yml`

You just need to start docker.

```sh
docker compose up
```

## Checking docker image


To check if the docker image works:

```sh
./gredlew build
docker build -t suzumechat-backend-local .
docker compose up
```


## Environment Variable

To use Local environment variable for development, write them in `.env`.

Example:

```
ENVIRONMENT=dev
FRONT_URL=http://localhost:3000
```

this file is not used in production. This should be valid only for local development.
