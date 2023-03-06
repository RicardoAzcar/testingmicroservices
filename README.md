# Instrucciones para el pipeline

## Paso 1:
```docker run -it [Nombre de la imagen]```

## Paso 2:

Ejecutar las instrucciones del gitlab

# Download the binary for your system
curl -L --output /usr/local/bin/gitlab-runner https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-linux-amd64

# Give it permission to execute
chmod +x /usr/local/bin/gitlab-runner

# Create a GitLab Runner user
useradd --comment 'GitLab Runner' --create-home gitlab-runner --shell /bin/bash

# Install and run as a service
gitlab-runner install --user=gitlab-runner --working-directory=/home/gitlab-runner
gitlab-runner start

gitlab-runner register --url https://gitlab.com/ --registration-token GR1348941ywFJJxHibzp2zMoD-4Hx

## Paso 3:
Verificar con el comando ```gitlab-runner verify``` que está corriendo

## Paso 4:
Cambiar a la carpeta OPT.
Correr el comando para instalar maven: ```wget https://downloads.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.tar.gz```
Descomprimir con el comando ```tar -xvf [nombre del archivo]```