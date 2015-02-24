package uk.ac.ebi.pride.proteomes.index.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiForm;
import uk.ac.ebi.pride.proteomes.index.repository.ProteomesRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author florian@ebi.ac.uk
 */
@Service
public class ProteomesSearchService {

    private ProteomesRepository proteomesRepository;

    public ProteomesSearchService(ProteomesRepository proteomesRepository) {
        this.proteomesRepository = proteomesRepository;
    }


    public Page<PeptiForm> findAll(Pageable pageable) {
        return proteomesRepository.findAll(pageable);
    }
    public Long countAll() {
        return proteomesRepository.count();
    }

    public Map<Integer, Long> getTaxidFacets() {
        Map<Integer, Long> facetMap = new HashMap<Integer, Long>();

        FacetPage<PeptiForm> facetPage = this.proteomesRepository.getTaxidFacets(new PageRequest(0,1));

        for (FacetFieldEntry entry : facetPage.getFacetResultPage(SolrPeptiForm.PEPTIFORM_TAXID)) {
            facetMap.put(Integer.parseInt(entry.getValue()), entry.getValueCount());
        }

        return facetMap;
    }


    public PeptiForm findById(String id) {
        return proteomesRepository.findOne(id);
    }
    // Note: countById does not make sense and existById is the same as findById != null

    public Page<PeptiForm> findBySequence(String sequence, Pageable pageable) {
        return proteomesRepository.findBySequence(sequence, pageable);
    }
    public Long countBySequence(String sequence) {
        return proteomesRepository.countBySequence(sequence);
    }

    public Page<PeptiForm> findByTaxid(int taxid, Pageable pageable) {
        return proteomesRepository.findByTaxid(taxid, pageable);
    }
    public Long countByTaxid(int taxid) {
        return proteomesRepository.countByTaxid(taxid);
    }

    public Page<PeptiForm> findBySpecies(String species, Pageable pageable) {
        return proteomesRepository.findBySpecies(species, pageable);
    }
    public Long countBySpecies(String species) {
        Page<PeptiForm> result = this.findBySpecies(species, new PageRequest(0, 1));
        return result.getTotalElements();
    }

    public Page<PeptiForm> find(String query, Pageable pageable) {
        // if we don't have a query term search for everything
        if (query == null || query.trim().isEmpty()) {
            query = "*";
        }
        return proteomesRepository.findByQuery(query, pageable);
    }
    public long count(String query) {
        Page<PeptiForm> result = this.find(query, new PageRequest(0, 1));
        return result.getTotalElements();
    }

    public Map<Integer, Long> getTaxidFacetsByQuery(String query) {
        Map<Integer, Long> facetMap = new HashMap<Integer, Long>();

        if (query == null || query.trim().isEmpty()) {
            facetMap = getTaxidFacets();
        } else {
            FacetPage<PeptiForm> facetPage = this.proteomesRepository.findByQueryFacetTaxid(query, new PageRequest(0, 1));
            for (FacetFieldEntry entry : facetPage.getFacetResultPage(SolrPeptiForm.PEPTIFORM_TAXID)) {
                facetMap.put(Integer.parseInt(entry.getValue()), entry.getValueCount());
            }
        }

        return facetMap;
    }

    public Page<PeptiForm> findNot(String query, Pageable pageable) {
        return proteomesRepository.findByQueryNot(query, pageable);
    }
    public long countNot(String query) {
        Page<PeptiForm> result = this.findNot(query, new PageRequest(0, 1));
        return result.getTotalElements();
    }


    public List<PeptiForm> findByProtein(String proteinAccession) {
        return proteomesRepository.findByProteins(proteinAccession);
    }
    public long countByProtein(String proteinAccession) {
        return this.proteomesRepository.countByProteins(proteinAccession);
    }

    public Page<PeptiForm> findByNumProteinsGreaterThan(int num, Pageable page) {
        return this.proteomesRepository.findByNumProteinsGreaterThan(num, page);
    }
    public long countByNumProteinsGreaterThan(int num) {
        return this.proteomesRepository.countByNumProteinsGreaterThan(num);
    }
    public Page<PeptiForm> findByNumProteinsLessThan(int num, Pageable page) {
        return this.proteomesRepository.findByNumProteinsLessThan(num, page);
    }
    public long countByNumProteinsLessThan(int num) {
        return this.proteomesRepository.countByNumProteinsLessThan(num);
    }


    public Page<PeptiForm> findByQueryAndFilterTaxid(String query, Collection<Integer> taxIds, Pageable pageable) {
        if (taxIds == null || taxIds.isEmpty()) {
            // no filters, use the normal query
            return this.find(query, pageable);
        } else {
            if (query == null || query.trim().isEmpty()) {
                query = "*";
            }
            return this.proteomesRepository.findByQueryAndFilterTaxid(query, taxIds, pageable);
        }
    }
    public long countByQueryAndFilterTaxid(String query, Collection<Integer> taxIds) {
        Page<PeptiForm> page = this.findByQueryAndFilterTaxid(query, taxIds, new PageRequest(0, 1));
        return page.getTotalElements();
    }

}
