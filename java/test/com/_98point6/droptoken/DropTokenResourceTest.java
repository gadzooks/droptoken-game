package com._98point6.droptoken;

import com._98point6.droptoken.model.game.GameBoard;
import com._98point6.droptoken.model.game.GameBoardImpl;
import com._98point6.droptoken.service.DropTokenService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DropTokenResourceTest {
    private static final List<String> PLAYERS = Arrays.asList("p1", "p2");
    private static final DropTokenService SERVICE = mock(DropTokenService.class);
    public static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new DropTokenResource(SERVICE))
            .build();

    @Test
//    @Ignore("ResourceExtension is being set to NULL, need to figure out why")
    public void testGetGames() {
        GameBoard game = new GameBoardImpl(PLAYERS, 4, 4);
        String[] games = new String[] {game.getId().toString()};
        when(SERVICE.getGames()).thenReturn(Arrays.asList(games));

        List<String> results = EXT.target("/drop_token").request().get(List.class);
        assertEquals(1, results.size());
        assertEquals(game.getId().toString(), results.get(0));
    }

    @Test
    public void testCreateNewGame() {
    }

    @Test
    public void testGetGameStatus() {
    }

    @Test
    public void testPostMove() {
    }

    @Test
    public void testPlayerQuit() {
    }

    @Test
    public void testGetMoves() {
    }

    @Test
    public void testGetMove() {
    }
}