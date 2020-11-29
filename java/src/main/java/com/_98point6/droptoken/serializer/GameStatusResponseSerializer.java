package com._98point6.droptoken.serializer;

import com._98point6.droptoken.model.GameStatusResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class GameStatusResponseSerializer extends StdSerializer<GameStatusResponse> {
    public GameStatusResponseSerializer(Class<GameStatusResponse> t) {
        super(t);
    }

    protected GameStatusResponseSerializer(JavaType type) {
        super(type);
    }

    protected GameStatusResponseSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected GameStatusResponseSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(GameStatusResponse gameStatusResponse, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("players");
        jsonGenerator.writeStartArray();
        for(String str: gameStatusResponse.getPlayers()) {
            jsonGenerator.writeString(str);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeFieldName("moves");
        jsonGenerator.writeNumber(gameStatusResponse.getMoves());

        jsonGenerator.writeFieldName("state");
        jsonGenerator.writeNumber(gameStatusResponse.getState());

        // if game is DONE, and it is a draw we want to set {"winner" : null}
        if(gameStatusResponse.getState().equals("DONE") || StringUtils.isNotBlank(gameStatusResponse.getWinner())) {
            jsonGenerator.writeFieldName("winner");
            jsonGenerator.writeString(gameStatusResponse.getWinner());
        }

        jsonGenerator.writeEndObject();
    }
}
