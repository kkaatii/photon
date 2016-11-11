package photon.tube.service;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.query.FailedQueryException;
import photon.tube.query.GraphContainer;
import photon.tube.query.Query;
import photon.tube.query.QueryResult;
import photon.tube.query.processor.Processor;
import photon.tube.query.processor.ProcessorNotFoundException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a <tt>Query</tt>, it will find corresponding <tt>Processor</tt> to process it and cache the result <tt>GraphContainer</tt>
 * for the same query in the future.
 */
@Service
public class ProcessorBackedQueryService implements QueryService {

    private final Map<String, Processor> procMap = new HashMap<>();
    private final Map<Query, GraphContainer> gcStore = new HashMap<>();

    private final CrudService crudService;
    private final OafService oafService;

    @Autowired
    public ProcessorBackedQueryService(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    @Override
    public QueryResult resultOf(Query query) {
        try {
            GraphContainer gc = gcStore.computeIfAbsent(query,
                    k -> getProcessor(query.type).process(query.ownerId, query.args));
            GraphContainer sectionContainer = query.sectionConfig.applyOn(gc);
            return new QueryResult(query)
                    .withGraphInfo(gc.info())
                    .withSectionInfo(sectionContainer.info())
                    .withSection(sectionContainer.export());
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    private Processor getProcessor(String procName) {
        return procMap.computeIfAbsent(procName, k -> {
            try {
                Class<?> clazz = Class.forName(completeProcName(procName));
                Constructor<?> ctor = clazz.getConstructor(CrudService.class, OafService.class);
                return (Processor) ctor.newInstance(crudService, oafService);
            } catch (Exception e) {
                throw new ProcessorNotFoundException(e);
            }
        });
    }

    private static String completeProcName(String name) {
        return String.format("photon.tube.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }

}
