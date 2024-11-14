
# 데이터베이스 성능 분석 보고서

## 개요
MariaDB를 사용하여 콘서트 데이터를 저장하고 검색하는 시나리오에서 데이터베이스 쿼리의 성능을 개선하기 위한 단계를 설명합니다. 테스트 데이터베이스에 100만 개의 레코드를 삽입하고 다양한 쿼리를 분석하여 최적화 가능성을 평가했습니다. MariaDB가 `EXPLAIN ANALYZE`를 지원하지 않기 때문에, 인덱스 사용의 성능 영향을 추정하기 위해 `EXPLAIN`과 `SHOW PROFILE`을 사용했습니다.


## 접근 방식

1. **쿼리 수집**:
    - 애플리케이션에서 가장 빈번하게 실행되고 복잡한 쿼리를 수집했습니다. 여기에는 자주 조회되는 콘서트 제목으로 콘서트 정보를 검색하는 쿼리가 포함됩니다.

2. **인덱스 식별**:
    - 수집한 쿼리를 기반으로 인덱스가 도움이 될 수 있는 필드를 식별했습니다. 특히 `concert` 테이블에서 `concert_title` 필드는 `LIKE` 연산자로 자주 조회되므로 인덱싱 대상으로 선택되었습니다.

3. **구현**:
    - 검색 성능을 개선하기 위해 `concert_title` 필드에 인덱스를 추가했습니다. 이 인덱스의 성능 영향을 측정하기 위해 인덱스가 있는 경우와 없는 경우 모두 테스트를 수행했습니다.
    - `EXPLAIN` 및 `SHOW PROFILE`을 사용하여 성능을 측정하기 위한 테스트 코드는 다음과 같습니다:

   ```java
   // 쿼리 성능에 대한 인덱스의 영향을 측정하기 위한 간소화된 테스트 코드
   package io.hhplus.concert.comparison;

    @Autowired
    private DataSource dataSource;

    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = dataSource.getConnection();
        try (PreparedStatement dropIndex = connection.prepareStatement("DROP INDEX idx_concert_title ON concert")) {
            dropIndex.execute();
        } catch (SQLException e) {
            System.out.println("인덱스가 존재하지 않거나 삭제할 수 없습니다.");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testExplainAndProfileWithoutAndWithIndex() throws SQLException {
        String sqlQuery = "SELECT * FROM concert WHERE concert_title LIKE ?";
        String explainQuery = "EXPLAIN " + sqlQuery;

        // 프로파일링 활성화
        try (PreparedStatement profilingOn = connection.prepareStatement("SET profiling = 1")) {
            profilingOn.execute();
        }

        // 1. 인덱스 없이 EXPLAIN 실행
        System.out.println("\n인덱스 없는 실행 계획:");
        try (PreparedStatement statement = connection.prepareStatement(explainQuery)) {
            statement.setString(1, "도레미%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String table = resultSet.getString("table");
                    String key = resultSet.getString("key");
                    String rows = resultSet.getString("rows");
                    System.out.printf("테이블: %s, 사용된 키: %s, 예상 행 수: %s%n", table, key, rows);
                }
            }
        }

        // 2. 인덱스 없이 실제 쿼리 실행 및 SHOW PROFILE 결과 요약
        double totalDurationWithoutIndex = 0;
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, "도레미%");
            statement.executeQuery();
        }
        try (PreparedStatement showProfile = connection.prepareStatement("SHOW PROFILE FOR QUERY 1");
             ResultSet resultSet = showProfile.executeQuery()) {
            System.out.println("인덱스 없이 실행한 프로파일 요약:");
            while (resultSet.next()) {
                String status = resultSet.getString("Status");
                double duration = resultSet.getDouble("Duration");
                totalDurationWithoutIndex += duration;
                if (status.equals("Sending data") || status.equals("Executing")) {
                    System.out.printf("상태: %s, 소요 시간: %.6f초%n", status, duration);
                }
            }
            System.out.printf("총 소요 시간: %.6f초%n", totalDurationWithoutIndex);
        }

        // 인덱스 생성
        try (PreparedStatement createIndex = connection.prepareStatement("CREATE INDEX idx_concert_title ON concert(concert_title)")) {
            createIndex.execute();
        }

        // 3. 인덱스 적용 후 EXPLAIN 실행
        System.out.println("\n인덱스 적용 후 실행 계획:");
        try (PreparedStatement statement = connection.prepareStatement(explainQuery)) {
            statement.setString(1, "도레미%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String table = resultSet.getString("table");
                    String key = resultSet.getString("key");
                    String rows = resultSet.getString("rows");
                    System.out.printf("테이블: %s, 사용된 키: %s, 예상 행 수: %s%n", table, key, rows);
                }
            }
        }

        // 4. 인덱스 적용 후 실제 쿼리 실행 및 SHOW PROFILE 결과 요약
        double totalDurationWithIndex = 0;
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, "도레미%");
            statement.executeQuery();
        }
        try (PreparedStatement showProfile = connection.prepareStatement("SHOW PROFILE FOR QUERY 2");
             ResultSet resultSet = showProfile.executeQuery()) {
            System.out.println("인덱스 적용 후 프로파일 요약:");
            while (resultSet.next()) {
                String status = resultSet.getString("Status");
                double duration = resultSet.getDouble("Duration");
                totalDurationWithIndex += duration;
                if (status.equals("Sending data") || status.equals("Executing")) {
                    System.out.printf("상태: %s, 소요 시간: %.6f초%n", status, duration);
                }
            }
            System.out.printf("총 소요 시간: %.6f초%n", totalDurationWithIndex);
        }
    }
   ```

## 분석 및 결과

- MariaDB가 `EXPLAIN ANALYZE`를 지원하지 않기 때문에, 인덱스가 적용된 경우와 적용되지 않은 경우의 쿼리 성능을 추정하기 위해 `EXPLAIN`과 `SHOW PROFILE`을 결합하여 사용했습니다.
- `concert_title` 필드에 인덱스를 적용하면 예상 행 수가 크게 줄어들었으나, 실제 쿼리 실행 시간은 오히려 증가하는 경향이 있었습니다. 이는 MariaDB의 쿼리 최적화와 실행 방식 중 `Sending data` 단계에서 인덱스가 영향을 미칠 수 있기 때문입니다.

### 비교 요약

| 설명                  | 인덱스 없이    | 인덱스 적용 후      |
|-----------------------|-----------|--------------------|
| 실행 계획             | 전체 테이블 스캔 | 인덱스 범위 스캔    |
| 예상 행 수            | 994938   | 1                  |
| 총 소요 시간 (초)     | 0.000933  | 0.224190초          |

## 결론
`concert_title` 필드에 인덱스를 추가하여 데이터베이스가 검색 범위를 줄일 수 있음을 확인했으나, 실제 쿼리 실행 시간은 늘어날 수 있었습니다. 이는 인덱스가 데이터 검색에는 유리하지만, MariaDB의 `Sending data` 단계에서 추가적인 시간 소요가 발생할 수 있기 때문입니다.
