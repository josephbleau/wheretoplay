package dev.joey3.wheretoplay.service;

import dev.joey3.wheretoplay.model.GameData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class GameSearchService {

    @Autowired
    private final Analyzer analyzer;

    @Autowired
    private final Directory directory;

    public GameSearchService(final Analyzer analyzer,
                             final Directory directory) {
        this.analyzer = Objects.requireNonNull(analyzer, "analyzer");
        this.directory = Objects.requireNonNull(directory, "directory");
    }

    public Set<GameData> search(String query) {
        Set<GameData> gameData = new HashSet<>();

        try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            QueryParser queryParser = new QueryParser("name", analyzer);
            Query luceneQuery = queryParser.parse(query);

            ScoreDoc[] results = indexSearcher.search(luceneQuery, 10).scoreDocs;

            Arrays.stream(results).forEach(result -> {
                Document document = null;
                try {
                    document = indexSearcher.storedFields().document(result.doc);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                gameData.add(
                        new GameData(
                                document.getField("name").stringValue(),
                                document.getField("steamid").stringValue()
                        )
                );
            });

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return gameData;
    }
}
