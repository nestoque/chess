# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Go-To-Sequence-Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGBvHkwHSWSUDTmYHQo4AAaxgrMoME2SDA8XlU1mhmACBVHDlKAAHmiNDzafy7gjySp6pqoDyySIVI7KjdnjAFKaUMBze01egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A3vI0wE1muXyVXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogFXot3sgY87nae1t+7GWoKDgcMsexHeofz1uj8fB9EKHx6jHAE-xWfN3cjwXL1fr896zfOwxJyowp6l7FovFqXtYJ+cLtn6pavAqRpLPU+wgk+8TtBAdZoFByyXAmWrvvAyCpjA4ROE4WYTBBnwwNBwLLHBCFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhQpmUvrVP6zRtF0vQGOo+RoFmHJGmsvz-BwVygRUQHPCMylzKp+h-Ds0KPHCFRbl2CBCeKGKCcJBJEmApKWRUw78rUjIsmy+lcteNJ7hUy5ihKUraDKcq+QOKgADxJu59JhfI0UoG5N4eSGHDHs+2jToaBkwGpOxzgFI5BcKmWVhe0qymWnKGVsOwpXFpQJaOcEejpUAtZZtT2eKGSqAB6HdUmXW1CMyy+Xs3yUYh9aLEsVyiWAPUSXUeEEaMU3EfMi2wReVELWMS2NgxnjeH4-heCg6AxHEiQ3Xd9m+FgK2CqB9QNNIEb8RG7QRt0PRyaoCnDHNSEjdpZnPBD6CYF1FmvrU1n2K9dlCa9jlqM5KVpaVHledl8Rnod81oCVvJleUwXio+1XhbVcOFJZLVtbUHXaHj5TsxwKDcEeF6k3qR0U-5VP8uVtTSPzTLGgz8gRVVIvkwjMOjaUvUvSeg3DaBLXjSMy3YRga3UM8m1ZnRTYXcx-gouu-jYOK6r8WiMAAOJGho71OubpYNB7-1A-YRrg2TkNaeU43M2rsKUEjnYo2iXs5sL8Hky5r74xL9JE0LzOUwuqhSyF9M5YrTMR-DrPxel9Kc8lrk8-Xo7IDkqdqBiReBTTwpl5WfYwKHObNXXBN597bs5HHX5m1QSIjyg09YPrY3qxNyxL6oBYNOMS8AJLSAWACM4TBIEIKbPEuooG6nIzSCySgKq9+QftW9GgAckai0XDAnRjYlFWkmT6lttpjG3rvfeRoj6n3PpfZY19b5v0+B-MYz8QCv12o-T+cwf5zD-gA6250mJXQ4AAdjcE4FATgYgRmCHALiAA2eAE5DCdxgEUE2zA-Y1C+q0DoIcw7TGropUYS8CEoE0utaG8c6jMxgCMSRRpTLyMTgvF0B45AoE7unUWawVFzCzp2HOxdPJMmJvozO4ti6lzpsrS8jM5Sx1rq1VuHMFbAG5uzTKeijF+TalLB8w8Ky1QCWPdxE9RyH2kLPOE89F6wLiWvUohs8EoDgfUM+F8YBAPMPPC2+EswZKyTAHJgQ8lnUYpdAIlh+bWU2PdJACQwD1L7BAJpAApCA4pPYVn8Jg1U3DgEfXWgI5kMkehL3DirJCWZsAIGAPUqAcAIDWSgIY5JMj-ZyK-J4uZd0RiLOWZQNZGytlzCPmor8GikQACteloD0czVSSyVnnOgJczJ0gTGaNSi3aJFiwDEx5NYpCPdqa0wlI3YAStXGvjZh42FPiPFeX8ckyFks+6igcUvJWR9InsyXi+UxgLc6jj8FodEejfJvNOas9ZXzYlYvUFLZk2BqWGHxXlcsBUTkrJAEyqAxICVxK6okl0FhUgjQNhvI2IyCmgPGeAhV9Eal2y8Ms5prStVykQMGWAwBsCLMIHkAoXDfbiX9l9H6f0AZA2MFDaOG8QDcDwPEhO5RepusNd3VFQLfV4DdKof1QScV8wFoYENAaKVjndVAUFuVWUlwjbLdEAYvGxvMUGqAndQ0pqlpGuW-TR4SuVUnaVYhUmG3yabCt-DVUkMwEAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
