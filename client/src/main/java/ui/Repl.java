package client;


import exception.ResponseException;
import serverfacade.ServerFacade;
import ui.GameClient;
import ui.PostLoginClient;
import ui.PreLoginClient;
import ui.ReplResult;

import static ui.EscapeSequences.*;

import java.util.Scanner;


public class Repl {
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;
    private final GameClient gameClient;
    private ReplResult currentResult;

    public Repl(String serverUrl) throws ResponseException {
        ServerFacade server = new ServerFacade(serverUrl);
        preClient = new PreLoginClient(server);
        postClient = new PostLoginClient(server, preClient);
        gameClient = new GameClient(server, preClient, postClient, serverUrl);
        currentResult = new ReplResult("h", ReplResult.State.PRELOGIN);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(SET_TEXT_COLOR_BLUE + preClient.help().message() + RESET_TEXT_COLOR);
        while (!currentResult.message().equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                ReplResult oldResult = currentResult;
                currentResult = getResult(currentResult, line);
                System.out.print(SET_TEXT_COLOR_BLUE + currentResult.message());
                if (oldResult.currentState() != currentResult.currentState()) {
                    System.out.print(SET_TEXT_COLOR_BLUE + getResult(currentResult, "newtab").message());
                }
            } catch (Throwable e) {
                System.out.print("\n" + e.getMessage());
                printPrompt();
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private ReplResult getResult(ReplResult lastResult, String line) throws ResponseException {
        return switch (currentResult.currentState()) {
            case PRELOGIN -> preClient.eval(line);
            case POSTLOGIN -> currentResult = postClient.eval(line);
            case GAME -> currentResult = gameClient.eval(line);
            default -> {
                throw new ResponseException(ResponseException.Code.ClientError, "unsupported client state");
            }
        };
    }

}