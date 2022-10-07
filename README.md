## Checking docker image


To check if the docker image works:

```sh
./gredlew build
docker build -t suzumechat-backend .
docker run -p 8080:8080 suzumechat-backend
```


## Environment Variable

To use Local environment variable for development, write them in `.env`.

Example:

```
ENVIRONMENT=dev
FRONT_URL=http://localhost:3000
```

this file is not used in production. This should be valid only for local development.
