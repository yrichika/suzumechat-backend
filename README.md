## Checking docker image


To check if the docker image works:

```sh
./gredlew build
docker build -t suzumechat-backend .
docker run -p 8080:8080 suzumechat-backend
```

