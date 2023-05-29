package dev.joey3.wheretoplay.service.aggregate;

import dev.joey3.wheretoplay.model.GameData;
import dev.joey3.wheretoplay.model.Storefront;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Populate a Lucene searchable index with all known game titles and the storefronts they're found on.
 */
@Component
public class GameDataAggregator {

    private final Set<StorefrontService> storefrontServices;
    private final Directory directory;
    private final IndexWriterConfig indexWriterConfig;

    @Autowired
    public GameDataAggregator(final Set<StorefrontService> storefrontServices,
                              final Directory directory,
                              final IndexWriterConfig indexWriterConfig) {
        this.storefrontServices = Objects.requireNonNull(storefrontServices, "storefrontServices");
        this.directory = Objects.requireNonNull(directory, "directory");
        this.indexWriterConfig = Objects.requireNonNull(indexWriterConfig, "indexWriterConfig");
    }

    @PostConstruct
    public void populateAllGamesDocument() throws IOException {
        Map<String, GameData> gameDataMap = new HashMap<>();

        // For every storefront service
        storefrontServices.forEach(storefrontService -> {
            // For every game listed on that service
            storefrontService.getAllGames().forEach(storefrontGameData -> {
                // Store Steam IDs in the Steam ID field
                if (storefrontService.getStorefront() == Storefront.STEAM) {
                    storefrontGameData.setSteamId(storefrontGameData.getSteamId());
                }

                // Create an entry in our game data map, or update it to include that services identifier for that game
                gameDataMap.putIfAbsent(storefrontGameData.getName(), storefrontGameData);
            });
        });

        // For all game entries
        try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
            gameDataMap.values().forEach(gameData -> {
                // Create a new searchable document for each containing the identifiers from each storefront
                Document document = new Document();
                document.add(new Field("name", gameData.getName(), TextField.TYPE_STORED));
                document.add(new Field("steamid", gameData.getSteamId(), TextField.TYPE_STORED));

                try {
                    indexWriter.addDocument(document);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
