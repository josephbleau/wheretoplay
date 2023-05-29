package dev.joey3.wheretoplay.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneConfig {
    @Bean
    public Analyzer analyzer() {
        return new StandardAnalyzer();
    }

    @Bean
    public Directory byteBufferDirectory() {
        return new ByteBuffersDirectory();
    }

    @Bean
    public IndexWriterConfig indexWriterConfig(Analyzer analyzer) {
        return new IndexWriterConfig(analyzer);
    }
}
