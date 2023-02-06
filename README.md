# customers
Customers is a spring boot application which runs as a microservice to serve the below purpose.
- This api has different end points to create/update/search customers
- create a customer - /customers
- search a customer by id - /customers/{id}
- search a customer by their firstname/lastname/both - /searchByName
- search all the available customers - /customers
- update the address of a customer - /updateAddress
- All the above actions are saved in database using in-mem h2

Swagger URL
--
- Below Swagger Documentation can help to understand about the interface
- http://localhost:8080/swagger-ui/index.html#/

Technical Info
--
Softwares:
######
- Spring Boot 2.8 (JDK 11)
- Maevn
- OpenAPI 1.6.4
- log4j2
- in-memory h2 database
- lombok
- Mapstruct DTO mappers
- Prometheus
- Docker

Deployment & Run
--
Manual:
######
- Clone the repository from Github 'https://github.com/pbiyyam/customers.git'
- mvn clean install
- Run the spring boot app from target 'java -jar customers-1.0.0.jar'
- Once the application is up & running, h2 console can be accessed from 'http://localhost:8080/h2-console' with below connection details
  --
    - jdbc url : jdbc:h2:file:./customersdb
    - username: sa
    - password: password
    - db tables are pre-filled with initial data for convinience
- customers microservice is accessible on 'http://localhost:8080/'

Docker:
######
- Follow the steps below to run the application using docker
- mvn clean build
- run docker-compose up
- once you see successful logs which means microservice is up & running on port 8080
- microservice is accessible on localhost:8080/{endpoints}
- swagger is accessible on - http://localhost:8080/swagger-ui/index.html#/
- prometheus monitoring is accessible on - http://localhost:9090
- h2 console is also accessible on http://localhost:8080/h2-console with the same connection details above
