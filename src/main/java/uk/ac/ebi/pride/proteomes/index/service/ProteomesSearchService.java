package uk.ac.ebi.pride.proteomes.index.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiformFields;
import uk.ac.ebi.pride.proteomes.index.repository.ProteomesRepository;

import javax.annotation.Resource;
import java.util.*;

/**
 * Search service implementation based on the functionality defined in the Solr ProteomesRepository.
 *
 * Notes:
 *
 * - countByXyz methods are usually based on the corresponding findByXyz method (using the
 * statistics in the returned page) rather than using the Solr count methods. This is done
 * to keep the consistency with the find method in case the find method is customised.
 *
 * - findAllByXyz are convenience methods to retrieve all PeptiForms for a given search record
 * without having to deal with paging parameters. This is only available when result sets are
 * expected to be small.
 *
 * - most methods querying specific fields will throw an exception if unsupported parameters
 * are provided (like null or empty Strings). Methods querying the general query field will
 * return everything by default.
 *
 *
 * @author florian@ebi.ac.uk
 */
@Service
@SuppressWarnings("unused")
public class ProteomesSearchService {

    @Resource
    private ProteomesRepository proteomesRepository;


    private static final PageRequest CPR = new PageRequest(0,1); // PageRequest used for counting (page.getTotalElements())
    private static void checkTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new IllegalArgumentException("A search term is required!");
        }
    }

    /*
     * Global queries, query for all records
     */

    public Page<SolrPeptiform> findAll(Pageable pageable) {
        return proteomesRepository.findAll(pageable);
    }
    public Long countAll() {
        return this.findAll(CPR).getTotalElements();
    }

    public Map<Integer, Long> getTaxidFacets() {
        Map<Integer, Long> facetMap = new HashMap<Integer, Long>();

        FacetPage<SolrPeptiform> facetPage = this.proteomesRepository.getTaxidFacets(new PageRequest(0,1));

        for (FacetFieldEntry entry : facetPage.getFacetResultPage(SolrPeptiformFields.PEPTIFORM_TAXID)) {
            facetMap.put(Integer.parseInt(entry.getValue()), entry.getValueCount());
        }

        return facetMap;
    }


    /*
     * Queries for specific fields
     */

    public SolrPeptiform findById(String id) {
        return proteomesRepository.findOne(id);
    }
    // Note: countById does not make sense and existById is the same as findById != null

    public Page<SolrPeptiform> findBySequence(String sequence, Pageable pageable) {
        checkTerm(sequence);
        return proteomesRepository.findBySequence(sequence, pageable);
    }
    public Long countBySequence(String sequence) {
        return this.findBySequence(sequence, CPR).getTotalElements();
    }

    public Page<SolrPeptiform> findByTaxid(int taxid, Pageable pageable) {
        if (taxid < 1) {
            throw new IllegalArgumentException("TaxId needs to be positive!");
        }
        return proteomesRepository.findByTaxid(taxid, pageable);
    }
    public Long countByTaxid(int taxid) {
        return this.findByTaxid(taxid, CPR).getTotalElements();
    }

    public Page<SolrPeptiform> findBySpecies(String species, Pageable pageable) {
        checkTerm(species);
        return proteomesRepository.findBySpecies(species, pageable);
    }
    public Long countBySpecies(String species) {
        return this.findBySpecies(species, CPR).getTotalElements();
    }

    public Page<SolrPeptiform> findByProtein(String proteinAccession, Pageable pageable) {
        checkTerm(proteinAccession);
        return proteomesRepository.findByProteins(proteinAccession, pageable);
    }
    public List<SolrPeptiform> findAllByProtein(String proteinAccession) {
        checkTerm(proteinAccession);
        return proteomesRepository.findAllByProteins(proteinAccession);
    }
    public long countByProtein(String proteinAccession) {
        return this.findByProtein(proteinAccession, CPR).getTotalElements();
    }

    public Page<SolrPeptiform> findByMod(String mod, Pageable pageable) {
        checkTerm(mod);
        return this.proteomesRepository.findByMods(mod, pageable);
    }
    public long countByMod(String mod) {
        return this.findByMod(mod, CPR).getTotalElements();
    }

    public Page<SolrPeptiform> findByNumProteinsGreaterThan(int num, Pageable page) {
        return this.proteomesRepository.findByNumProteinsGreaterThan(num, page);
    }
    public long countByNumProteinsGreaterThan(int num) {
        return this.proteomesRepository.countByNumProteinsGreaterThan(num);
    }
    public Page<SolrPeptiform> findByNumProteinsLessThan(int num, Pageable page) {
        return this.proteomesRepository.findByNumProteinsLessThan(num, page);
    }
    public long countByNumProteinsLessThan(int num) {
        return this.proteomesRepository.countByNumProteinsLessThan(num);
    }

    public List<SolrPeptiform> findAllByUpGroup(String upGroupId) {
        checkTerm(upGroupId);
        return this.proteomesRepository.findAllByUpGroups(upGroupId);
    }
    public Page<SolrPeptiform> findByUpGroup(String upGroupId, Pageable pageable) {
        checkTerm(upGroupId);
        return this.proteomesRepository.findByUpGroups(upGroupId, pageable);
    }
    public long countByUpGroup(String upGroupId) {
        return this.findByUpGroup(upGroupId, CPR).getTotalElements();
    }

    public List<SolrPeptiform> findAllByGeneGroup(String geneGroupId) {
        checkTerm(geneGroupId);
        return this.proteomesRepository.findAllByGeneGroups(geneGroupId);
    }
    public Page<SolrPeptiform> findByGeneGroup(String geneGroupId, Pageable pageable) {
        checkTerm(geneGroupId);
        return this.proteomesRepository.findByGeneGroups(geneGroupId, pageable);
    }
    public long countByGeneGroup(String geneGroupId) {
        return this.findByGeneGroup(geneGroupId, CPR).getTotalElements();
    }


    /*
     * General queries, not field specific
     */

    public Page<SolrPeptiform> findByQuery(String query, Pageable pageable) {
        // if we don't have a query term return everything
        if (query == null || query.trim().isEmpty()) {
            return proteomesRepository.findAll(pageable);
        }
        return proteomesRepository.findByQuery(query, pageable);
    }
    public long countByQuery(String query) {
        return this.findByQuery(query, CPR).getTotalElements();
    }

    public Map<Integer, Long> getTaxidFacetsByQuery(String query) {
        Map<Integer, Long> facetMap = new HashMap<Integer, Long>();

        if (query == null || query.trim().isEmpty()) {
            facetMap = getTaxidFacets();
        } else {
            FacetPage<SolrPeptiform> facetPage = this.proteomesRepository.findByQueryFacetTaxid(query, new PageRequest(0, 1));
            for (FacetFieldEntry entry : facetPage.getFacetResultPage(SolrPeptiformFields.PEPTIFORM_TAXID)) {
                facetMap.put(Integer.parseInt(entry.getValue()), entry.getValueCount());
            }
        }

        return facetMap;
    }

    public Page<SolrPeptiform> findByQueryNot(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return new PageImpl<SolrPeptiform>(Collections.<SolrPeptiform>emptyList());
        }
        return proteomesRepository.findByQueryNot(query, pageable);
    }
    public long countByQueryNot(String query) {
        return this.findByQueryNot(query, CPR).getTotalElements();
    }


    public Page<SolrPeptiform> findByQueryAndFilterTaxid(String query, Collection<Integer> taxIds, Pageable pageable) {
        if (taxIds == null || taxIds.isEmpty()) {
            // no filters, use the normal query
            return this.findByQuery(query, pageable);
        } else {
            if (query == null || query.trim().isEmpty()) {
                query = "*";
            }
            return this.proteomesRepository.findByQueryAndFilterTaxid(query, taxIds, pageable);
        }
    }
    public long countByQueryAndFilterTaxid(String query, Collection<Integer> taxIds) {
        return this.findByQueryAndFilterTaxid(query, taxIds, CPR).getTotalElements();
    }

    public Page<FacetFieldEntry> getProteinCounts(int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getProteinCounts(page, size, sortByIndex);
    }
    public Page<FacetFieldEntry> getProteinCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getProteinCountsBySpecies(taxids, page, size, sortByIndex);
    }

    public Page<FacetFieldEntry> getUPGroupCounts(int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getUPGroupCounts(page, size, sortByIndex);
    }
    public Page<FacetFieldEntry> getUPGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getUPGroupCountsBySpecies(taxids, page, size, sortByIndex);
    }

    public Page<FacetFieldEntry> getGeneGroupCounts(int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getGeneGroupCounts(page, size, sortByIndex);
    }
    public Page<FacetFieldEntry> getGeneGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean sortByIndex) {
        return this.proteomesRepository.getGeneGroupCountsBySpecies(taxids, page, size, sortByIndex);
    }

}
