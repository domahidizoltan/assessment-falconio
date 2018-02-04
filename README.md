# Falcon.io practical skills assessment

author: Zoltan Domahidi  
date: 4th of February, 2018  


## Tasks

* A REST endpoint is taking a dummy JSON input, and the server puts the REST payload on Redis or another tool you think is well suited for the task  
* A Consumer is running in the application, taking the freshly received message and persists it in a database of your choice  
* A REST endpoint is implemented for retrieving all the messages persisted in JSON format from the database  
* The message should also be pushed through Websockets for listening browser clients at the time the message was received on the REST endpoint  
* A simple HTML page is implemented to show the real time message delivery  


## Installation

This project could be run locally by using Docker.

To install and run the application simply run the ```./install-and-run.sh``` script. This will build the project, 
create and start the containers with 2 hardcoded instance of the application.

When you try to run the application from any IDE, you will need to install the Lombok plugin. ```https://projectlombok.org/```


## Usage and details

| endpoints                                                                    | description                                             |
|------------------------------------------------------------------------------|---------------------------------------------------------|
| http://localhost:8080/messages/                                              | lists the last 20 messages                              |
| http://localhost:8080/messages/?createTime=2018-01-01T12:13:14.000Z&limit=10 | lists the last 10 messages before the given create time |
| http://locallhost:8080/messages/                                             | POST here to save a new JSON message                    |

Curl command to POST a new message:  
> curl -X POST http://localhost:8080/messages/ -H 'Content-Type: application/json' -d "{\"anyField\":\"anyValue\"}"  

You can check the messages on a MongoDB administration tool running on http://localhost:1234/. Create a connection with url ```mongodb://mongo-db:27017```  

Open the ```/client/index.html``` to see the simple UI for listing end sending messages. The UI will load the first page 
of messages from the REST endpoint. When you submit a new message, it will send it to the backend on websocket. The backend 
saves the new message and sends back the new message also on websocket to all the subscribed clients.    

When you run the containers by using docker-compose you will have 2 hardcoded applications running on 
```localhost:8010``` and ```localhost:8020```. An Nginx proxy is listening on ```localhost:8080``` and will load-balance 
the requests (you can see this in the docker logs when you hit the endpoints a couple of times). The health status of 
the applications are listed in consul on ```localhost:8500```.  

> My idea was to create a service-discovery server, what will register dynamically new applications, and serves their 
connection to the proxy what will make the load-balancing. Unfortunately I could not manage to do this, I had many 
problems with websocket settings configuration with Consul-template. Some progress could be checked on branch 
```sd-and-lb-experiment``` where I could make this by using HAproxy with a dummy application what is not using websocket 
connection.  

#### Note:  

There was a requirement to use a consumer for the messages, so the REST controller layer and the service layer could be 
scaled independently. I was thinking that using websocket here could be also possible, but there might be problems with 
delivering the same message to all the clients/threads subscribed to the given topic. Because of this I rather used an 
in-memory solution. The REST controller layer published messages in a message pool (queue) and also starts a consumer. 
The consumers are assigned to a thread pool, what's size can be configured (the default is 10). When the consumer will pick 
a message from the queue (not necessarily the one published) and will process it. When there are no more messages in the 
pool the consumer exits the thread.  
A better approach would be to use Reactive streams, but unfortunately I could not achieve a usable solution in a short 
time with Reactor.

