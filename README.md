
# SuzumeChat (NO LONGER MAINTAINED)


## Setup Git Environment


Please do not commit .vscode/settings.json and .vscode/launch.json. Although you can adjust them to suit your local environment, please refrain from including them when committing changes.

To Prevent,

```
git update-index --assume-unchange .vscode/settings.json
git update-index --assume-unchange .vscode/launch.json
```


## Start Development Environment

You need a DB and Redis to start this app locally. Both are included in the docker-compose.yml file.

Before start the app, you need to start Docker.

```sh
docker compose up
```

## Checking docker image


To check if the Docker image works:

```sh
./gradlew build

docker build -t suzumechat-backend-local .
# if you are using mac and building it for production.
docker build --platform amd64 -t suzumechat-backend-local .
# if you have a different name for the keyset json other than plain_keyset.json.
docker build -t suzumechat-backend-local --build-arg keyset_file=[keyset file name] . 

# uncomment `suzumechat_app` section in docker-compose.yml, then
docker compose up
```

**When starting with docker-compose, the application ports are changed from 8080 to 8010 and from 8081 to 8011. 8011 is for Actuator.**


## Environment Variable

To use Local environment variable for development, write them in `.env`.

Example:

```
ENVIRONMENT=dev
FRONT_URL=http://localhost:3000
```

This file is not used in a production environment and should only be valid for your local development environment.


## Create a Tink Keyset

Google Tinkey creates an encryption keyset that is used to encrypt data in the database.
Please install Tinkey.

REFERENCE: https://github.com/google/tink/blob/master/docs/TINKEY.md#usage

### Local Development Environment

Please generate a keyset for your local development environment.
The following command will create plaintext-keyset.json in the project directory.

```bash
tinkey create-keyset --key-template AES128_GCM --out plaintext-keyset.json
```

This keyset should only be used in development. Please do not use a plaintext keyset in a production environment.


### For Production (AWS KMS)


You will also need an AWS account and the appropriate IAM permissions.
Please install the AWS CLI and configure your ID and secret access key using aws configure.

Here's an example of how to generate a keyset for a production environment.
Please specify AWS ARN with `--master-key-uri`.

```shell
tinkey create-keyset --key-template AES128_GCM --out enc_keyset.json \
--master-key-uri aws-kms://arn:aws:kms:<region>:<account-id>:key/<key-id>
```


