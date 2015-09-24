package uk.ac.ebi.pride.proteomes.index.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;

import javax.annotation.Resource;
import java.util.*;

import static uk.ac.ebi.pride.proteomes.index.model.SolrPeptiformFields.*;

/**
 * @author florian@ebi.ac.uk
 */
@Repository
@SuppressWarnings("unused")
public class ProteomesRepositoryImpl implements ProteomesRepositoryCustom {

    @Resource
    SolrTemplate solrTemplate;

    @Override
    public Page<FacetFieldEntry> getProteinCounts(int page, int size, boolean soryByIndex) {
        return getCounts(PROTEINS, null, null, page, size, soryByIndex);
    }

    @Override
    public Page<FacetFieldEntry> getProteinCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex) {
        return getCounts(PROTEINS, PEPTIFORM_TAXID, createQueryValues(taxids), page, size, soryByIndex);
    }

    @Override
    public Page<FacetFieldEntry> getUPGroupCounts(int page, int size, boolean soryByIndex) {
        return getCounts(UP_GROUPS, null, null, page, size, soryByIndex);
    }

    @Override
    public Page<FacetFieldEntry> getUPGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex) {
        return getCounts(UP_GROUPS, PEPTIFORM_TAXID, createQueryValues(taxids), page, size, soryByIndex);
    }

    @Override
    public Page<FacetFieldEntry> getGeneGroupCounts(int page, int size, boolean soryByIndex) {
        return getCounts(GENE_GROUPS, null, null, page, size, soryByIndex);
    }

    @Override
    public Page<FacetFieldEntry> getGeneGroupCountsBySpecies(Collection<Integer> taxids, int page, int size, boolean soryByIndex) {
        return getCounts(GENE_GROUPS, PEPTIFORM_TAXID, createQueryValues(taxids), page, size, soryByIndex);
    }

    private static List<String> createQueryValues(Collection<Integer> taxids) {
        List<String> values;
        if (taxids == null) {
            values = new ArrayList<String>(0);
        } else {
            values = new ArrayList<String>(taxids.size());
            for (Integer integer : taxids) {
                values.add(integer.toString());
            }
        }
        return values;
    }

    // count method based on Solr facets
    private Page<FacetFieldEntry> getCounts(String facetField, String searchField, List<String> searchValues, int page, int size, boolean sortByIndex) {

        // ToDo: check input parameters
        // if no sensible values throw an exception
        // also check that the fields are valid (e.g. exists in the index)?

        FacetOptions options = new FacetOptions(facetField);
        options.setPageable(new PageRequest(page, size));
        if (sortByIndex) {
            options.setFacetSort(FacetOptions.FacetSort.INDEX);
        }

        Criteria criteria;
        if (searchField == null || searchField.trim().isEmpty()) {
            // if no field was specified, use the general search field TEXT
            criteria = new Criteria(TEXT);
        } else {
            criteria = new Criteria(searchField);
        }
        // searchValues cannot be null to create a valid criteria
        if (searchValues == null) {
            searchValues = new ArrayList<String>(1);
            // not needed to set a default value,
            // empty list = match all
        }
        criteria.is(searchValues);
        FacetQuery facetQuery = new SimpleFacetQuery(criteria);
        facetQuery.setFacetOptions(options);
        facetQuery.setPageRequest(new PageRequest(0, 1));

        FacetPage<SolrPeptiform> facetPage = solrTemplate.queryForFacetPage(facetQuery, SolrPeptiform.class);

        FacetOptions options2 = new FacetOptions(facetField);
        options2.setFacetLimit(10000000);
        FacetQuery facetQuery2 = facetQuery.setFacetOptions(options2);
        FacetPage<SolrPeptiform> facetPage2 = solrTemplate.queryForFacetPage(facetQuery2, SolrPeptiform.class);

        long totalCount = facetPage2.getFacetResultPage(facetField).getContent().size();


        // stats version
//        StatsOptions statsOptions = new StatsOptions()
//                .addField(facetField)
//                .setSelectiveCalcDistinct(true)
//                .setCalcDistinct(true);
//
//
//        SimpleQuery statsQuery = new SimpleQuery(criteria);
//        statsQuery.setStatsOptions(statsOptions);
//        StatsPage<Peptiform> statsPage = solrTemplate.queryForStatsPage(statsQuery, Peptiform.class);
//        FieldStatsResult priceStatResult = statsPage.getFieldStatsResult(facetField);
//        long totalCountStats = 0;
//        if (priceStatResult != null) {
//            totalCountStats = priceStatResult.getDistinctCount();
//        }
//

        // we don't want to return the full facet page, so we extract the bits we want
        Page<FacetFieldEntry> tempPage = facetPage.getFacetResultPage(facetField);
        PageRequest pageRequest = new PageRequest(tempPage.getNumber(), tempPage.getSize(), tempPage.getSort());
        // on the result page set the total count of the initial result, not of the paged facet part
        // ToDo: facetPage.getTotalElements is NOT correct, as it counts the documents, not the unique facet values
//        return new SolrResultPage<FacetFieldEntry>(tempPage.getContent(), pageRequest, tempPage.getTotalElements(), 0.0f);
        return new SolrResultPage<FacetFieldEntry>(tempPage.getContent(), pageRequest, totalCount, 0.0f);
    }


}
