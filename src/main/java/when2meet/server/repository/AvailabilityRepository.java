package when2meet.server.repository;

import when2meet.server.model.Availability;
import when2meet.server.model.AvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, AvailabilityId> {
    List<Availability> findByEventID(int eventID);
}
