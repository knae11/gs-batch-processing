package inactiveuser;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where (u.updatedTime < :time) and u.status = :status")
    List<User> findByUpdatedDateBeforeAndStatusEquals(LocalDateTime time, String status);
}
