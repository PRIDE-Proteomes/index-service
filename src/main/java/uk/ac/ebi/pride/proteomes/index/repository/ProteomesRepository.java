package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.solr.core.query.Query.Operator.AND;
import static uk.ac.ebi.pride.proteomes.index.model.SolrPeptiformFields.*;

/**
 * @author florian@ebi.ac.uk
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface ProteomesRepository extends SolrCrudRepository<SolrPeptiform, String>, ProteomesRepositoryCustom {

    /*
     * Global queries, query for all records
     */

    Page<SolrPeptiform> findAll(Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = SolrPeptiform.TEXT + ":*")
    FacetPage<SolrPeptiform> getTaxidFacets(Pageable pageable);


    /*
     * Queries for specific fields
     */

    Page<SolrPeptiform> findBySequence(String sequence, Pageable pageable);

    Page<SolrPeptiform> findByTaxid(int taxid, Pageable pageable);

    @Query(value = PEPTIFORM_SPECIES+":?0")
    Page<SolrPeptiform> findBySpecies(String species, Pageable pageable);

    Page<SolrPeptiform> findByProteinAccession(String proteinAcc, Pageable pageable);
    List<SolrPeptiform> findAllByProteinAccession(String proteinAcc);

    Page<SolrPeptiform> findByMod(String mod, Pageable pageable);

    Page<SolrPeptiform> findByNumProteinsGreaterThan(int num, Pageable page);
    long countByNumProteinsGreaterThan(int num);

    Page<SolrPeptiform> findByNumProteinsLessThan(int num, Pageable page);
    long countByNumProteinsLessThan(int num);

    List<SolrPeptiform> findAllByGeneGroup(String geneGroupId);
    Page<SolrPeptiform> findByGeneGroup(String geneGroupId, Pageable pageable);


    /*
     * General queries, not field specific
     */

    @Query(value = SolrPeptiform.TEXT + ":?0")
    Page<SolrPeptiform> findByQuery(String query, Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = SolrPeptiform.TEXT + ":?0")
    FacetPage<SolrPeptiform> findByQueryFacetTaxid(String query, Pageable pageable);

    @Query(value = "-"+ TEXT + ":?0")
    Page<SolrPeptiform> findByQueryNot(String query, Pageable pageable);

    @Query(value = TEXT+":?0", filters = PEPTIFORM_TAXID+":(?1)", defaultOperator = AND)
    Page<SolrPeptiform> findByQueryAndFilterTaxid(String query, Collection<Integer> taxIds, Pageable pageable);


}
