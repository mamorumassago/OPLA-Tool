package br.ufpr.dinf.gres.persistence.service;

import br.ufpr.dinf.gres.domain.entity.objectivefunctions.SDObjectiveFunction;
import br.ufpr.dinf.gres.persistence.base.BaseService;
import br.ufpr.dinf.gres.persistence.repository.SscMetricRepository;
import org.springframework.stereotype.Service;

@Service
public class SscMetricService extends BaseService<SDObjectiveFunction> {


    public SscMetricService(SscMetricRepository repository) {
        super(repository);
    }
}
