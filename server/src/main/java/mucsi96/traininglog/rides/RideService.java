package mucsi96.traininglog.rides;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.traininglog.api.RideStats;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideService {
  private final RideRepository activityRepository;
  private final Clock clock;

  public void saveRide(Ride ride) {
    log.info("persisting activity in db with name {}", ride.getName());
    activityRepository.save(ride);
  }

  private List<Ride> getRides(Optional<Integer> period, ZoneId zoneId) {
    ZonedDateTime endTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS)
        .plusDays(1);
    return period.map(days -> {
      ZonedDateTime startTime = ZonedDateTime.now(clock).withZoneSameInstant(zoneId).truncatedTo(ChronoUnit.DAYS)
          .minusDays(days - 1);
      log.info("Getting activities from {} to {}", startTime, endTime);
      return activityRepository.findByCreatedAtBetween(startTime, endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    }).orElseGet(() -> {
      log.info("Getting activities before {}", endTime);
      return activityRepository.findByCreatedAtBefore(endTime, Sort.by(Sort.Direction.ASC, "createdAt"));
    });
  }

  public RideStats getStats(Optional<Integer> period, ZoneId zoneId) {
    return getRides(period, zoneId).stream()
        .map(ride -> RideStats.builder()
            .calories(ride.getCalories())
            .distance(ride.getDistance())
            .elevationGain(ride.getTotalElevationGain())
            .time(ride.getMovingTime())
            .build())
        .reduce(RideStats.builder()
            .calories(0f)
            .distance(0f)
            .elevationGain(0f)
            .time(0)
            .build(),
            (rideStats1, rideStats2) -> RideStats.builder()
                .calories(rideStats1.getCalories() + rideStats2.getCalories())
                .distance(rideStats1.getDistance() + rideStats2.getDistance())
                .elevationGain(rideStats1.getElevationGain() + rideStats2.getElevationGain())
                .time(rideStats1.getTime() + rideStats2.getTime())
                .build());
  }
}
