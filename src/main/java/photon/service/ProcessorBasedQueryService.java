package photon.service;

import photon.query.*;
import photon.query.processor.Processor;
import photon.query.processor.ProcessorNotFoundException;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a <tt>Query</tt>, it will find corresponding <tt>Processor</tt> to process it and cache the result <tt>GraphContainer</tt>
 * for the same query in the future.
 */

@Service
public class ProcessorBasedQueryService implements QueryService {

    private final Map<String, Processor> procMap = new HashMap<>();
    private final Map<Query, GraphContainer> gcStore = new HashMap<>();

    private CrudService crudService;

    @Autowired
    public ProcessorBasedQueryService(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public QueryResult execute(Query query) {
        try {
            GraphContainer gc = gcStore.computeIfAbsent(query,
                    k -> getProcessor(query.type).process(query.args));
            return new QueryResult(query)
                    .withInfo(gc.info())
                    .withSlice(query.sliceConfig.applyOn(gc));
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    private Processor getProcessor(String name) {
        return procMap.computeIfAbsent(name, k -> {
            try {
                Class<?> clazz = Class.forName(completeProcessorName(name));
                Constructor<?> ctor = clazz.getConstructor(CrudService.class);
                return (Processor) ctor.newInstance(crudService);
            } catch (Exception e) {
                throw new ProcessorNotFoundException(e);
            }
        });
    }

    private static String completeProcessorName(String name) {
        return String.format("photon.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }

}