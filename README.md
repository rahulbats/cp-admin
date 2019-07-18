# CP-Admin
This project is for administering your kafka cluster using a CI/CD tool like Jenkins.

## How to run this?
This project uses Gradle and Spring Boot.
Follow these steps 
* Checkout the project into your machine and cd into the directory. 
* Build the jar using `./gradlew clean build`. 
* Copy the application.yml into a preferred location. 
* Edit the application.yml and change the Kafka connection details and Topics and ACLs according to your needs. 
* Run the jar using `java  -Dspring.config.location=<PATH TO YOUR APPLICATION YML>  -jar build/libs/admin-0.0.1-SNAPSHOT.jar`. 


## Perpetual vs One time
The jar can read the application yml and create Topics and ACLs and then shutdown.
This is useful for a CI/CD based scenario.

The jar can also run like a server where you can issue create Topics/ACLs using the REST interface.

Open the application.yml and change the setting `perpetual=true` to run it as a perpetual server. 
Set `perpetual=false` to run it in CI/CD.