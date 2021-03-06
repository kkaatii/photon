package photon.tube.model;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ArrowMapper {
    FrameArrow selectOne(Integer id);

    void insert(Arrow a);

    void insertWithTargetFrame(@Param("arrow") Arrow arrow, @Param("targetFrame") String targetFrame);

    List<FrameArrow> selectByOrigin(Integer origin);

    List<FrameArrow> selectBetween(@Param("origin") Integer origin, @Param("target") Integer target);

    List<FrameArrow> selectActive();

    List<Integer> originIdSet();

    void delete(Integer id);

    void deleteSimilar(Arrow a);

    Set<Integer> neighborIdSet(@Param("origin") Integer origin, @Param("type") ArrowType at);

    void deleteByNode(Integer id);

    void reactivateByNode(Integer id);

    void deactivateByNode(Integer id);
}
