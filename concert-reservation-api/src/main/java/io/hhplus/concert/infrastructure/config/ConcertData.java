package io.hhplus.concert.infrastructure.config;

import io.hhplus.concert.domain.concert.ConcertRepository;
import io.hhplus.concert.domain.concert.ConcertScheduleRepository;
import io.hhplus.concert.infrastructure.entity.concert.ConcertEntity;
import io.hhplus.concert.infrastructure.entity.concert.ConcertScheduleEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConcertData implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    public ConcertData(ConcertRepository concertRepository, ConcertScheduleRepository concertScheduleRepository) {
        this.concertRepository = concertRepository;
        this.concertScheduleRepository = concertScheduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        LocalDateTime localDateTime = LocalDateTime.now();

        if(concertRepository.count() == 0) {
            String[] concertTitle = {
                    "트렌드 코리아 2025", "고양이 해결사 깜냥 7", "하루 한 장 나의 어휘력을 위한 필사 노트", "고전이 답했다 마땅히 살아야 할 삶에 대하여",
                    "언젠가 우리가 같은 별을 바라본다면", "생각의 연금술", "흔한남매 17", "시대예보 : 호명사회",
                    "영원한 천국", "너에게 들려주는 단단한 말"
            };

            long[] concertPrice = {
                    30000L, 12000L, 18000L, 12000L,
                    10000L, 15000L, 10000L, 19000L,
                    25000L, 14000L
            };

            for(int i = 0; i < 10; i++) {

                // 임시 콘서트 데이터
                ConcertEntity concertEntity = ConcertEntity.builder()
                        .concertTitle(concertTitle[i])
                        .concertPrice(concertPrice[i])
                        .build();

                ConcertEntity concert = concertRepository.save(concertEntity);

                // 임시 콘서트 스케쥴 데이터
                LocalDateTime sampleReservationDate = localDateTime.plusDays(i*5);
                ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.builder()
                        .concertId(concert.getConcertId())
                        .availableReservationDate(sampleReservationDate)
                        .maxSeatCount(150L)
                        .remainingSeatCount(0L)
                        .build();

                concertScheduleRepository.save(concertSchedule);

            }
        }
    }

}