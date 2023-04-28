Quick start:
1. This service require OAuth2 KeyCloak provider running. Check application.yml for details.

To start KeyCloak provider go to folder \keycloak-21.0.2\bin -> kc.bat start-dev

I start with configuration file \keycloak-21.0.2\conf\keycloak.conf with:
```
hostname=localhost
http-enabled=true
http-port=8442

# we are not using https, but without these lines below KeyCloak doesn't start
https-certificate-file=<path to file .pem>
https-certificate-key-file=<path to file .pem>`
```

2. For quick demo checkout to  **"Task 3 security, OAuth2 KeyCloak authorization server"** commit.
It uses embedded H2 db.
3. Run the app
4. Run web client https://github.com/OlegSkv/ModuleSpringBoot_task3_OAuth2WebClient