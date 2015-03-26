package uk.ac.ebi.pride.proteomes.index.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;
import uk.ac.ebi.pride.proteomes.index.repository.ProteomesRepository;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author florian@ebi.ac.uk
 */
@Service
public class ProteomesIndexService {

    @Resource
    private ProteomesRepository proteomesRepository;


    public void deleteAll() {
        this.proteomesRepository.deleteAll();
    }

    public void save(PeptiForm peptiForm) {
        this.proteomesRepository.save(peptiForm);
    }

    public void save(Collection<PeptiForm> peptiForms) {
        this.proteomesRepository.save(peptiForms);
    }

    public void delete(String id) {
        this.proteomesRepository.delete(id);
    }
}
