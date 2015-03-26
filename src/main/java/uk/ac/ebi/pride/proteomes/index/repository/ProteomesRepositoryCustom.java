package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.solr.core.query.result.FacetPage;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;

/**
 * @author florian@ebi.ac.uk
 */
public interface ProteomesRepositoryCustom {

    public FacetPage<PeptiForm> facetProteinsTest1(int page, int size);
}
