## Requirements

- cdk CLI installed (version 2.X)
- Java 11
- Maven (not neccesary if using IntelliJ)

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Build and Deploy

Position yourself at the root level of the project and execute:

- mvn clean package (in IntelliJ go to Maven -> Lifecycle -> package)
- cd cdk-config
- cdk diff -> just to watch the changes
- cdk deploy