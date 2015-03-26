package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.solr.core.query.Query.Operator.AND;
import static uk.ac.ebi.pride.proteomes.index.model.SolrPeptiForm.*;

/**
 * @author florian@ebi.ac.uk
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface ProteomesRepository extends SolrCrudRepository<PeptiForm, String>, ProteomesRepositoryCustom {

    /*
     * Global queries, query for all records
     */

    Page<PeptiForm> findAll(Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = PeptiForm.TEXT + ":*")
    FacetPage<PeptiForm> getTaxidFacets(Pageable pageable);


    /*
     * Queries for specific fields
     */

    Page<PeptiForm> findBySequence(String sequence, Pageable pageable);

    Page<PeptiForm> findByTaxid(int taxid, Pageable pageable);

    @Query(value = PEPTIFORM_SPECIES+":?0")
    Page<PeptiForm> findBySpecies(String species, Pageable pageable);

    Page<PeptiForm> findByProteins(String proteinAcc, Pageable pageable);
    List<PeptiForm> findAllByProteins(String proteinAcc);

    Page<PeptiForm> findByMods(String mod, Pageable pageable);

    Page<PeptiForm> findByNumProteinsGreaterThan(int num, Pageable page);
    long countByNumProteinsGreaterThan(int num);

    Page<PeptiForm> findByNumProteinsLessThan(int num, Pageable page);
    long countByNumProteinsLessThan(int num);

    List<PeptiForm> findAllByUpGroups(String upGroupId);
    Page<PeptiForm> findByUpGroups(String upGroupId, Pageable pageable);

    List<PeptiForm> findAllByGeneGroups(String geneGroupId);
    Page<PeptiForm> findByGeneGroups(String geneGroupId, Pageable pageable);


    /*
     * General queries, not field specific
     */

    @Query(value = PeptiForm.TEXT + ":?0")
    Page<PeptiForm> findByQuery(String query, Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = PeptiForm.TEXT + ":?0")
    FacetPage<PeptiForm> findByQueryFacetTaxid(String query, Pageable pageable);

    @Query(value = "-"+ TEXT + ":?0")
    Page<PeptiForm> findByQueryNot(String query, Pageable pageable);

    @Query(value = TEXT+":?0", filters = PEPTIFORM_TAXID+":(?1)", defaultOperator = AND)
    Page<PeptiForm> findByQueryAndFilterTaxid(String query, Collection<Integer> taxIds, Pageable pageable);


}
