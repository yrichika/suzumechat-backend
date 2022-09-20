## Checking docker image


To check if the docker image works:

```sh
./gredlew build
docker build -t suzumechat-backend .
docker run -p 8080:8080 suzumechat-backend
```


## Environment Variable

If you are using Visual Studio Code, go to \[add\]\[Add Configuration\] to open `.vscode/launch.json` file.

In the `lanch.json`, add something like this:

```json
"configurations": [
    {
        "type": "java",
        "name": "Launch Current File",
        "request": "launch",
        "mainClass": "${file}",
        // ADD environment variables HERE!
        "env": {
            "SOME_ENV": "secret value"
        }
    },
    {
      // ....
    }
]
```
