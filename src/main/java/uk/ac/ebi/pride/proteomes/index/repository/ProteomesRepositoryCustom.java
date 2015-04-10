package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;

import java.util.Collection;

/**
 * @author florian@ebi.ac.uk
 */
public interface ProteomesRepositoryCustom {

    public Page<FacetFieldEntry> getProteinCounts(int page, int size, boolean soryByIndex);
    public Page<FacetFieldEntry> getProteinCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex);

    public Page<FacetFieldEntry> getUPGroupCounts(int page, int size, boolean soryByIndex);
    public Page<FacetFieldEntry> getUPGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex);

    public Page<FacetFieldEntry> getGeneGroupCounts(int page, int size, boolean soryByIndex);
    public Page<FacetFieldEntry> getGeneGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex);
}
