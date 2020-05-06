import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PageRank {
    PageRank() {
    }

    void main() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://database-1.cs51t9p5aar9.us-east-1.rds.amazonaws.com:5432/pagerank", "postgres", "12345679");
        String[] nodes = {"site_a", "site_b", "site_c", "site_d", "site_e", "site_f", "site_g"};
        double[] hub = {1, 1, 1, 1, 1, 1, 1};
        double[] auth = new double[nodes.length];
        Map<String, Integer> map = new HashMap<>();
        int[][] aMatrix = new int[nodes.length][nodes.length];
        int ii = 0;
        for (String a : nodes) {
            //language=sql
            String sql = "SELECT * from topology where node = '" + a + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int jj = 0;
            for (String b : nodes) {
                if (map.get(a) == null) {
                    map.put(a, resultSet.getInt(b));
                } else {
                    map.put(a, map.get(a) + resultSet.getInt(b));
                }
                aMatrix[ii][jj] = resultSet.getInt(b);
                jj++;
            }
            ii++;
        }


        auth = authority(aMatrix, hub);
        for (int i = 0; i < 20; i++) {
            hub = hubbinesS(aMatrix, auth);
            auth = authority(aMatrix, hub);
            System.out.println(i + ". hub: " + Arrays.toString(hub));
            System.out.println(i + ". auth: " + Arrays.toString(auth));
        }
    }

    private double[] authority(int[][] matrix, double[] hubbiness) {
        double[] res = new double[hubbiness.length];
        for (int i = 0; i < hubbiness.length; i++) {
            double sum = 0;
            for (int j = 0; j < hubbiness.length; j++) {
                sum = sum + matrix[j][i] * hubbiness[j];
            }
            res[i] = sum;
        }
        double max = -1;
        for (double number: res) {
            if (max < number) {
                max = number;
            }
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i] / max;
        }
        return res;
    }

    private double[] hubbinesS(int[][] matrix, double[] authority) {
        double[] res = new double[authority.length];
        for (int i = 0; i < authority.length; i++) {
            double sum = 0;
            for (int j = 0; j < authority.length; j++) {
                sum = sum + matrix[i][j] * authority[j];
            }
            res[i] = sum;
        }
        double max = -1;
        for (double number: res) {
            if (max < number) {
                max = number;
            }
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i] / max;
        }
        return res;
    }
}
