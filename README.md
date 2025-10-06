# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Go-To-Sequence-Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGBvHkwHSWSUDTmYHQo4AAaxgrMoME2SDA8XlU1mhmACBVHDlKAAHmiNDzafy7gjySp6pqoDyySIVI7KjdnjAFKaUMBze01egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A3vI0wE1muXyVXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogFXot3sgY87nae1t+7GWoKDgcMsexHeofz1uj8fB9EKHx6jHAE-xWfN3cjwXL1fr896zfOwxJyowp6l7FovFqXtYJ+cLtn6pavAqRpLPU+wgk+8TtBAdZoFByyXAmWrvvAyCpjA4ROE4WYTBBnwwNBwLLHBCFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhQpmUvrVP6zRtF0vQGOo+RoFmHJGmsvz-BwVygRUQHPCMylzKp+h-Ds0KPHCFRbl2CBCeKGKCcJBJEmApKWRUw78rUjIsmy+lcteNJ7hUy5ihKUraDKcq+QOKgADxJu59JhfI0UoG5N4eSGHDHs+2jToaBkwGpOxzgFI5BcKmWVhe0qymWnKGVsOwpXFpQJaOcEejpUAtZZtT2eKGSqAB6HdUmXW1CMyy+Xs3yUYh9aLEsVyiWAPUSXUeEEaMU3EfMi2wReVELWMS2NgxnjeH4-heCg6AxHEiQ3Xd9m+FgK2CqB9QNNIEb8RG7QRt0PRyaoCnDHNSEjdpZnPBD6CYF1FmvrU1n2K9dlCa9jlqM5KVpaVHnZNl8Rnod81oCVvJleUwXio+1XhbVcOFJZLVtbUFipHj5TsxwKDcEeF6k3qR0U-5VP8uVtTSPzTLGgz8gRVVIvk818XpfSnNiF1a2dn1mMnoNw2gS140jMt2EYLrNS1JtWZ0U2F3Mf4KLrv42Diuq-FojAADiRoaO9TrUJJvv-UD9hGuDZOQ1p5TjczCMw1ASN68gOT+zmwvweTLmvvjEv0kTQvM5TC6qFLIX0zlitMzH8Os+rBOa1AXOuTzGujunYCZ2oGJl4FNPClXlZ9jAkc5mrrWd55AfezkSewpQ1tIhPKDz1gJtjcnE3LGvqgFg04xrwAktIBYAIzhMEgQgps8S6igbqcjNILJKAqrP5B+170aAByRpFoXBgJ0C2JRVpJk+nbbaYx96H2PkaM+l9r632WPfR+X9Pg-zGO-EAn9dqv1-nMABcwgEgIdudJiV0OAAHY3BOBQE4GIEZghwC4gANngBOQwvcYBFEtswYONtGitA6BHKO0x66KVGGvEhKBNLrWhkvOozMYAjFkUaUyyjU5UCRAeOQKBe7Z1FmsDRcw86dgLuXWoxdTyl3FuXSudNlaXkZnKROjdp7N1HFrbm7NMpGLMX5NqUsHzjwrLVIJU92an2kIvL8K8XSxJGqbHek1YGIPPvUK+N8YBgPMNbZ40D0mxOQbkihjFLoBEsPzaymx7pIASGAGpfYID1IAFIQHFH7Cs-hcGqn4eAj660vpNGZDJHoa9o4qyQlmbACBgA1KgHACA1koCmMyQokOKd4471USMeZizKArLWRsuYZ8tFfh0UiAAVl0tARjmaqQWUsk50AzkoDPhY3RKgrF7k8kyYmPJjG5wcYPWmEoOpuJcaLaJM8krAD8TPLygTMkD2phC9ca8lZnyRd4mxqg3lQDDEhDEsSViHNeas6A6LJZD1FM47FtVcWePZlrYYeLC6jj8FodERjfLPKOcs6l6zYm0vUFLZk2BeWGGxXlcsBVKWUBACK4kOK4mspnr4nWkDka+K3qUM2+Sra6u2bbfC9szqVOdl4RZDSmm2rlIgYMsBgDYHmYQPIBQ+FB3EmaxoP0-oAyBsYKGuzlFjm4HgeJ5lyi9RAFGqA-dOXWITS6t0qhk0hPpXzAWhgM1wvxfq-OHd8VprwEC3K4qK45tluiAMCtEWaqLa3MQ7d2blqgL3TN1apa5rlj0yezauUc1bTG5epqfldmLds1JEbzaDIKZOopFrRgVKAA)

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
