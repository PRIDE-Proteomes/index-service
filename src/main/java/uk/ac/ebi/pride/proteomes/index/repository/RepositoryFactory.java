package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;

/**
 * @author florian@ebi.ac.uk
 */
public class RepositoryFactory {

    private SolrOperations solrOperations;

    public RepositoryFactory(SolrOperations solrOperations) {
        this.solrOperations = solrOperations;
    }

    public ProteomesRepository create() {
        return new SolrRepositoryFactory(this.solrOperations).getRepository(ProteomesRepository.class);
    }

}