package i221.repository;

import i221.entity.CaseData;
import i221.entity.VideoImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface VideoImageRepo extends JpaRepository<VideoImage, CaseData> {
    @Query(value = "select max(id) from detect_video_image where case_id = :caseId", nativeQuery = true)
    Integer findMaxIdByCaseId(@Param("caseId") int caseId);

    @Modifying
    @Transactional
    @Query(value = "delete a from detect_video_image as a inner join (select  id, my.case_id from detect_video_image as dv,  (select max(id) - 3000 as mid, case_id from detect_video_image group by case_id) as my where dv.case_id = my.case_id and dv.id < my.mid order by id   limit 100)  as outdate  on a.id = outdate.id and a.case_id = outdate.case_id", nativeQuery = true)
    void dropImagesOutDate();
}
