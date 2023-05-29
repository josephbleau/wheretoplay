package dev.joey3.wheretoplay.service.aggregate;

import dev.joey3.wheretoplay.model.GameData;
import dev.joey3.wheretoplay.model.Storefront;

import java.util.Set;

public interface StorefrontService {
    Set<GameData> getAllGames();

    Storefront getStorefront();
}
