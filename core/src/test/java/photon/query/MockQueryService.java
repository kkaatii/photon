package photon.query;

import photon.model.Owner;

import java.util.concurrent.Future;

/**
 * Created by dan on 27/09/2017.
 */
public class MockQueryService implements QueryService {
    @Override
    public void executeQuery(Owner owner, String queryString, QueryCallback callback) {

    }

    @Override
    public Future<QueryResult> executeQuery(Owner owner, String queryString) {
        return null;
    }
}