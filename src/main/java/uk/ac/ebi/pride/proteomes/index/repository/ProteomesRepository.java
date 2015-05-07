package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.proteomes.index.model.Peptiform;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.solr.core.query.Query.Operator.AND;
import static uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform.*;

/**
 * @author florian@ebi.ac.uk
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface ProteomesRepository extends SolrCrudRepository<Peptiform, String>, ProteomesRepositoryCustom {

    /*
     * Global queries, query for all records
     */

    Page<Peptiform> findAll(Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = Peptiform.TEXT + ":*")
    FacetPage<Peptiform> getTaxidFacets(Pageable pageable);


    /*
     * Queries for specific fields
     */

    Page<Peptiform> findBySequence(String sequence, Pageable pageable);

    Page<Peptiform> findByTaxid(int taxid, Pageable pageable);

    @Query(value = PEPTIFORM_SPECIES+":?0")
    Page<Peptiform> findBySpecies(String species, Pageable pageable);

    Page<Peptiform> findByProteins(String proteinAcc, Pageable pageable);
    List<Peptiform> findAllByProteins(String proteinAcc);

    Page<Peptiform> findByMods(String mod, Pageable pageable);

    Page<Peptiform> findByNumProteinsGreaterThan(int num, Pageable page);
    long countByNumProteinsGreaterThan(int num);

    Page<Peptiform> findByNumProteinsLessThan(int num, Pageable page);
    long countByNumProteinsLessThan(int num);

    List<Peptiform> findAllByUpGroups(String upGroupId);
    Page<Peptiform> findByUpGroups(String upGroupId, Pageable pageable);

    List<Peptiform> findAllByGeneGroups(String geneGroupId);
    Page<Peptiform> findByGeneGroups(String geneGroupId, Pageable pageable);


    /*
     * General queries, not field specific
     */

    @Query(value = Peptiform.TEXT + ":?0")
    Page<Peptiform> findByQuery(String query, Pageable pageable);

    @Facet(fields = {PEPTIFORM_TAXID}, limit = 100)
    @Query(value = Peptiform.TEXT + ":?0")
    FacetPage<Peptiform> findByQueryFacetTaxid(String query, Pageable pageable);

    @Query(value = "-"+ TEXT + ":?0")
    Page<Peptiform> findByQueryNot(String query, Pageable pageable);

    @Query(value = TEXT+":?0", filters = PEPTIFORM_TAXID+":(?1)", defaultOperator = AND)
    Page<Peptiform> findByQueryAndFilterTaxid(String query, Collection<Integer> taxIds, Pageable pageable);


}
