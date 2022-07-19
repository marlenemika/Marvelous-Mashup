package de.uulm.sopra.team08.server.net;

import de.uulm.sopra.team08.req.MMRequest;
import de.uulm.sopra.team08.server.util.TriConsumer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

class TestWebSocketClient extends WebSocketClient {

    private @NotNull Consumer<ServerHandshake> onOpen = s -> {};
    private @NotNull Consumer<String> onMessage = m -> {};
    private @NotNull TriConsumer<Integer, String, Boolean> onClose = (t, u, v) -> {};
    private @NotNull Consumer<Exception> onError = Assertions::fail;


    TestWebSocketClient(String host, int port) {
        super(URI.create(String.format("ws://%s:%d", host, port)));
    }


    @Override
    public void onOpen(ServerHandshake shs) {
        onOpen.accept(shs);
    }

    public void setOnOpen(@NotNull Consumer<ServerHandshake> onOpen) {
        this.onOpen = onOpen;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        System.out.println(new String(bytes.array()));
    }

    @Override
    public void onMessage(String message) {
        onMessage.accept(message);
    }

    public void setOnMessage(@NotNull Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        onClose.accept(code, reason, remote);
    }

    public void setOnClose(@NotNull TriConsumer<Integer, String, Boolean> onClose) {
        this.onClose = onClose;
    }

    @Override
    public void onError(Exception ex) {
        onError.accept(ex);
    }

    public void setOnError(@NotNull Consumer<Exception> onError) {
        this.onError = onError;
    }

    public void sendRequest(@NotNull MMRequest request) {
        if (request.getRequestType().isIngame())
            send(String.format("{messageType:\"REQUESTS\",messages:[%s]}", request.toJsonRequest()));
        else send(request.toJsonRequest());
    }

}
