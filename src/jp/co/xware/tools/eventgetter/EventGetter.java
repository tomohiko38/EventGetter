package jp.co.xware.tools.eventgetter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class EventGetter {

    /**
     * 起動.
     *
     * @param args 起動パラメータ
     */
    public static void main(final String[] args) {
        new EventGetter(args);
    }

    /**
     * コンストラクタ.
     *
     * @param args 起動パラメータ
     */
    private EventGetter(final String[] args) {
        this.execute(args);
    }

    /**
     * 主処理.
     *
     * @param args 起動パラメータ
     */
    private void execute(final String[] args) {

        // 起動パラメータのチェック
        // 3個でなかったらエラーメッセージを表示する
        if (args.length != 3) {
            System.out.println("引数には年(4桁)と月(2桁)と及びキーワードを指定してください。");
            return;
        }

        // 起動パラメータから値を取得する
        String year = args[0];    // 年
        String month = args[1];   // 月
        String keyword = args[2]; // キーワード

        // 年が数字かどうかのチェック
        int yyyy = 0;
        try {
            yyyy = Integer.parseInt(year);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            System.out.println("年は数字で指定してください。");
            return;
        }

        // 月が数字かどうかのチェック
        int mm = 0;
        try {
            mm = Integer.parseInt(month);
        } catch (NumberFormatException nfe) {
            System.out.println("月は数字で指定してください。");
            return;
        }

        // 文字列に変換
        // ゼロパディングもしておく
        String tmpMonth = "";
        if (1 <= mm && mm <= 9) {
            tmpMonth = "0" + String.valueOf(mm);
        } else if (10 <= mm && mm <= 12) {
            tmpMonth = String.valueOf(mm);
        } else {
            System.out.println("月は1〜12を指定してください。");
            return;
        }

        // Doorkeeper の情報を取得する
        String retDoorKeeper = this.communication(this.editDoorkeeperUrl(year, tmpMonth, keyword));
        System.out.println(retDoorKeeper);

        // connpass の情報を取得する
        String retConnpass = this.communication(this.editConnpass(year, tmpMonth, keyword));
        System.out.println(retConnpass);
    }

    /**
     * 指定した月の最終日を求める.
     *
     * @param year 年
     * @param month 月
     * @return 最終日(文字列)
     */
    private String getLastDayOfMonth(final int year,
                                     final int month) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        return String.valueOf(cal.getActualMaximum(Calendar.DATE));
    }

    /**
     * DoorKeeper 用の URL を作成する.
     *
     * @param year 年
     * @param month 月
     * @param keyword キーワード
     * @return URL
     */
    private String editDoorkeeperUrl(final String year,
                                     final String month,
                                     final String keyword) {

        // 月の最終日を求める
        String lastDayOfMonth = this.getLastDayOfMonth(Integer.parseInt(year),
                                                       Integer.parseInt(month));

        // URL を編集する
        String doorkeeperUrl = "https://api.doorkeeper.jp/events/?locale=ja&sort=starts_at&since="
                                    + year
                                    + "-"
                                    + month
                                    + "-01&until="
                                    + year
                                    + "-"
                                    + month
                                    + "-"
                                    + lastDayOfMonth
                                    + "&q="
                                    + keyword;

        return doorkeeperUrl;
    }

    /**
     * connpass 用の URL を編集する.
     *
     * @param year 年
     * @param month 月
     * @param keyword キーワード
     * @return URL
     */
    private String editConnpass(final String year,
                                final String month,
                                final String keyword) {

        // URL を編集する
        String connpassUrl = "https://connpass.com/api/v1/event/?keyword=" + keyword + "&ym=" + year + month + "&count=100";
        return connpassUrl;
    }

    /**
     * 通信処理.
     *
     * @param targetUrl 実際にアクセスするための URL
     * @return 取得した文字列(JSON)
     */
    private String communication(final String targetUrl) {

        HttpURLConnection con = null;
        StringBuffer result = new StringBuffer();

        try {

            URL url = new URL(targetUrl);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.connect();

            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {

                final InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                if (encoding == null) {
                    encoding = "UTF-8";
                }

                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);

                while (bufReader.ready()) {
                    result.append( bufReader.readLine());
                }

                bufReader.close();
                inReader.close();
                in.close();

            } else {
                System.out.println(status);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return result.toString();
    }

}
