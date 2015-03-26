package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;

import javax.annotation.Resource;

import static uk.ac.ebi.pride.proteomes.index.model.SolrPeptiForm.PROTEINS;

/**
 * @author florian@ebi.ac.uk
 */
@Repository
@SuppressWarnings("unused")
public class ProteomesRepositoryImpl implements ProteomesRepositoryCustom {

    @Resource
    SolrTemplate solrTemplate;

    @Override
    public FacetPage<PeptiForm> facetProteinsTest1(int page, int size) {
        FacetOptions options = new FacetOptions(PROTEINS)
                .setPageable(new PageRequest(page, size));

        FacetQuery query = new SimpleFacetQuery(new SimpleStringCriteria("*:*"));
        query.setFacetOptions(options);
        query.setPageRequest(new PageRequest(0, 1));

        return solrTemplate.queryForFacetPage(query, PeptiForm.class);
    }
}
