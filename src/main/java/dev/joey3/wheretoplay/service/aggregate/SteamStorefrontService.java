package dev.joey3.wheretoplay.service.aggregate;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.applist.GetAppList;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetAppListRequest;
import dev.joey3.wheretoplay.model.GameData;
import dev.joey3.wheretoplay.model.Storefront;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SteamStorefrontService implements StorefrontService {
    private final SteamWebApiClient steamWebApiClient;

    @Autowired
    public SteamStorefrontService(final SteamWebApiClient steamWebApiClient) {
        this.steamWebApiClient = steamWebApiClient;
    }

    public Set<GameData> getAllGames() {
        GetAppListRequest getAppListRequest = new GetAppListRequest.GetAppListRequestBuilder()
                .buildRequest();

        try {
            GetAppList response = steamWebApiClient.processRequest(getAppListRequest);

            return response.getApplist().getApps().stream()
                    .map(app -> new GameData(app.getName(), String.valueOf(app.getAppid())))
                    .collect(Collectors.toSet());
        } catch (SteamApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Storefront getStorefront() {
        return Storefront.STEAM;
    }
}
