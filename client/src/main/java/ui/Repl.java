package client;


import exception.ResponseException;
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
        preClient = new PreLoginClient(serverUrl);
        postClient = new PostLoginClient(serverUrl, preClient);
        gameClient = new GameClient(serverUrl, preClient, postClient);
        currentResult = new ReplResult("h", ReplResult.State.PRELOGIN);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!currentResult.message().equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                switch (currentResult.currentState()) {
                    case PRELOGIN -> {
                        currentResult = preClient.eval(line);
                    }
                    case POSTLOGIN -> {
                        currentResult = postClient.eval(line);
                    }
                    case GAME -> {
                        currentResult = gameClient.eval(line);
                    }
                    default -> {
                        throw new ResponseException(ResponseException.Code.ClientError, "unsupported client state");
                    }
                }
                System.out.print(SET_TEXT_COLOR_BLUE + currentResult.message());
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_BG_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}