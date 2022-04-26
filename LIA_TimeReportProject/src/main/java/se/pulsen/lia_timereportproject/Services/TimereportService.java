package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Repositories.TimereportRepo;

@Service
public class TimereportService {
    @Autowired
    TimereportRepo timereportRepo;

    public Timereport save(Timereport timereport){
        return timereportRepo.save(timereport);
    }
}
