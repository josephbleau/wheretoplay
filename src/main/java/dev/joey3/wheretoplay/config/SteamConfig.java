package dev.joey3.wheretoplay.config;

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SteamConfig {

    @Bean
    public SteamWebApiClient steamWebApiClient(@Value("${steam.api.key") String steamApiKey) {
        return new SteamWebApiClient.SteamWebApiClientBuilder(steamApiKey).build();
    }
}
