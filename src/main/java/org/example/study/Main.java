package org.example.study;

import org.example.study.entities.StockEntity;
import org.example.study.utils.DatabaseUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.time.LocalDateTime;

public class Main {
    private static StockEntity[] retrieveStockData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apis.data.go.kr/1160100/service/GetKrxListedInfoService/getItemInfo?serviceKey=appkey&numOfRows=5000&pageNo=1&resultType=json"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
        JSONArray items = new JSONObject(responseBody)
                .getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

        // items가 가지는 각 JSONObject의 basDt가 달라지는 점을 확인, 달라지는 지점에서는 과거 데이터이고 여기서부터는 다시 첫 종목부터 사이클이 도는 점을 확인
        // 고로, 첫 인자의 basDt와 다른 값을 가지는 지점부터는 사용할 필요가 없음
        String mostRecentBasdt = items.getJSONObject(0).getString("basDt");
        int thresholdIndex = items.length() - 1;
        for (int i = 0; i < items.length(); i++) {
            JSONObject object = items.getJSONObject(i);
            if (!object.getString("basDt").equals(mostRecentBasdt)) {
                thresholdIndex = i - 1;
                break;
            }
        }

        StockEntity[] stocks = new StockEntity[thresholdIndex + 1];
        for (int i = 0; i < stocks.length; i++) {
            JSONObject object = items.getJSONObject(i);
            String id = object.getString("srtnCd");
            String isin = object.getString("isinCd");
            String market = object.getString("mrktCtg");
            String displayText = object.getString("itmsNm");
            StockEntity stock = new StockEntity();
            stock.setId(id);
            stock.setIsin(isin);
            stock.setMarker(market);
            stock.setDisplayText(displayText);
            stock.setCreateAt(LocalDateTime.now());
            stocks[i] = stock;
        }
        return stocks;

    }

    private static void deleteStockById(Connection connection, String id) throws SQLException {
        // 전달받은 id 로 레코드를 삭제하는 메서드 ㄱ
        final String query = """
                SELECT id, isin, market, display_text, create_at
                FROM `jdbc_study`.`stocks`
                WHERE `id` = ?
                """;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, id);
        ResultSet resultSet = stmt.executeQuery();
        //executeQuery 메서드는 SELECT 쿼리를 실행하고 결과를 가지게 되는 rESULTsET 객체를 반환한다.
        // 안타깝게도 ResultSet 객체는 배열처럼 인덱스로 접근할수는 없고, 반환되는 레코드의 개수도 알 수 없어서 next() 메서드를 호출하여, 지칭할 수 있는(Pointing) 다음 레코드가 있는지의 여부를 반환 받음과 동시에 다음 레코드를 지칭하여 각 열의 값을 가지고 올 수 있는 구조이다. 만약 next() 메서드의 호출 결과가 false 라면 호출 전 레코드가 마지막 레코드였다는 의미이고,. 처음부터 nexT() 호출 겨로가가 false 였다면, 쿼리 실행 결과 반환되는 레코드가 없었다는 읨이다.
        if (!resultSet.next()) {
            // 애초에 nexT() 메서드 호출 겨로가가 false 라는 것은 전달 받은 id 와 일치하는 레코드가 없다는 의미 임으로 null을 반환해준다.
            return null;
        }
        // !!! 위 if 문에서


    private static void insertStocks(Connection connection, StockEntity[] stocks) throws SQLException {// throws 메서드에 전가
        int lastProgress = 0;
        int done = 0;
        for (StockEntity stock : stocks) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO `jdbc_study`.`stocks` (`id`, `isin`, `market`, `display_text`, `create_at`) VALUES (?,?,?,?,?)");
            stmt.setString(1, stock.getId());
            stmt.setString(2, stock.getIsin());
            stmt.setString(3, stock.getMarker());
            stmt.setString(4, stock.getDisplayText());
            stmt.setTimestamp(5, Timestamp.valueOf(stock.getCreateAt()));
            stmt.executeUpdate(); // INSERT UPDATE DELETE
            done++;
            int progress = (done * 100) / stocks.length;
            if (progress != lastProgress) {
                System.out.println(progress + "%");
            }
            lastProgress = progress;
        }
    }


    public static void main(String[] args) {
        StockEntity[] stocks;
        try {
            System.out.println("주식 정보 받아오기시작");
            stocks = retrieveStockData();
            System.out.println("주식 정보 받아오기 성공");
        } catch (InterruptedException | IOException e) {
            System.out.println("주식 실패");
            e.printStackTrace();
            return;
        }
        Connection connection;
        try {
            System.out.println("데이터베이스 연결 시작");
            connection = DatabaseUtils.getConnection();
            System.out.println("데이터베이스 연결 성공");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("데이터베이스 연결 실패");
            e.printStackTrace();
            return;
        }
        try {
            System.out.println("- 주식 정보 삽입 시작");
            insertStocks(connection, stocks);
            System.out.println("- 주식 정보 삽입 성공");
        } catch (SQLException e) {
            System.out.println("주식 정보 삽입 실패");
            e.printStackTrace();
        }


    }

}
