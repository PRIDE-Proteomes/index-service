package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;

import java.util.Collection;

/**
 * @author florian@ebi.ac.uk
 */
public interface ProteomesRepositoryCustom {

    Page<FacetFieldEntry> getProteinCounts(int page, int size, boolean soryByIndex);
    Page<FacetFieldEntry> getProteinCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex);

    Page<FacetFieldEntry> getGeneGroupCounts(int page, int size, boolean soryByIndex);
    Page<FacetFieldEntry> getGeneGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex);
}
