package photon.model;

// TODO write xml mapper

public interface FavoriteLogMapper {

    FavoriteLog selectByUserId(Integer uid);

    void insert(FavoriteLog flog);

    void deleteByUserId(Integer uid);

}