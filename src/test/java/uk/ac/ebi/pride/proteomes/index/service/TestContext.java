package uk.ac.ebi.pride.proteomes.index.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author florian@ebi.ac.uk
 */
@Configuration
@EnableSolrRepositories("uk.ac.ebi.pride.proteomes.index.repository")
@ComponentScan(basePackages = {"uk.ac.ebi.pride.proteomes.index.service"})
public class TestContext {

    @Bean
    public SolrServerFactory solrServerFactory() throws IOException, SAXException, ParserConfigurationException {
        return new EmbeddedSolrServerFactory("classpath:solr");
    }

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServerFactory());
    }

}
