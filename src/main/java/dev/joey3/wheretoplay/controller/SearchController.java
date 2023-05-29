package dev.joey3.wheretoplay.controller;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.Game;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import dev.joey3.wheretoplay.service.GameSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class SearchController {
    private final SteamWebApiClient steamWebApiClient;

    private final GameSearchService gameSearchService;

    @Autowired
    public SearchController(final SteamWebApiClient steamWebApiClient, final
    GameSearchService gameSearchService) {
        this.steamWebApiClient = Objects.requireNonNull(steamWebApiClient, "steamWebApiClient");
        this.gameSearchService = Objects.requireNonNull(gameSearchService, "gameSearchService");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String viewIndex(Model model) {
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, path="/search")
    public String searchStorefronts(
            @RequestParam(name ="gameName") String gameName,
            Model model) {

        model.addAttribute("gameData", gameSearchService.search(gameName));

        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, path="/listGames")
    public String listGames(
            @RequestParam(name = "steamId") String steamId,
            Model model) throws SteamApiException {

        GetOwnedGamesRequest request =
                new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder(steamId)
                        .includeAppInfo(true)
                        .buildRequest();

        GetOwnedGames response = steamWebApiClient.processRequest(request);

        model.addAttribute(
                "games",
                response.getResponse().getGames().stream()
                        .map(Game::getName)
                        .collect(Collectors.toSet())
        );

        return "index";
    }
}
