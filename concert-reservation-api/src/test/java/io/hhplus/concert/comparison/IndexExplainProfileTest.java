package io.hhplus.concert.comparison;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootTest
public class IndexExplainProfileTest {

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
}