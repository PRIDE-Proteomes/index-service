package uk.ac.ebi.pride.proteomes.index.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 * @author florian@ebi.ac.uk
 */
@Configuration
@EnableSolrRepositories("uk.ac.ebi.pride.proteomes.index.repository")
@ComponentScan(basePackages = {"uk.ac.ebi.pride.proteomes.index.service"})
public class TestContext {

    @Bean
    public SolrServer solrServer() {
        CoreContainer container = new CoreContainer("src/main/resources/solr");
        container.load();
        return new EmbeddedSolrServer(container, "collection1");
    }

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServer());
    }

}
